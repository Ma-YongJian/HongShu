package com.hongshu.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.constant.NoteConstant;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.web.domain.dto.BrowseRecordDTO;
import com.hongshu.web.domain.entity.WebLikeOrCollect;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.entity.WebUserNoteRelation;
import com.hongshu.web.domain.vo.NoteSearchVO;
import com.hongshu.web.mapper.WebLikeOrCollectMapper;
import com.hongshu.web.mapper.WebNoteMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.mapper.WebUserNoteRelationMapper;
import com.hongshu.web.service.IWebBrowseRecordService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户
 *
 * @Author hongshu
 */
@Service
public class WebBrowseRecordServiceImpl extends ServiceImpl<WebNoteMapper, WebNote> implements IWebBrowseRecordService {

    @Autowired
    private WebUserMapper userMapper;
    @Autowired
    private WebNoteMapper noteMapper;
    @Autowired
    private WebUserNoteRelationMapper userNoteRelationMapper;
    @Autowired
    private WebLikeOrCollectMapper likeOrCollectionMapper;
    @Autowired
    private ElasticsearchClient elasticsearchClient;


    /**
     * 获取浏览记录
     */
    @Override
    public List<NoteSearchVO> getAllBrowseRecordByUser(long page, long limit, String uid) {
        List<WebUserNoteRelation> noteRelationList = userNoteRelationMapper.selectList(new QueryWrapper<WebUserNoteRelation>()
                .eq("uid", uid)
                .orderByDesc("update_time"));

        // 是否点赞
        List<WebLikeOrCollect> likeOrCollections = likeOrCollectionMapper.selectList(new QueryWrapper<WebLikeOrCollect>().eq("uid", uid).eq("type", 1));
        List<String> likeOrCollectionIds = likeOrCollections.stream().map(WebLikeOrCollect::getLikeOrCollectionId).collect(Collectors.toList());


        List<NoteSearchVO> noteSearchVOList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(noteRelationList)) {

            Set<String> nids = noteRelationList.stream().map(WebUserNoteRelation::getNid).collect(Collectors.toSet());
            Map<String, WebNote> noteMap = noteMapper.selectBatchIds(nids).stream().collect(Collectors.toMap(WebNote::getId, note -> note));

            noteRelationList.forEach(item -> {
                WebNote note = noteMap.get(item.getNid());
                WebUser user = userMapper.selectById(note.getUid());
                NoteSearchVO noteSearchVo = ConvertUtils.sourceToTarget(note, NoteSearchVO.class);
                noteSearchVo.setAvatar(user.getAvatar());
                noteSearchVo.setIsLike(likeOrCollectionIds.contains(note.getId()));
                noteSearchVo.setUsername(user.getUsername());
                noteSearchVOList.add(noteSearchVo);
            });
        }
        return noteSearchVOList;
    }

    /**
     * 添加浏览记录
     */
    @Override
    @Transactional
    public void addBrowseRecord(BrowseRecordDTO browseRecordDTO) {
        WebUserNoteRelation uerNoteRelation;
        uerNoteRelation = userNoteRelationMapper.selectOne(new QueryWrapper<WebUserNoteRelation>()
                .eq("uid", browseRecordDTO.getUid())
                .eq("nid", browseRecordDTO.getNid()));
        if (ObjectUtils.isEmpty(uerNoteRelation)) {
            // 新增浏览记录
            uerNoteRelation = new WebUserNoteRelation();
            uerNoteRelation.setUid(browseRecordDTO.getUid());
            uerNoteRelation.setNid(browseRecordDTO.getNid());
            uerNoteRelation.setCreateTime(new Date());
            uerNoteRelation.setUpdateTime(new Date());
            userNoteRelationMapper.insert(uerNoteRelation);
        } else {
            // 更新浏览记录
            uerNoteRelation.setUpdateTime(new Date());
            userNoteRelationMapper.updateById(uerNoteRelation);
        }
        // 更新浏览数量
        WebNote note = noteMapper.selectOne(new QueryWrapper<WebNote>()
                .eq("id", browseRecordDTO.getNid()));
        note.setViewCount(note.getViewCount() + 1);
        noteMapper.updateById(note);
        // 更新ES数据
        this.updateEsNote(browseRecordDTO);
    }

    /**
     * 更新ES浏览记录
     */
    private void updateEsNote(BrowseRecordDTO browseRecordDTO) {
        try {
            // Step 1: 获取现有的数据
            GetResponse<NoteSearchVO> getResponse = elasticsearchClient.get(g ->
                            g.index(NoteConstant.NOTE_INDEX)
                                    .id(browseRecordDTO.getNid()),
                    NoteSearchVO.class);
            // 检查是否获取到了数据
            if (getResponse.found()) {
                NoteSearchVO noteSearchVo = getResponse.source();
                // Step 2: 更新 viewCount 字段
                noteSearchVo.setViewCount(noteSearchVo.getViewCount() + 1);
                // Step 3: 将更新后的数据保存回 Elasticsearch
                UpdateResponse<NoteSearchVO> updateResponse = elasticsearchClient.update(u ->
                                u.index(NoteConstant.NOTE_INDEX)
                                        .id(browseRecordDTO.getNid())
                                        .doc(noteSearchVo),
                        NoteSearchVO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除浏览记录
     */
    @Override
    public void delRecord(String uid, List<String> idList) {
        // 根据 uid 查询出所有的数据
        List<WebUserNoteRelation> userNoteRelations = userNoteRelationMapper.selectList(
                new QueryWrapper<WebUserNoteRelation>().eq("uid", uid)
        );
        // 筛选出需要删除的数据
        List<String> idsToDelete = userNoteRelations.stream()
                .filter(relation -> idList.contains(relation.getNid()))  // 筛选出 nid 在 idList 中的数据
                .map(WebUserNoteRelation::getId)  // 获取对应的 id        // 只保留 id 在 idList 中的数据
                .collect(Collectors.toList());
        if (!idsToDelete.isEmpty()) {
            // 删除符合条件的数据
            userNoteRelationMapper.deleteBatchIds(idsToDelete);
        }
    }
}
