package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.web.auth.AuthContextHolder;
import com.hongshu.web.domain.entity.WebAlbumNoteRelation;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.NoteSearchVO;
import com.hongshu.web.mapper.WebAlbumNoteRelationMapper;
import com.hongshu.web.mapper.WebNoteMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.IWebAlbumNoteRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 专辑-笔记
 *
 * @Author hongshu
 */
@Service
public class WebAlbumNoteRelationServiceImpl extends ServiceImpl<WebAlbumNoteRelationMapper, WebAlbumNoteRelation> implements IWebAlbumNoteRelationService {

    @Autowired
    WebNoteMapper noteMapper;

    @Autowired
    WebUserMapper userMapper;


    /**
     * 得到当前专辑下的所有笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param albumId     专辑ID
     * @param userId      用户ID
     */
    @Override
    public Page<NoteSearchVO> getNoteByAlbumId(long currentPage, long pageSize, String albumId, String userId) {
        Page<NoteSearchVO> result = new Page<>();
        Page<WebAlbumNoteRelation> albumImgRelationPage = this.page(new Page<>(currentPage, pageSize), new QueryWrapper<WebAlbumNoteRelation>().eq("aid", albumId).orderByDesc("create_time"));
        long total = albumImgRelationPage.getTotal();
        List<WebAlbumNoteRelation> records = albumImgRelationPage.getRecords();
        List<String> nids = records.stream().map(WebAlbumNoteRelation::getNid).collect(Collectors.toList());
        String currentUser = AuthContextHolder.getUserId();
        List<WebNote> noteList;
        if (currentUser.equals(userId)) {
            // 表示是当前用户(能够查看所有的专辑，包括上传中的笔记)
            noteList = noteMapper.selectBatchIds(nids);
        } else {
            noteList = noteMapper.selectList(new QueryWrapper<WebNote>().in("id", nids).eq("status", 1));
        }
        List<String> uids = noteList.stream().map(WebNote::getUid).collect(Collectors.toList());
        List<WebUser> userList = userMapper.selectBatchIds(uids);
        HashMap<String, WebUser> userMap = new HashMap<>(16);
        userList.forEach(item -> {
            userMap.put(String.valueOf(item.getId()), item);
        });
        HashMap<String, WebNote> noteMap = new HashMap<>(16);
        noteList.forEach(item -> {
            noteMap.put(String.valueOf(item.getId()), item);
        });
        List<NoteSearchVO> noteVoList = new ArrayList<>();
        WebNote note;
        WebUser user;
        NoteSearchVO noteSearchVo;
        for (WebAlbumNoteRelation model : records) {
            note = noteMap.get(model.getNid());
            user = userMap.get(note.getUid());
            noteSearchVo = ConvertUtils.sourceToTarget(note, NoteSearchVO.class);
            noteSearchVo.setUsername(user.getUsername())
                    .setAvatar(user.getAvatar());
            noteVoList.add(noteSearchVo);
        }
        result.setTotal(total);
        result.setRecords(noteVoList);
        return result;
    }
}
