package com.hongshu.web.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.constant.UserConstant;
import com.hongshu.common.enums.ResultCodeEnum;
import com.hongshu.common.exception.web.HongshuException;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.common.utils.WebUtils;
import com.hongshu.web.auth.AuthContextHolder;
import com.hongshu.web.domain.dto.NoteDTO;
import com.hongshu.web.domain.entity.*;
import com.hongshu.web.domain.vo.NoteVO;
import com.hongshu.web.mapper.WebNoteMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author hongshu
 */
@Service
public class WebNoteServiceImpl extends ServiceImpl<WebNoteMapper, WebNote> implements IWebNoteService {

    @Autowired
    IWebUserService userService;
    @Autowired
    private WebUserMapper userMapper;
    @Autowired
    IWebTagNoteRelationService tagNoteRelationService;
    @Autowired
    IWebTagService tagService;
    @Autowired
    IWebNavbarService categoryService;
    @Autowired
    IWebEsNoteService esNoteService;
    @Autowired
    IWebFollowService followerService;
    @Autowired
    IWebLikeOrCollectService likeOrCollectionService;
    @Autowired
    IWebCommentService commentService;
    @Autowired
    IWebCommentSyncService commentSyncService;
    @Autowired
    IWebAlbumNoteRelationService albumNoteRelationService;
    @Autowired
    private IWebOssService ossService;
    @Autowired
    WebNoteMapper noteMapper;


    @NotNull
    private StringBuilder getTags(WebNote note, NoteDTO noteDTO) {
        List<String> tagList = noteDTO.getTagList();
        List<WebTagNoteRelation> tagNoteRelationList = new ArrayList<>();
        List<WebTag> tagList1 = tagService.list();
        Map<String, WebTag> tagMap = tagList1.stream().collect(Collectors.toMap(WebTag::getTitle, tag -> tag));
        StringBuilder tags = new StringBuilder();
        if (!tagList.isEmpty()) {
            for (String tag : tagList) {
                WebTagNoteRelation tagNoteRelation = new WebTagNoteRelation();
                if (tagMap.containsKey(tag)) {
                    WebTag tagModel = tagMap.get(tag);
                    tagNoteRelation.setTid(String.valueOf(tagModel.getId()));
                } else {
                    WebTag model = new WebTag();
                    model.setTitle(tag);
                    model.setLikeCount(1L);
                    tagService.save(model);
                    tagNoteRelation.setTid(String.valueOf(model.getId()));
                }
                tagNoteRelation.setNid(String.valueOf(note.getId()));
                tagNoteRelationList.add(tagNoteRelation);
                tags.append(tag);
            }
            tagNoteRelationService.saveBatch(tagNoteRelationList);
        }
        return tags;
    }

    /**
     * 获取笔记
     *
     * @param noteId 笔记ID
     */
    @Override
    public NoteVO getNoteById(String noteId) {
        WebNote note = this.getById(noteId);
        if (note == null) {
            throw new HongshuException(ResultCodeEnum.FAIL);
        }
        note.setViewCount(note.getViewCount() + 1);
        WebUser user = userService.getById(note.getUid());
        NoteVO noteVo = ConvertUtils.sourceToTarget(note, NoteVO.class);
        noteVo.setUsername(user.getUsername())
                .setAvatar(user.getAvatar())
                .setTime(note.getUpdateTime().getTime());

        boolean follow = followerService.isFollow(String.valueOf(user.getId()));
        noteVo.setIsFollow(follow);

        String currentUid = AuthContextHolder.getUserId();
        List<WebLikeOrCollect> likeOrCollectionList = likeOrCollectionService.list(new QueryWrapper<WebLikeOrCollect>().eq("like_or_collection_id", noteId).eq("uid", currentUid));

        Set<Integer> types = likeOrCollectionList.stream().map(WebLikeOrCollect::getType).collect(Collectors.toSet());
        noteVo.setIsLike(types.contains(1));
        noteVo.setIsCollection(types.contains(3));


        //得到标签
        List<WebTagNoteRelation> tagNoteRelationList = tagNoteRelationService.list(new QueryWrapper<WebTagNoteRelation>().eq("nid", noteId));
        List<String> tids = tagNoteRelationList.stream().map(WebTagNoteRelation::getTid).collect(Collectors.toList());

        if (!tids.isEmpty()) {
            List<WebTag> tagList = tagService.listByIds(tids);
            noteVo.setTagList(tagList);
        }

        this.updateById(note);
        return noteVo;
    }

