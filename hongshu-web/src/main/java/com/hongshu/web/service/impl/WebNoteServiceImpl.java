package com.hongshu.web.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.enums.ResultCodeEnum;
import com.hongshu.common.exception.web.HongshuException;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.web.auth.AuthContextHolder;
import com.hongshu.web.domain.dto.NoteDTO;
import com.hongshu.web.domain.entity.*;
import com.hongshu.web.domain.vo.NoteVO;
import com.hongshu.web.mapper.WebNoteMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author hongshu
 */
@Slf4j
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
    IWebAlbumNoteRelationService albumNoteRelationService;
    @Autowired
    private IWebOssService ossService;
    @Autowired
    WebNoteMapper noteMapper;


    private StringBuilder getTags(WebNote note, NoteDTO noteDTO) {
        StringBuilder tags = new StringBuilder();
        if (note == null || StringUtils.isBlank(note.getId())) {
            return tags;
        }
        List<String> tagList = parseTagTitles(noteDTO);
        if (tagList.isEmpty()) {
            return tags;
        }
        List<WebTagNoteRelation> tagNoteRelationList = new ArrayList<>();
        for (String title : tagList) {
            WebTag model = tagService.getOne(new QueryWrapper<WebTag>().eq("title", title), false);
            if (model == null) {
                model = new WebTag();
                model.setTitle(title);
                model.setLikeCount(1L);
                tagService.save(model);
            }
            WebTagNoteRelation tagNoteRelation = new WebTagNoteRelation();
            tagNoteRelation.setTid(String.valueOf(model.getId()));
            tagNoteRelation.setNid(String.valueOf(note.getId()));
            tagNoteRelationList.add(tagNoteRelation);
            tags.append(title);
        }
        if (!tagNoteRelationList.isEmpty()) {
            tagNoteRelationService.saveBatch(tagNoteRelationList);
        }
        return tags;
    }

    private List<String> parseTagTitles(NoteDTO noteDTO) {
        List<String> result = new ArrayList<>();
        if (noteDTO == null || noteDTO.getTagList() == null) {
            return result;
        }
        LinkedHashSet<String> uniq = new LinkedHashSet<>();
        for (String tag : noteDTO.getTagList()) {
            if (StringUtils.isBlank(tag)) {
                continue;
            }
            String title = tag.trim();
            if (title.startsWith("{")) {
                try {
                    cn.hutool.json.JSONObject obj = JSONUtil.parseObj(title);
                    title = StringUtils.defaultIfBlank(obj.getStr("title"), obj.getStr("name"));
                } catch (Exception ignored) {
                }
            }
            if (StringUtils.isNotBlank(title)) {
                uniq.add(title.trim());
            }
        }
        result.addAll(uniq);
        return result;
    }

    private void applyCategory(WebNote note, String cid) {
        if (note == null || StringUtils.isBlank(cid)) {
            throw new HongshuException("请选择分类");
        }
        WebNavbar category = categoryService.getById(cid);
        if (category == null) {
            throw new HongshuException("分类不存在");
        }
        note.setCid(String.valueOf(category.getId()));
        String pid = category.getPid();
        if (StringUtils.isBlank(pid) || "0".equals(pid) || "-1".equals(pid)) {
            note.setCpid(String.valueOf(category.getId()));
        } else {
            note.setCpid(pid);
        }
    }

    private String resolveNoteType(NoteDTO noteDTO) {
        Integer type = noteDTO == null ? null : noteDTO.getType();
        if (type != null && type == 2) {
            return "2";
        }
        return "1";
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
        Long viewCount = note.getViewCount();
        note.setViewCount((viewCount == null ? 0L : viewCount) + 1);
        WebUser user = userService.getById(note.getUid());
        if (user == null) {
            throw new HongshuException(ResultCodeEnum.FAIL);
        }
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
        if (StringUtils.isBlank(currentUid)) {
            throw new HongshuException(ResultCodeEnum.TOKEN_FAIL);
        }
        WebUser user = userMapper.selectById(currentUid);
        if (user == null) {
            throw new HongshuException(ResultCodeEnum.FAIL);
        }
        NoteDTO noteDTO = JSONUtil.toBean(noteData, NoteDTO.class);
        if (noteDTO == null || StringUtils.isBlank(noteDTO.getTitle()) || StringUtils.isBlank(noteDTO.getCid())) {
            throw new HongshuException("请填写标题并选择分类");
        }
        List<String> urlList = resolveNoteUrls(noteDTO, files);
        Long trendCount = user.getTrendCount();
        user.setTrendCount((trendCount == null ? 0L : trendCount) + 1);
        userMapper.updateById(user);

        WebNote note = ConvertUtils.sourceToTarget(noteDTO, WebNote.class);
        note.setId(null);
        note.setUid(currentUid);
        note.setAuthor(user.getUsername());
        note.setStatus("1");
        note.setAuditStatus("0");
        applyCategory(note, noteDTO.getCid());
        note.setNoteType(resolveNoteType(noteDTO));
        note.setCreator(user.getUsername());
        note.setTime(System.currentTimeMillis());
        note.setCreateTime(new Date());
        note.setUpdateTime(new Date());
        note.setLikeCount(0L);
        note.setCollectionCount(0L);
        note.setCommentCount(0L);
        note.setViewCount(0L);
        note.setCount(urlList.size());
        note.setUrls(JSONUtil.toJsonStr(urlList));
        note.setNoteCover(resolveCover(noteDTO, urlList));
        this.save(note);
        getTags(note, noteDTO);
        bindAlbum(note.getId(), noteDTO.getAlbumId());
        return note.getId();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNoteByIds(List<String> noteIds) {
        String currentUid = AuthContextHolder.getUserId();
        if (StringUtils.isBlank(currentUid)) {
            throw new HongshuException(ResultCodeEnum.TOKEN_FAIL);
        }
        if (noteIds == null || noteIds.isEmpty()) {
            return;
        }
        List<WebNote> noteList = this.listByIds(noteIds);
        for (WebNote item : noteList) {
            if (item == null || !currentUid.equals(item.getUid())) {
                throw new HongshuException("只能删除自己的笔记");
            }
        }
        noteList.forEach(item -> {
            String noteId = item.getId();
            esNoteService.deleteNote(noteId);
            List<String> pathArr = parseUrlList(item.getUrls());
            if (StringUtils.isNotBlank(item.getNoteCover()) && !pathArr.contains(item.getNoteCover())) {
                pathArr.add(item.getNoteCover());
            }
            ossService.batchDelete(pathArr);
            likeOrCollectionService.remove(new QueryWrapper<WebLikeOrCollect>().eq("like_or_collection_id", noteId));
            List<WebComment> commentList = commentService.list(new QueryWrapper<WebComment>().eq("nid", noteId));
            List<String> cids = commentList.stream().map(WebComment::getId).collect(Collectors.toList());
            if (!cids.isEmpty()) {
                likeOrCollectionService.remove(new QueryWrapper<WebLikeOrCollect>().in("like_or_collection_id", cids));
                commentService.removeBatchByIds(cids);
            }
            tagNoteRelationService.remove(new QueryWrapper<WebTagNoteRelation>().eq("nid", noteId));
            albumNoteRelationService.remove(new QueryWrapper<WebAlbumNoteRelation>().eq("nid", noteId));
        });
        this.removeBatchByIds(noteIds);
        WebUser user = userMapper.selectById(currentUid);
        if (user != null && !noteList.isEmpty()) {
            long trendCount = user.getTrendCount() == null ? 0L : user.getTrendCount();
            user.setTrendCount(Math.max(0L, trendCount - noteList.size()));
            userMapper.updateById(user);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNoteByDTO(String noteData, MultipartFile[] files) {
        String currentUid = AuthContextHolder.getUserId();
        if (StringUtils.isBlank(currentUid)) {
            throw new HongshuException(ResultCodeEnum.TOKEN_FAIL);
        }
        NoteDTO noteDTO = JSONUtil.toBean(noteData, NoteDTO.class);
        if (noteDTO == null || StringUtils.isBlank(noteDTO.getId())) {
            throw new HongshuException(ResultCodeEnum.FAIL);
        }
        WebNote exist = this.getById(noteDTO.getId());
        if (exist == null || !currentUid.equals(exist.getUid())) {
            throw new HongshuException(ResultCodeEnum.FAIL);
        }
        List<String> urlList = resolveNoteUrls(noteDTO, files);
        List<String> oldUrls = parseUrlList(exist.getUrls());
        List<String> removed = new ArrayList<>();
        for (String old : oldUrls) {
            if (!urlList.contains(old)) {
                removed.add(old);
            }
        }
        if (!removed.isEmpty()) {
            ossService.batchDelete(removed);
        }
        exist.setTitle(noteDTO.getTitle());
        exist.setContent(noteDTO.getContent());
        applyCategory(exist, noteDTO.getCid());
        exist.setNoteCoverHeight(noteDTO.getNoteCoverHeight());
        exist.setCount(urlList.size());
        exist.setUrls(JSONUtil.toJsonStr(urlList));
        exist.setNoteCover(resolveCover(noteDTO, urlList));
        exist.setAuditStatus("0");
        exist.setNoteType(resolveNoteType(noteDTO));
        exist.setTime(System.currentTimeMillis());
        exist.setUpdateTime(new Date());
        this.updateById(exist);
        tagNoteRelationService.remove(new QueryWrapper<WebTagNoteRelation>().eq("nid", exist.getId()));
        getTags(exist, noteDTO);
        albumNoteRelationService.remove(new QueryWrapper<WebAlbumNoteRelation>().eq("nid", exist.getId()));
        bindAlbum(exist.getId(), noteDTO.getAlbumId());
        esNoteService.deleteNote(exist.getId());
    }

    private List<String> resolveNoteUrls(NoteDTO noteDTO, MultipartFile[] files) {
        List<String> urls = new ArrayList<>();
        if (noteDTO.getUrls() != null) {
            for (String url : noteDTO.getUrls()) {
                if (isStoredUrl(url)) {
                    urls.add(url);
                }
            }
        }
        List<MultipartFile> validFiles = new ArrayList<>();
        if (files != null) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    validFiles.add(file);
                }
            }
        }
        if (!validFiles.isEmpty()) {
            try {
                urls.addAll(ossService.saveBatch(validFiles.toArray(new MultipartFile[0])));
            } catch (Exception e) {
                log.error("图片上传失败", e);
                throw new HongshuException("图片上传失败");
            }
        }
        if (urls.isEmpty()) {
            throw new HongshuException("请上传图片");
        }
        return urls;
    }

    private String resolveCover(NoteDTO noteDTO, List<String> urlList) {
        if (noteDTO != null && isStoredUrl(noteDTO.getNoteCover()) && urlList.contains(noteDTO.getNoteCover())) {
            return noteDTO.getNoteCover();
        }
        return urlList.get(0);
    }

    private boolean isStoredUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return false;
        }
        String value = url.trim();
        if (value.startsWith("blob:") || value.startsWith("file:") || value.startsWith("wxfile:")) {
            return false;
        }
        if (value.startsWith("/profile")) {
            return true;
        }
        String lower = value.toLowerCase();
        if (lower.startsWith("http://127.0.0.1") || lower.startsWith("http://localhost")) {
            return false;
        }
        return lower.startsWith("http://") || lower.startsWith("https://");
    }

    private List<String> parseUrlList(String urls) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isBlank(urls)) {
            return result;
        }
        try {
            JSONArray array = JSONUtil.parseArray(urls);
            for (Object item : array) {
                if (item != null && StringUtils.isNotBlank(item.toString())) {
                    result.add(item.toString());
                }
            }
        } catch (Exception e) {
            log.warn("解析笔记图片失败: {}", urls);
        }
        return result;
    }

    private void bindAlbum(String noteId, String albumId) {
        if (StringUtils.isBlank(noteId) || StringUtils.isBlank(albumId)) {
            return;
        }
        WebAlbumNoteRelation relation = new WebAlbumNoteRelation();
        relation.setNid(noteId);
        relation.setAid(albumId);
        albumNoteRelationService.save(relation);
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
