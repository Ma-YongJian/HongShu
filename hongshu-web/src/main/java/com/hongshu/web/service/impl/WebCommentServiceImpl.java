package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.constant.UserConstant;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.common.utils.WebUtils;
import com.hongshu.web.auth.AuthContextHolder;
import com.hongshu.web.domain.dto.CommentDTO;
import com.hongshu.web.domain.entity.*;
import com.hongshu.web.domain.vo.CommentVo;
import com.hongshu.web.mapper.*;
import com.hongshu.web.service.IWebCommentService;
import com.hongshu.web.websocket.im.ChatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论
 *
 * @author: hongshu
 */
@Service
public class WebCommentServiceImpl extends ServiceImpl<WebCommentMapper, WebComment> implements IWebCommentService {

    @Autowired
    WebNoteMapper noteMapper;
    @Autowired
    WebUserMapper userMapper;
    @Autowired
    WebCommentSyncMapper commentSyncMapper;
    @Autowired
    WebLikeOrCollectionMapper likeOrCollectionMapper;
    @Autowired
    ChatUtils chatUtils;


    /**
     * 获取所有一级分类
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param noteId      笔记ID
     */
    @Override
    public Page<CommentVo> getOneCommentByNoteId(long currentPage, long pageSize, String noteId) {
        return null;
    }

    /**
     * 根据评论ID获取当前评论
     *
     * @param commentId 评论ID
     */
    @Override
    public Object getCommentById(String commentId) {
        return null;
    }

