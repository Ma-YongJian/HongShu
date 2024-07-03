package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.web.auth.AuthContextHolder;
import com.hongshu.web.domain.dto.LikeOrCollectionDTO;
import com.hongshu.web.domain.entity.*;
import com.hongshu.web.domain.vo.CommentVo;
import com.hongshu.web.domain.vo.LikeOrCollectionVo;
import com.hongshu.web.mapper.*;
import com.hongshu.web.service.IWebLikeOrCollectionService;
import com.hongshu.web.websocket.im.ChatUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 点赞/收藏
 *
 * @author: hongshu
 */
@Service
public class WebLikeOrCollectionServiceImpl extends ServiceImpl<WebLikeOrCollectionMapper, WebLikeOrCollection> implements IWebLikeOrCollectionService {

    @Autowired
    WebUserMapper userMapper;
    @Autowired
    WebNoteMapper noteMapper;
    @Autowired
    WebAlbumMapper albumMapper;
    @Autowired
    WebCommentMapper commentMapper;
    @Autowired
    WebCommentSyncMapper commentSyncMapper;
    @Autowired
    WebAlbumNoteRelationMapper albumNoteRelationMapper;
    @Autowired
    ChatUtils chatUtils;


    /**
     * 点赞或收藏
     *
     * @param likeOrCollectionDTO 点赞收藏
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void likeOrCollectionByDTO(LikeOrCollectionDTO likeOrCollectionDTO) {
        String currentUid = AuthContextHolder.getUserId();
        // 点赞
        if (isLikeOrCollection(likeOrCollectionDTO)) {
            this.remove(new QueryWrapper<WebLikeOrCollection>().eq("uid", currentUid).eq("like_or_collection_id", likeOrCollectionDTO.getLikeOrCollectionId()).eq("type", likeOrCollectionDTO.getType()));
            this.updateLikeCollectionCount(likeOrCollectionDTO, -1);
        } else {
            // 点赞评论或者笔记
            WebLikeOrCollection likeOrCollection = ConvertUtils.sourceToTarget(likeOrCollectionDTO, WebLikeOrCollection.class);
            likeOrCollection.setTimestamp(System.currentTimeMillis());
            likeOrCollection.setUid(currentUid);
            likeOrCollection.setCreateTime(new Date());
            likeOrCollection.setUpdateTime(new Date());
            this.save(likeOrCollection);
            this.updateLikeCollectionCount(likeOrCollectionDTO, 1);
            // 不是当前用户才进行通知
            if (!likeOrCollectionDTO.getPublishUid().equals(currentUid)) {
                chatUtils.sendMessage(likeOrCollectionDTO.getPublishUid(), 0);
            }
        }
    }

    /**
     * 是否点赞或收藏
     *
     * @param likeOrCollectionDTO 点赞收藏
     */
    @Override
    public boolean isLikeOrCollection(LikeOrCollectionDTO likeOrCollectionDTO) {
        String currentUid = AuthContextHolder.getUserId();
        long count = this.count(new QueryWrapper<WebLikeOrCollection>().eq("uid", currentUid).eq("like_or_collection_id", likeOrCollectionDTO.getLikeOrCollectionId()).eq("type", likeOrCollectionDTO.getType()));
        return count > 0;
    }