    /**
     * 新增笔记
     *
     * @param noteData 笔记对象
     * @param files    图片文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveNoteByDTO(String noteData, MultipartFile[] files) {

        String currentUid = AuthContextHolder.getUserId();
        // 更新用户笔记数量
        WebUser user = userMapper.selectById(currentUid);
        user.setTrendCount(user.getTrendCount() + 1);
        userMapper.updateById(user);

        // 保存笔记
        NoteDTO noteDTO = JSONUtil.toBean(noteData, NoteDTO.class);
        WebNote note = ConvertUtils.sourceToTarget(noteDTO, WebNote.class);
        note.setUid(currentUid);
        note.setAuthor(user.getUsername());
        note.setAuditStatus("0");
        note.setNoteType("0");
        note.setCreator(user.getUsername());
        note.setTime(System.currentTimeMillis());
        note.setCreateTime(new Date());
        note.setUpdateTime(new Date());

        // 批量上传图片
        List<String> dataList = null;
        try {
            dataList = ossService.saveBatch(files);
        } catch (Exception e) {
            log.error("图片上传失败");
            e.printStackTrace();
        }
        String[] urlArr = Objects.requireNonNull(dataList).toArray(new String[dataList.size()]);
        String urls = JSONUtil.toJsonStr(urlArr);
        note.setUrls(urls);
        note.setNoteCover(urlArr[0]);
        this.save(note);

        return note.getId();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNoteByIds(List<String> noteIds) {
        List<WebNote> noteList = this.listByIds(noteIds);
        // TODO 这里需要优化，数据一致性问题
        noteList.forEach(item -> {
            String noteId = item.getId();
            esNoteService.deleteNote(noteId);

            String urls = item.getUrls();
            JSONArray objects = JSONUtil.parseArray(urls);
            Object[] array = objects.toArray();
            List<String> pathArr = new ArrayList<>();
            for (Object o : array) {
                pathArr.add((String) o);
            }
            ossService.batchDelete(pathArr);
            // TODO 可以使用多线程优化，
            // 删除点赞图片，评论，标签关系，收藏关系
            likeOrCollectionService.remove(new QueryWrapper<WebLikeOrCollect>().eq("like_or_collection_id", noteId));
            List<WebComment> commentList = commentService.list(new QueryWrapper<WebComment>().eq("nid", noteId));
            List<WebCommentSync> commentSyncList = commentSyncService.list(new QueryWrapper<WebCommentSync>().eq("nid", noteId));
            List<String> cids = commentList.stream().map(WebComment::getId).collect(Collectors.toList());
            List<String> cids2 = commentSyncList.stream().map(WebCommentSync::getId).collect(Collectors.toList());
            if (!cids.isEmpty()) {
                likeOrCollectionService.remove(new QueryWrapper<WebLikeOrCollect>().in("like_or_collection_id", cids).eq("type", 2));
            }
            commentService.removeBatchByIds(cids);
            commentSyncService.removeBatchByIds(cids2);
            tagNoteRelationService.remove(new QueryWrapper<WebTagNoteRelation>().eq("nid", noteId));
            albumNoteRelationService.remove(new QueryWrapper<WebAlbumNoteRelation>().eq("nid", noteId));
        });
        this.removeBatchByIds(noteIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNoteByDTO(String noteData, MultipartFile[] files) {
        String currentUid = AuthContextHolder.getUserId();
        NoteDTO noteDTO = JSONUtil.toBean(noteData, NoteDTO.class);
        WebNote note = ConvertUtils.sourceToTarget(noteDTO, WebNote.class);
        note.setUid(currentUid);
        boolean flag = this.updateById(note);
        if (!flag) {
            return;
        }
        WebNavbar category = categoryService.getById(note.getCid());
        WebNavbar parentCategory = categoryService.getById(note.getCpid());
        List<String> dataList = null;
        try {
            dataList = ossService.saveBatch(files);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 删除原来图片的地址
        String urls = note.getUrls();
        JSONArray objects = JSONUtil.parseArray(urls);
        Object[] array = objects.toArray();
        List<String> pathArr = new ArrayList<>();
        for (Object o : array) {
            pathArr.add((String) o);
        }
        ossService.batchDelete(pathArr);

        String[] urlArr = Objects.requireNonNull(dataList).toArray(new String[dataList.size()]);
        String newUrls = JSONUtil.toJsonStr(urlArr);
        note.setUrls(newUrls);
        note.setNoteCover(urlArr[0]);
        note.setAuditStatus("0");
        note.setTime(System.currentTimeMillis());
        note.setNoteType("0");
        note.setUpdateTime(new Date());
        this.updateById(note);

        // 删除原来的标签绑定关系
        tagNoteRelationService.remove(new QueryWrapper<WebTagNoteRelation>().eq("nid", note.getId()));
        // 重新绑定标签关系
        StringBuilder tags = getTags(note, noteDTO);

        esNoteService.deleteNote(note.getId());

//        WebUser user = userService.getById(currentUid);
//
//        NoteSearchVo noteSearchVo = ConvertUtils.sourceToTarget(note, NoteSearchVo.class);
//        noteSearchVo.setUsername(user.getUsername())
//                .setAvatar(user.getAvatar())
//                .setCategoryName(category.getTitle())
//                .setCategoryParentName(parentCategory.getTitle())
//                .setTags(tags.toString())
//                .setTime(note.getUpdateTime().getTime());
//        esNoteService.updateNote(noteSearchVo);
    }

    @Override
    public Page<NoteVO> getHotPage(long currentPage, long pageSize) {
        return null;
    }

    @Override
    public boolean pinnedNote(String noteId) {
        String currentUid = AuthContextHolder.getUserId();
        WebNote note = this.getById(noteId);
        if ("1".equals(note.getPinned())) {
            note.setPinned("0");
        } else {
            List<WebNote> noteList = this.list(new QueryWrapper<WebNote>().eq("uid", currentUid));
            long count = noteList.stream().filter(item -> "1".equals(item.getPinned())).count();
            if (count >= 3) {
                throw new HongshuException("最多只能置顶3个笔记");
            }
            note.setPinned("1");
            note.setUpdateTime(new Date());
        }
        return this.updateById(note);
    }
}