    /**
     * 保存评论
     *
     * @param commentDTO 评论
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommentVo saveCommentByDTO(CommentDTO commentDTO) {
        String currentUid = AuthContextHolder.getUserId();
        WebCommentSync commentsync = ConvertUtils.sourceToTarget(commentDTO, WebCommentSync.class);
        commentsync.setUid(currentUid);
        commentsync.setCreateTime(new Date());
        commentsync.setUpdateTime(new Date());
        commentSyncMapper.insert(commentsync);

        WebNote note = noteMapper.selectById(commentDTO.getNid());
        note.setCommentCount(note.getCommentCount() + 1);
        noteMapper.updateById(note);

        CommentVo commentVo = ConvertUtils.sourceToTarget(commentsync, CommentVo.class);
        WebUser user = userMapper.selectById(currentUid);

        commentVo.setUsername(user.getUsername())
                .setAvatar(user.getAvatar())
                .setTime(commentsync.getCreateTime().getTime());

        // 一级评论数量加1
        if (!"0".equals(commentDTO.getPid())) {
            WebComment parentComment = this.getById(commentDTO.getPid());
            if (parentComment == null) {
                WebCommentSync commentSync = commentSyncMapper.selectById(commentDTO.getPid());
                commentSync.setTwoCommentCount(commentSync.getTwoCommentCount() + 1);
                commentSyncMapper.updateById(commentSync);
            } else {
                parentComment.setTwoCommentCount(parentComment.getTwoCommentCount() + 1);
                this.updateById(parentComment);
            }
        }
        return commentVo;
    }

    /**
     * 根据评论ID同步评论集
     *
     * @param commentIds 评论ID数据集
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void syncCommentByIds(List<String> commentIds) {
        String currentUid = AuthContextHolder.getUserId();
        if (!commentIds.isEmpty()) {
            List<WebCommentSync> commentSyncs = commentSyncMapper.selectList(new QueryWrapper<WebCommentSync>().in("id", commentIds));
            List<WebComment> comments = ConvertUtils.sourceToTarget(commentSyncs, WebComment.class);
            this.saveBatch(comments);

            // TODO 可以使用异步的方式进行通知
            for (WebComment comment : comments) {
                if (!comment.getNoteUid().equals(currentUid)) {
                    if (!Objects.equals(comment.getReplyUid(), comment.getNoteUid())) {
                        chatUtils.sendMessage(comment.getNoteUid(), 1);
                    }
                    chatUtils.sendMessage(comment.getReplyUid(), 1);
                }
            }
        }
    }

    /**
     * 根据一级评论ID获取所有的二级评论
     *
     * @param currentPage  当前页
     * @param pageSize     分页数
     * @param oneCommentId 一级评论ID
     */
    @Override
    public Page<CommentVo> getTwoCommentByOneCommentId(long currentPage, long pageSize, String oneCommentId) {
        Page<CommentVo> result = new Page<>();
        String currentUid = AuthContextHolder.getUserId();
        Page<WebComment> twoCommentPage = this.page(new Page<>((int) currentPage, (int) pageSize), new QueryWrapper<WebComment>().eq("pid", oneCommentId).orderByDesc("like_count").orderByDesc("create_time"));
        List<WebComment> twoCommentList = twoCommentPage.getRecords();
        long total = twoCommentPage.getTotal();

        if (!twoCommentList.isEmpty()) {
            Set<String> uids = twoCommentList.stream().map(WebComment::getUid).collect(Collectors.toSet());
            List<WebUser> users = userMapper.selectBatchIds(uids);
            Map<String, WebUser> userMap = users.stream().collect(Collectors.toMap(WebUser::getId, user -> user));
            Set<String> replyUids = twoCommentList.stream().map(WebComment::getReplyUid).collect(Collectors.toSet());
            Map<String, WebUser> replyUserMap = new HashMap<>(16);
            if (!replyUids.isEmpty()) {
                List<WebUser> replyUsers = userMapper.selectBatchIds(replyUids);
                replyUserMap = replyUsers.stream().collect(Collectors.toMap(WebUser::getId, user -> user));
            }

            List<CommentVo> commentVos = new ArrayList<>();
            List<WebLikeOrCollection> likeOrCollections = likeOrCollectionMapper.selectList(new QueryWrapper<WebLikeOrCollection>().eq("uid", currentUid).eq("type", 2));
            List<String> likeComments = likeOrCollections.stream().map(WebLikeOrCollection::getLikeOrCollectionId).collect(Collectors.toList());
            for (WebComment comment : twoCommentList) {
                CommentVo commentVo = ConvertUtils.sourceToTarget(comment, CommentVo.class);
                WebUser user = userMap.get(comment.getUid());
                commentVo.setUsername(user.getUsername())
                        .setAvatar(user.getAvatar())
                        .setTime(comment.getCreateTime().getTime())
                        .setIsLike(likeComments.contains(comment.getId()));
                WebUser replyUser = replyUserMap.get(comment.getReplyUid());
                if (replyUser != null) {
                    commentVo.setReplyUsername(replyUser.getUsername());
                }
                commentVos.add(commentVo);
            }
            result.setRecords(commentVos);
        }
        result.setTotal(total);
        return result;
    }

