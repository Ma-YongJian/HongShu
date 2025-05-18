package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hongshu.common.core.domain.Query;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.common.validator.ValidatorUtil;
import com.hongshu.web.domain.entity.WebComment;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.CommentVO;
import com.hongshu.web.mapper.SysCommentMapper;
import com.hongshu.web.mapper.WebNoteMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.ISysCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 评论信息 服务层处理
 *
 * @Author hongshu
 */
@Slf4j
@Service
public class SysCommentServiceImpl implements ISysCommentService {

    @Autowired
    private SysCommentMapper commentMapper;
    @Autowired
    private WebUserMapper userMapper;
    @Autowired
    private WebNoteMapper noteMapper;


    /**
     * 获取所有一级分类列表
     *
     * @param query 评论信息
     */
    @Override
    public List<CommentVO> selectCommentList(Query query) {
        QueryWrapper<WebComment> qw = new QueryWrapper<>();
        qw.lambda()
                .eq(ValidatorUtil.isNotNull(query.getUid()), WebComment::getUid, query.getUid())
//                .like(ValidatorUtil.isNotNull(query.getNoteUid()), WebComment::getNoteUid, query.getNoteUid())
//                .like(ValidatorUtil.isNotNull(query.getContent()), WebComment::getContent, query.getContent())
                .eq(WebComment::getLevel, 1)
                .ge(ValidatorUtil.isNotNull(query.get("params[beginTime]")), WebComment::getCreateTime, query.get("params[beginTime]"))
                .le(ValidatorUtil.isNotNull(query.get("params[endTime]")), WebComment::getCreateTime, query.get("params[endTime]"))
                .orderByDesc(WebComment::getCreateTime);
        // 查询评论列表
        List<WebComment> commentList = commentMapper.selectList(qw);
        // 转换为 VO
        List<CommentVO> commentVOList = ConvertUtils.sourceToTarget(commentList, CommentVO.class);
        if (commentVOList.isEmpty()) {
            return commentVOList;
        }
        // 批量查询用户和笔记信息
        Set<String> userIds = commentVOList.stream()
                .flatMap(commentVo -> Stream.of(commentVo.getUid(), commentVo.getNoteUid()))
                .collect(Collectors.toSet());
        Map<String, WebUser> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(WebUser::getId, Function.identity()));
        Set<String> noteIds = commentVOList.stream()
                .map(CommentVO::getNid)
                .collect(Collectors.toSet());
        Map<String, WebNote> noteMap = noteMapper.selectBatchIds(noteIds).stream()
                .collect(Collectors.toMap(WebNote::getId, Function.identity()));
        // 设置关联数据
        for (CommentVO commentVo : commentVOList) {
            WebUser webUser = userMap.get(commentVo.getUid());
            if (webUser != null) {
                commentVo.setUsername(webUser.getUsername());
                commentVo.setAvatar(webUser.getAvatar());
            }
            WebUser replyUser = userMap.get(commentVo.getReplyUid());
            if (webUser != null) {
                commentVo.setReplyUsername(replyUser.getUsername());
                commentVo.setReplyAvatar(replyUser.getAvatar());
            }
            WebUser pushUser = userMap.get(commentVo.getNoteUid());
            if (pushUser != null) {
                commentVo.setPushUsername(pushUser.getUsername());
            }
            WebNote note = noteMap.get(commentVo.getNid());
            if (note != null) {
                commentVo.setTitle(note.getTitle());
                commentVo.setNoteCover(note.getNoteCover());
            }
        }
        return commentVOList;
    }


    /**
     * 查询一级以下评论
     *
     * @param query 评论信息
     */
    @Override
    public List<CommentVO> selectTreeList(Query query) {
        QueryWrapper<WebComment> qw = new QueryWrapper<>();
        qw.lambda()
                .eq(ValidatorUtil.isNotNull(query.getUid()), WebComment::getUid, query.getUid())
//                .like(ValidatorUtil.isNotNull(query.getNoteUid()), WebComment::getNoteUid, query.getNoteUid())
//                .like(ValidatorUtil.isNotNull(query.getContent()), WebComment::getContent, query.getContent())
                .eq(ValidatorUtil.isNotNull(query.get("nid")), WebComment::getNid, query.get("nid"))
                .ge(ValidatorUtil.isNotNull(query.get("params[beginTime]")), WebComment::getCreateTime, query.get("params[beginTime]"))
                .le(ValidatorUtil.isNotNull(query.get("params[endTime]")), WebComment::getCreateTime, query.get("params[endTime]"))
                .orderByDesc(WebComment::getCreateTime);
        // 查询评论列表
        List<WebComment> commentList = commentMapper.selectList(qw);
        // 转换为 VO
        List<CommentVO> commentVOList = ConvertUtils.sourceToTarget(commentList, CommentVO.class);
        if (commentVOList.isEmpty()) {
            return commentVOList;
        }
        // 批量查询用户和笔记信息
        Set<String> userIds = commentVOList.stream()
                .flatMap(commentVo -> Stream.of(commentVo.getUid(), commentVo.getNoteUid()))
                .collect(Collectors.toSet());
        Map<String, WebUser> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(WebUser::getId, Function.identity()));
        Set<String> noteIds = commentVOList.stream()
                .map(CommentVO::getNid)
                .collect(Collectors.toSet());
        Map<String, WebNote> noteMap = noteMapper.selectBatchIds(noteIds).stream()
                .collect(Collectors.toMap(WebNote::getId, Function.identity()));
        // 设置关联数据
        for (CommentVO commentVo : commentVOList) {
            WebUser webUser = userMap.get(commentVo.getUid());
            if (webUser != null) {
                commentVo.setUsername(webUser.getUsername());
                commentVo.setAvatar(webUser.getAvatar());
            }
            WebUser pushUser = userMap.get(commentVo.getNoteUid());
            if (pushUser != null) {
                commentVo.setPushUsername(pushUser.getUsername());
            }
            WebNote note = noteMap.get(commentVo.getNid());
            if (note != null) {
                commentVo.setTitle(note.getTitle());
                commentVo.setNoteCover(note.getNoteCover());
            }
        }
        return commentVOList;
    }

    /**
     * 通过评论ID查询评论信息
     *
     * @param id 评论ID
     */
    @Override
    public WebComment selectCommentById(Long id) {
        return commentMapper.selectById(id);
    }

    /**
     * 通过笔记ID查询评论信息
     *
     * @param nid 笔记ID
     */
    @Override
    public List<WebComment> selectCommentByNid(Long nid) {
        QueryWrapper<WebComment> qw = new QueryWrapper<>();
        qw.lambda().like(ValidatorUtil.isNotNull(nid), WebComment::getNid, nid);
        return commentMapper.selectList(qw);
    }

    /**
     * 批量删除评论信息
     *
     * @param ids 需要删除的评论ID
     */
    @Override
    public int deleteCommentByIds(Long[] ids) {
        List<Long> longs = Arrays.asList(ids);
        for (Long id : ids) {
            WebComment comment = selectCommentById(id);
            if (ValidatorUtil.isNull(comment)) {
                log.info("评论不存在:{}", id);
                longs.remove(id);
            }
        }
        return commentMapper.deleteBatchIds(longs);
    }

    @Override
    public Integer getCommentCount(int status) {
        QueryWrapper<WebComment> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq(BaseSQLConf.STATUS, status);
        return Math.toIntExact(commentMapper.selectCount(queryWrapper));
    }
}
