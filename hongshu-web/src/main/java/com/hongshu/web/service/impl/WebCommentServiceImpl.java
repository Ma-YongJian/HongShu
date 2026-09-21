package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.enums.ResultCodeEnum;
import com.hongshu.common.exception.web.HongshuException;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.web.auth.AuthContextHolder;
import com.hongshu.web.domain.dto.CommentDTO;
import com.hongshu.web.domain.entity.*;
import com.hongshu.web.domain.vo.CommentVO;
import com.hongshu.web.mapper.*;
import com.hongshu.web.service.IWebCommentService;
import com.hongshu.web.websocket.im.ChatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论
 *
 * @Author hongshu
 */
@Service
public class WebCommentServiceImpl extends ServiceImpl<WebCommentMapper, WebComment> implements IWebCommentService {

    @Autowired
    WebNoteMapper noteMapper;
    @Autowired
    WebUserMapper userMapper;
    @Autowired
    WebLikeOrCollectMapper likeOrCollectionMapper;
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
    public Page<CommentVO> getOneCommentByNoteId(long currentPage, long pageSize, String noteId) {
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
    public CommentVO saveCommentByDTO(CommentDTO commentDTO) {
        String currentUid = AuthContextHolder.getUserId();
        Date now = new Date();

        WebComment comment = ConvertUtils.sourceToTarget(commentDTO, WebComment.class);
        comment.setUid(currentUid);
        comment.setCreateTime(now);
        comment.setUpdateTime(now);
        if (comment.getPid() == null || comment.getPid().isEmpty()) {
            comment.setPid("0");
        }
        if (comment.getReplyId() == null || comment.getReplyId().isEmpty()) {
            comment.setReplyId("0");
        }
        if (comment.getReplyUid() == null || comment.getReplyUid().isEmpty()) {
            comment.setReplyUid("0");
        }
        if (comment.getLikeCount() == null) {
            comment.setLikeCount(0L);
        }
        if (comment.getTwoCommentCount() == null) {
            comment.setTwoCommentCount(0L);
        }
        if (comment.getSort() == null) {
            comment.setSort(0);
        }
        this.save(comment);

        WebNote note = noteMapper.selectById(commentDTO.getNid());
        if (note != null) {
            long count = note.getCommentCount() == null ? 0L : note.getCommentCount();
            note.setCommentCount(count + 1);
            noteMapper.updateById(note);
        }

        CommentVO commentVo = ConvertUtils.sourceToTarget(comment, CommentVO.class);
        WebUser user = userMapper.selectById(currentUid);
        if (user != null) {
            commentVo.setUsername(user.getUsername()).setAvatar(user.getAvatar());
        } else {
            commentVo.setUsername("用户");
        }
        commentVo.setTime(now.getTime());

        String pid = comment.getPid();
        if (pid != null && !"0".equals(pid)) {
            WebComment parentComment = this.getById(pid);
            if (parentComment != null) {
                long two = parentComment.getTwoCommentCount() == null ? 0L : parentComment.getTwoCommentCount();
                parentComment.setTwoCommentCount(two + 1);
                this.updateById(parentComment);
            }
        }

        notifyComment(comment, currentUid);
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
        // 已改为只使用 web_comment，不再从同步表拷贝
    }

    /**
     * 根据一级评论ID获取所有的二级评论
     *
     * @param currentPage  当前页
     * @param pageSize     分页数
     * @param oneCommentId 一级评论ID
     */
    @Override
    public Page<CommentVO> getTwoCommentByOneCommentId(long currentPage, long pageSize, String oneCommentId) {
        Page<CommentVO> result = new Page<>();
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

            List<CommentVO> commentVOS = new ArrayList<>();
            List<WebLikeOrCollect> likeOrCollections = new ArrayList<>();
            if (currentUid != null && !currentUid.isEmpty()) {
                likeOrCollections = likeOrCollectionMapper.selectList(new QueryWrapper<WebLikeOrCollect>().eq("uid", currentUid).eq("type", 2));
            }
            List<String> likeComments = likeOrCollections.stream().map(WebLikeOrCollect::getLikeOrCollectionId).collect(Collectors.toList());
            for (WebComment comment : twoCommentList) {
                CommentVO commentVo = ConvertUtils.sourceToTarget(comment, CommentVO.class);
                WebUser user = userMap.get(comment.getUid());
                if (user != null) {
                    commentVo.setUsername(user.getUsername()).setAvatar(user.getAvatar());
                } else {
                    commentVo.setUsername("用户");
                }
                commentVo.setTime(comment.getCreateTime() != null ? comment.getCreateTime().getTime() : System.currentTimeMillis())
                        .setIsLike(likeComments.contains(comment.getId()));
                WebUser replyUser = replyUserMap.get(comment.getReplyUid());
                if (replyUser != null) {
                    commentVo.setReplyUsername(replyUser.getUsername());
                }
                commentVOS.add(commentVo);
            }
            result.setRecords(commentVOS);
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
    public IPage<CommentVO> getNoticeComment(long currentPage, long pageSize) {
        Page<CommentVO> result = new Page<>();
        String currentUid = AuthContextHolder.getUserId();

        Page<WebComment> commentPage = this.page(new Page<>((int) currentPage, (int) pageSize), new QueryWrapper<WebComment>().or(e -> e.eq("note_uid", currentUid).or().eq("reply_uid", currentUid)).ne("uid", currentUid).orderByDesc("create_time"));

        List<WebComment> commentList = commentPage.getRecords();
        long total = commentPage.getTotal();

        List<CommentVO> commentVOList = new ArrayList<>();
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
                CommentVO commentVo = ConvertUtils.sourceToTarget(comment, CommentVO.class);
                WebUser user = userMap.get(comment.getUid());
                WebNote note = noteMap.get(comment.getNid());
                if (user != null) {
                    commentVo.setUsername(user.getUsername()).setAvatar(user.getAvatar());
                }
                commentVo.setTime(comment.getCreateTime() == null ? 0L : comment.getCreateTime().getTime());
                if (note != null) {
                    commentVo.setNoteCover(note.getNoteCover()).setTitle(note.getTitle());
                }

                if (!"0".equals(comment.getPid())) {
                    WebComment replyComment = replyCommentMap.get(comment.getReplyId());
                    if (replyComment != null) {
                        commentVo.setReplyContent(replyComment.getContent());
                    }
                    if (!comment.getReplyUid().equals(currentUid)) {
                        WebUser replyUser = userMap.get(comment.getReplyUid());
                        if (replyUser != null) {
                            commentVo.setReplyUsername(replyUser.getUsername());
                        }
                    }
                }
                commentVOList.add(commentVo);
            }
        }
        result.setRecords(commentVOList);
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
    public Page<CommentVO> getCommentWithCommentByNoteId(long currentPage, long pageSize, String noteId) {
        Page<CommentVO> result = new Page<>();
        Page<WebComment> oneCommentPage = this.page(new Page<>((int) currentPage, (int) pageSize),
                new QueryWrapper<WebComment>().eq("nid", noteId).and(w -> w.eq("pid", "0").or().eq("pid", 0)).orderByDesc("like_count"));
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

            List<WebLikeOrCollect> likeOrCollections = new ArrayList<>();
            if (currentUid != null && !currentUid.isEmpty()) {
                likeOrCollections = likeOrCollectionMapper.selectList(new QueryWrapper<WebLikeOrCollect>().eq("uid", currentUid).eq("type", 2));
            }
            List<String> likeComments = likeOrCollections.stream().map(WebLikeOrCollect::getLikeOrCollectionId).collect(Collectors.toList());

            Set<String> replyUids = twoCommentList.stream().map(WebComment::getReplyUid).collect(Collectors.toSet());
            Map<String, WebUser> replyUserMap = new HashMap<>(16);
            if (!replyUids.isEmpty()) {
                List<WebUser> replyUsers = userMapper.selectBatchIds(replyUids);
                replyUserMap = replyUsers.stream().collect(Collectors.toMap(WebUser::getId, user -> user));
            }
            List<CommentVO> twoCommentVOS = new ArrayList<>();
            for (WebComment twoComment : twoCommentList) {
                CommentVO commentVo = ConvertUtils.sourceToTarget(twoComment, CommentVO.class);
                WebUser user = userMap.get(twoComment.getUid());
                if (user != null) {
                    commentVo.setUsername(user.getUsername()).setAvatar(user.getAvatar());
                } else {
                    commentVo.setUsername("用户");
                }
                commentVo.setTime(twoComment.getCreateTime() != null ? twoComment.getCreateTime().getTime() : System.currentTimeMillis())
                        .setIsLike(likeComments.contains(twoComment.getId()));
                WebUser replyUser = replyUserMap.get(twoComment.getReplyUid());
                if (replyUser != null) {
                    commentVo.setReplyUsername(replyUser.getUsername());
                }
                twoCommentVOS.add(commentVo);
            }

            Map<String, List<CommentVO>> twoCommentVoMap = twoCommentVOS.stream().collect(Collectors.groupingBy(CommentVO::getPid));
            List<CommentVO> commentVOList = new ArrayList<>();
            for (WebComment oneComment : oneCommentList) {
                CommentVO commentVo = ConvertUtils.sourceToTarget(oneComment, CommentVO.class);
                WebUser user = userMap.get(oneComment.getUid());
                if (user != null) {
                    commentVo.setUsername(user.getUsername()).setAvatar(user.getAvatar());
                } else {
                    commentVo.setUsername("用户");
                }
                commentVo.setTime(oneComment.getCreateTime() != null ? oneComment.getCreateTime().getTime() : System.currentTimeMillis())
                        .setIsLike(likeComments.contains(oneComment.getId()));
                List<CommentVO> children = twoCommentVoMap.get(oneComment.getId());
                long replyTotal = children == null ? 0 : children.size();
                if (commentVo.getTwoCommentCount() == null) {
                    commentVo.setTwoCommentCount(replyTotal);
                }
                if (children != null && children.size() > 3) {
                    children = children.subList(0, 3);
                }
                commentVo.setChildren(children);
                commentVOList.add(commentVo);
            }
            result.setRecords(commentVOList);
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
        List<CommentVO> comments = new ArrayList<>();
        if ("0".equals(pid)) {
            while (!flag) {
                Page<CommentVO> allOneCommentPage = this.getCommentWithCommentByNoteId(page1, limit1, comment.getNid());
                List<CommentVO> commentVOList = allOneCommentPage.getRecords();
                List<String> pids = commentVOList.stream().map(CommentVO::getId).collect(Collectors.toList());
                if (pids.contains(commentId)) {
                    flag = true;
                    total = allOneCommentPage.getTotal();
                } else {
                    page1++;
                }
                comments.addAll(commentVOList);
            }
        } else {
            boolean flag2 = false;

            while (!flag) {
                IPage<CommentVO> allOneCommentPage = this.getCommentWithCommentByNoteId(page1, limit1, comment.getNid());
                List<CommentVO> commentVOList = allOneCommentPage.getRecords();
                List<String> pids = commentVOList.stream().map(CommentVO::getId).collect(Collectors.toList());
                if (pids.contains(pid)) {
                    for (CommentVO commentVo : commentVOList) {
                        if (Objects.equals(commentVo.getId(), pid)) {
                            List<CommentVO> comments2 = new ArrayList<>();
                            flag = true;
                            total = allOneCommentPage.getTotal();
                            while (!flag2) {
                                IPage<CommentVO> allTwoCommentPage = this.getTwoCommentByOneCommentId(page2, limit2, pid);
                                List<CommentVO> commentVOList2 = allTwoCommentPage.getRecords();
                                List<String> ids = commentVOList2.stream().map(CommentVO::getId).collect(Collectors.toList());
                                if (ids.contains(commentId)) {
                                    flag2 = true;
                                } else {
                                    page2++;
                                }
                                comments2.addAll(commentVOList2);
                            }
                            commentVo.setChildren(comments2);
                        }
                    }
                } else {
                    page1++;
                }
                comments.addAll(commentVOList);
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
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCommentById(String commentId) {
        String currentUid = AuthContextHolder.getUserId();
        if (StringUtils.isBlank(currentUid) || StringUtils.isBlank(commentId)) {
            throw new HongshuException(ResultCodeEnum.TOKEN_FAIL);
        }
        WebComment comment = this.getById(commentId);
        if (comment == null) {
            return;
        }
        if (!currentUid.equals(comment.getUid()) && !currentUid.equals(comment.getNoteUid())) {
            throw new HongshuException("只能删除自己的评论");
        }
        LinkedHashSet<String> ids = new LinkedHashSet<>();
        ArrayDeque<String> queue = new ArrayDeque<>();
        queue.add(commentId);
        while (!queue.isEmpty()) {
            String id = queue.poll();
            if (!ids.add(id)) {
                continue;
            }
            List<WebComment> children = this.list(new QueryWrapper<WebComment>().eq("pid", id));
            for (WebComment child : children) {
                if (child != null && child.getId() != null) {
                    queue.add(child.getId());
                }
            }
        }
        likeOrCollectionMapper.delete(new QueryWrapper<WebLikeOrCollect>()
                .in("like_or_collection_id", ids));
        this.removeBatchByIds(ids);
        WebNote note = noteMapper.selectById(comment.getNid());
        if (note != null) {
            long count = note.getCommentCount() == null ? 0L : note.getCommentCount();
            note.setCommentCount(Math.max(0L, count - ids.size()));
            noteMapper.updateById(note);
        }
        String pid = comment.getPid();
        if (pid != null && !"0".equals(pid)) {
            WebComment parent = this.getById(pid);
            if (parent != null) {
                long two = parent.getTwoCommentCount() == null ? 0L : parent.getTwoCommentCount();
                parent.setTwoCommentCount(Math.max(0L, two - ids.size()));
                this.updateById(parent);
            }
        }
    }

    private void notifyComment(WebComment comment, String currentUid) {
        if (comment == null || currentUid == null || currentUid.equals(comment.getNoteUid())) {
            return;
        }
        String noteUid = comment.getNoteUid();
        String replyUid = comment.getReplyUid();
        if (noteUid != null && !noteUid.isEmpty() && !Objects.equals(replyUid, noteUid)) {
            chatUtils.sendMessage(noteUid, 1);
        }
        if (replyUid != null && !replyUid.isEmpty() && !"0".equals(replyUid) && !currentUid.equals(replyUid)) {
            chatUtils.sendMessage(replyUid, 1);
        }
    }
}