    /**
     * 获取当前用户通知的评论集
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @Override
    public IPage<CommentVo> getNoticeComment(long currentPage, long pageSize) {
        Page<CommentVo> result = new Page<>();
        String currentUid = WebUtils.getRequestHeader(UserConstant.USER_ID);
//        String currentUid = AuthContextHolder.getUserId();

        Page<WebComment> commentPage = this.page(new Page<>((int) currentPage, (int) pageSize), new QueryWrapper<WebComment>().or(e -> e.eq("note_uid", currentUid).or().eq("reply_uid", currentUid)).ne("uid", currentUid).orderByDesc("create_time"));

        List<WebComment> commentList = commentPage.getRecords();
        long total = commentPage.getTotal();

        List<CommentVo> commentVoList = new ArrayList<>();
        if (!commentList.isEmpty()) {
            Set<String> uids = commentList.stream().map(WebComment::getUid).collect(Collectors.toSet());
            Map<String, WebUser> userMap = userMapper.selectBatchIds(uids).stream().collect(Collectors.toMap(WebUser::getId, user -> user));

            Set<String> nids = commentList.stream().map(WebComment::getNid).collect(Collectors.toSet());
            Map<String, WebNote> noteMap = noteMapper.selectBatchIds(nids).stream().collect(Collectors.toMap(WebNote::getId, note -> note));

            // 得到所有回复的评论内容
            Set<String> cids = commentList.stream().filter(item -> !"0".equals(item.getPid())).map(WebComment::getReplyId).collect(Collectors.toSet());
            Map<String, WebComment> replyCommentMap = new HashMap<>(16);
            if (!cids.isEmpty()) {
                replyCommentMap = this.listByIds(cids).stream().collect(Collectors.toMap(WebComment::getId, comment -> comment));
            }
            for (WebComment comment : commentList) {
                CommentVo commentVo = ConvertUtils.sourceToTarget(comment, CommentVo.class);
                WebUser user = userMap.get(comment.getUid());
                WebNote note = noteMap.get(comment.getNid());
                commentVo.setUsername(user.getUsername())
                        .setAvatar(user.getAvatar())
                        .setTime(comment.getCreateTime().getTime())
                        .setNoteCover(note.getNoteCover());

                if (!"0".equals(comment.getPid())) {
                    WebComment replyComment = replyCommentMap.get(comment.getReplyId());
                    commentVo.setReplyContent(replyComment.getContent());
                    if (!comment.getReplyUid().equals(currentUid)) {
                        WebUser replyUser = userMap.get(comment.getReplyUid());
                        commentVo.setReplyUsername(replyUser.getUsername());
                    }
                }
                commentVoList.add(commentVo);
            }
        }
        result.setRecords(commentVoList);
        result.setTotal(total);
        return result;
    }

    /**
     * 获取所有的一级评论并携带二级评论
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param noteId      笔记ID
     */
    @Override
    public Page<CommentVo> getCommentWithCommentByNoteId(long currentPage, long pageSize, String noteId) {
        //先得到所有的一级评论
        Page<CommentVo> result = new Page<>();
        Page<WebComment> oneCommentPage = this.page(new Page<>((int) currentPage, (int) pageSize), new QueryWrapper<WebComment>().eq("nid", noteId).eq("pid", "0").orderByDesc("like_count"));
        List<WebComment> oneCommentList = oneCommentPage.getRecords();
        if (!oneCommentList.isEmpty()) {
            Set<String> oneUids = oneCommentList.stream().map(WebComment::getUid).collect(Collectors.toSet());
            long onetotal = oneCommentPage.getTotal();
            String currentUid = AuthContextHolder.getUserId();
            //得到对应的二级评论
            List<String> oneIds = oneCommentList.stream().map(WebComment::getId).collect(Collectors.toList());
            List<WebComment> twoCommentList = this.list(new QueryWrapper<WebComment>().in("pid", oneIds).orderByDesc("like_count").orderByDesc("create_time"));
            Set<String> twoUids = twoCommentList.stream().map(WebComment::getUid).collect(Collectors.toSet());
            oneUids.addAll(twoUids);

            List<WebUser> users = userMapper.selectBatchIds(oneUids);
            Map<String, WebUser> userMap = users.stream().collect(Collectors.toMap(WebUser::getId, user -> user));

            //得到当前用户点赞的评论
            List<WebLikeOrCollection> likeOrCollections = likeOrCollectionMapper.selectList(new QueryWrapper<WebLikeOrCollection>().eq("uid", currentUid).eq("type", 2));
            List<String> likeComments = likeOrCollections.stream().map(WebLikeOrCollection::getLikeOrCollectionId).collect(Collectors.toList());

            Set<String> replyUids = twoCommentList.stream().map(WebComment::getReplyUid).collect(Collectors.toSet());
            Map<String, WebUser> replyUserMap = new HashMap<>(16);
            if (!replyUids.isEmpty()) {
                List<WebUser> replyUsers = userMapper.selectBatchIds(replyUids);
                replyUserMap = replyUsers.stream().collect(Collectors.toMap(WebUser::getId, user -> user));
            }
            List<CommentVo> twoCommentVos = new ArrayList<>();
            for (WebComment twoComment : twoCommentList) {
                CommentVo commentVo = ConvertUtils.sourceToTarget(twoComment, CommentVo.class);
                WebUser user = userMap.get(twoComment.getUid());
                commentVo.setUsername(user.getUsername())
                        .setAvatar(user.getAvatar())
                        .setTime(twoComment.getCreateTime().getTime())
                        .setIsLike(likeComments.contains(twoComment.getId()));
                WebUser replyUser = replyUserMap.get(twoComment.getReplyUid());
                if (replyUser != null) {
                    commentVo.setReplyUsername(replyUser.getUsername());
                }
                twoCommentVos.add(commentVo);
            }

            Map<String, List<CommentVo>> twoCommentVoMap = twoCommentVos.stream().collect(Collectors.groupingBy(CommentVo::getPid));
            List<CommentVo> commentVoList = new ArrayList<>();
            for (WebComment oneComment : oneCommentList) {
                CommentVo commentVo = ConvertUtils.sourceToTarget(oneComment, CommentVo.class);
                WebUser user = userMap.get(oneComment.getUid());
                commentVo.setUsername(user.getUsername())
                        .setAvatar(user.getAvatar())
                        .setTime(oneComment.getCreateTime().getTime())
                        .setIsLike(likeComments.contains(oneComment.getId()));
                List<CommentVo> children = twoCommentVoMap.get(oneComment.getId());

                if (children != null && children.size() > 3) {
                    children = children.subList(0, 3);
                }
                commentVo.setChildren(children);
                commentVoList.add(commentVo);
            }
            result.setRecords(commentVoList);
            result.setTotal(onetotal);
        }
        return result;
    }