    /**
     * 获取当前用户最新的点赞和收藏信息
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @Override
    public Page<LikeOrCollectionVo> getNoticeLikeOrCollection(long currentPage, long pageSize) {
        Page<LikeOrCollectionVo> result = new Page<>();
        String currentUid = AuthContextHolder.getUserId();

        Page<WebLikeOrCollection> likeOrCollectionPage = this.page(new Page<>((int) currentPage, (int) pageSize), new QueryWrapper<WebLikeOrCollection>().eq("publish_uid", currentUid).ne("uid", currentUid).orderByDesc("create_time"));
        List<WebLikeOrCollection> likeOrCollectionList = likeOrCollectionPage.getRecords();
        long total = likeOrCollectionPage.getTotal();
        // TODO 可以使用多线程优化
        // 得到所有用户
        Set<String> uids = likeOrCollectionList.stream().map(WebLikeOrCollection::getUid).collect(Collectors.toSet());
        Map<String, WebUser> userMap = new HashMap<>(16);
        if (!uids.isEmpty()) {
            userMap = userMapper.selectBatchIds(uids).stream().collect(Collectors.toMap(WebUser::getId, user -> user));
        }
        // notes
        Set<String> nids = likeOrCollectionList.stream().filter(e -> e.getType() == 1 || e.getType() == 3).map(WebLikeOrCollection::getLikeOrCollectionId).collect(Collectors.toSet());
        Map<String, WebNote> noteMap = new HashMap<>(16);
        if (!nids.isEmpty()) {
            noteMap = noteMapper.selectBatchIds(nids).stream().collect(Collectors.toMap(WebNote::getId, note -> note));
        }
        // comments
        Set<String> cids = likeOrCollectionList.stream().filter(e -> e.getType() == 2).map(WebLikeOrCollection::getLikeOrCollectionId).collect(Collectors.toSet());
        Map<String, CommentVo> commentVoMap = new HashMap<>(16);
        if (!cids.isEmpty()) {
            List<WebComment> commentList = commentMapper.selectBatchIds(cids);
            Set<String> noteIds = commentList.stream().map(WebComment::getNid).collect(Collectors.toSet());
            Map<String, WebNote> noteMap1 = noteMapper.selectBatchIds(noteIds).stream().collect(Collectors.toMap(WebNote::getId, note -> note));

            commentList.forEach((item -> {
                CommentVo commentVo = ConvertUtils.sourceToTarget(item, CommentVo.class);
                WebNote note = noteMap1.get(item.getNid());
                commentVo.setNoteCover(note.getNoteCover());
                commentVoMap.put(String.valueOf(item.getId()), commentVo);
            }));
        }
        //albums
        Set<String> aids = likeOrCollectionList.stream().filter(e -> e.getType() == 4).map(WebLikeOrCollection::getLikeOrCollectionId).collect(Collectors.toSet());
        Map<String, WebAlbum> albumMap = new HashMap<>(16);
        if (!aids.isEmpty()) {
            albumMap = albumMapper.selectBatchIds(aids).stream().collect(Collectors.toMap(WebAlbum::getId, album -> album));
        }
        List<LikeOrCollectionVo> likeOrCollectionVoList = new ArrayList<>();
        for (WebLikeOrCollection model : likeOrCollectionList) {
            LikeOrCollectionVo likeOrCollectionVo = new LikeOrCollectionVo();
            WebUser user = userMap.get(model.getUid());
            likeOrCollectionVo.setUid(String.valueOf(user.getId()))
                    .setUsername(user.getUsername())
                    .setAvatar(user.getAvatar())
                    .setTime(model.getTimestamp())
                    .setType(model.getType());
            switch (model.getType()) {
                case 2:
                    CommentVo commentVo = commentVoMap.get(model.getLikeOrCollectionId());
                    likeOrCollectionVo.setItemId(commentVo.getId())
                            .setItemCover(commentVo.getNoteCover())
                            .setContent(commentVo.getContent());
                    break;
                case 4:
                    WebAlbum album = albumMap.get(model.getLikeOrCollectionId());
                    likeOrCollectionVo.setItemId(String.valueOf(album.getId()))
                            .setItemCover(album.getAlbumCover())
                            .setContent(album.getTitle());
                    break;
                default:
                    WebNote note = noteMap.get(model.getLikeOrCollectionId());
                    likeOrCollectionVo.setItemId(String.valueOf(note.getId()))
                            .setItemCover(note.getNoteCover());
                    break;
            }
            likeOrCollectionVoList.add(likeOrCollectionVo);
        }
        result.setRecords(likeOrCollectionVoList);
        result.setTotal(total);
        return result;
    }

    /**
     * 点赞
     */
    private void updateLikeCollectionCount(LikeOrCollectionDTO likeOrCollectionDTO, int val) {
        switch (likeOrCollectionDTO.getType()) {
            case 1:
                WebNote likeNote = noteMapper.selectById(likeOrCollectionDTO.getLikeOrCollectionId());
                likeNote.setLikeCount(likeNote.getLikeCount() + val);
                noteMapper.updateById(likeNote);
                break;
            case 2:
                WebComment comment = commentMapper.selectById(likeOrCollectionDTO.getLikeOrCollectionId());
                if (comment == null) {
                    WebCommentSync commentSync = commentSyncMapper.selectById(likeOrCollectionDTO.getLikeOrCollectionId());
                    commentSync.setLikeCount(commentSync.getLikeCount() + val);
                    commentSyncMapper.updateById(commentSync);
                } else {
                    comment.setLikeCount(comment.getLikeCount() + val);
                    commentMapper.updateById(comment);
                }
                break;
            case 3:
                String currentUid = AuthContextHolder.getUserId();
                WebNote collectionNote = noteMapper.selectById(likeOrCollectionDTO.getLikeOrCollectionId());
                //收藏图片
                collectionNote.setCollectionCount(collectionNote.getCollectionCount() + val);
                noteMapper.updateById(collectionNote);

                WebAlbumNoteRelation albumNoteRelation = new WebAlbumNoteRelation();
                albumNoteRelation.setNid(String.valueOf(collectionNote.getId()));
                if (val == 1) {
                    WebAlbum album = albumMapper.selectOne(new QueryWrapper<WebAlbum>().eq("uid", currentUid).eq("type", 0));
                    Integer imgCount = collectionNote.getCount();
                    if (ObjectUtils.isEmpty(album)) {
                        album = new WebAlbum();
                        album.setTitle("默认专辑");
                        album.setUid(currentUid);
                        album.setAlbumCover(collectionNote.getNoteCover());
                        album.setImgCount(Long.valueOf(imgCount));
                        album.setCreateTime(new Date());
                        album.setUpdateTime(new Date());
                        albumMapper.insert(album);
                    } else {
                        album.setImgCount(album.getImgCount() + imgCount);
                        if (StringUtils.isBlank(album.getAlbumCover())) {
                            album.setAlbumCover(collectionNote.getNoteCover());
                        }
                        albumMapper.updateById(album);
                    }
                    albumNoteRelation.setAid(String.valueOf(album.getId()));
                    albumNoteRelation.setCreateTime(new Date());
                    albumNoteRelation.setUpdateTime(new Date());
                    albumNoteRelationMapper.insert(albumNoteRelation);
                } else {
                    List<WebAlbumNoteRelation> albumNoteRelationList = albumNoteRelationMapper.selectList(new QueryWrapper<WebAlbumNoteRelation>().eq("nid", collectionNote.getId()));
                    Set<String> aids = albumNoteRelationList.stream().map(WebAlbumNoteRelation::getAid).collect(Collectors.toSet());
                    List<WebAlbum> albumList = albumMapper.selectBatchIds(aids);
                    WebAlbum album = albumList.stream().filter(item -> item.getUid().equals(currentUid)).findFirst().orElse(null);
                    Integer imgCount = collectionNote.getCount();
                    long nums = album.getImgCount() - imgCount;
                    if (nums <= 0) {
                        album.setAlbumCover(null);
                    }
                    album.setImgCount(nums);
                    albumMapper.updateById(album);
                    albumNoteRelationMapper.delete(new QueryWrapper<WebAlbumNoteRelation>().eq("aid", album.getId()).eq("nid", collectionNote.getId()));
                }
                break;
            default:
                // 收藏专辑
                WebAlbum collectAlbum = albumMapper.selectById(likeOrCollectionDTO.getLikeOrCollectionId());
                collectAlbum.setCollectionCount(collectAlbum.getCollectionCount() + val);
                albumMapper.updateById(collectAlbum);
                break;
        }
    }
}