    /**
     * 自动滚动到当前评论
     *
     * @param commentId 评论ID
     */
    @Override
    public Map<String, Object> scrollComment(String commentId) {
        Map<String, Object> resMap = new HashMap<>(16);
        WebComment comment = this.getById(commentId);
        String pid = comment.getPid();
        int page1 = 1;
        int page2 = 1;
        int limit1 = 7;
        int limit2 = 10;
        long total = 0;
        boolean flag = false;
        List<CommentVo> comments = new ArrayList<>();
        if ("0".equals(pid)) {
            while (!flag) {
                Page<CommentVo> allOneCommentPage = this.getCommentWithCommentByNoteId(page1, limit1, comment.getNid());
                List<CommentVo> commentVoList = allOneCommentPage.getRecords();
                List<String> pids = commentVoList.stream().map(CommentVo::getId).collect(Collectors.toList());
                if (pids.contains(commentId)) {
                    flag = true;
                    total = allOneCommentPage.getTotal();
                } else {
                    page1++;
                }
                comments.addAll(commentVoList);
            }
        } else {
            boolean flag2 = false;

            while (!flag) {
                IPage<CommentVo> allOneCommentPage = this.getCommentWithCommentByNoteId(page1, limit1, comment.getNid());
                List<CommentVo> commentVoList = allOneCommentPage.getRecords();
                List<String> pids = commentVoList.stream().map(CommentVo::getId).collect(Collectors.toList());
                if (pids.contains(pid)) {
                    for (CommentVo commentVo : commentVoList) {
                        if (Objects.equals(commentVo.getId(), pid)) {
                            List<CommentVo> comments2 = new ArrayList<>();
                            flag = true;
                            total = allOneCommentPage.getTotal();
                            while (!flag2) {
                                IPage<CommentVo> allTwoCommentPage = this.getTwoCommentByOneCommentId(page2, limit2, pid);
                                List<CommentVo> commentVoList2 = allTwoCommentPage.getRecords();
                                List<String> ids = commentVoList2.stream().map(CommentVo::getId).collect(Collectors.toList());
                                if (ids.contains(commentId)) {
                                    flag2 = true;
                                } else {
                                    page2++;
                                }
                                comments2.addAll(commentVoList2);
                            }
                            commentVo.setChildren(comments2);
                        }
                    }
                } else {
                    page1++;
                }
                comments.addAll(commentVoList);
            }
        }
        resMap.put("records", comments);
        resMap.put("total", total);
        resMap.put("page1", page1);
        resMap.put("page2", page2);
        return resMap;
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     */
    @Override
    public void deleteCommentById(String commentId) {

    }
}
