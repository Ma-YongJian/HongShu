package com.hongshu.web.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hongshu.common.core.domain.Query;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.common.validator.ValidatorUtil;
import com.hongshu.web.domain.entity.WebComment;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.CommentVo;
import com.hongshu.web.mapper.WebNoteMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.mapper.sys.SysCommentMapper;
import com.hongshu.web.service.sys.ISysCommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 评论信息 服务层处理
 *
 * @author: hongshu
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
     * 查询评论信息集合
     *
     * @param query 评论信息
     */
    @Override
    public List<CommentVo> selectCommentList(Query query) {
        QueryWrapper<WebComment> qw = new QueryWrapper<>();
//        qw.lambda().like(ValidatorUtil.isNotNull(query.getUid()), WebComment::getUid, query.getUid());
//        qw.lambda().like(ValidatorUtil.isNotNull("noteUid"), WebComment::getNoteUid, "noteUid");
//        qw.lambda().like(ValidatorUtil.isNotNull("content"), WebComment::getContent, "content");
        qw.lambda().ge(ValidatorUtil.isNotNull(query.get("params[beginTime]")), WebComment::getCreateTime, query.get("params[beginTime]"));
        qw.lambda().le(ValidatorUtil.isNotNull(query.get("params[endTime]")), WebComment::getCreateTime, query.get("params[endTime]"));
        qw.orderByDesc("create_time");
        List<WebComment> commentList = commentMapper.selectList(qw);
        List<CommentVo> commentVoList = ConvertUtils.sourceToTarget(commentList, CommentVo.class);
        for (CommentVo commentVo : commentVoList) {
            WebUser webUser = userMapper.selectById(commentVo.getUid());
            commentVo.setUsername(webUser.getUsername());
            commentVo.setAvatar(webUser.getAvatar());
            WebUser user = userMapper.selectById(commentVo.getNoteUid());
            commentVo.setPushUsername(user.getUsername());
            commentVo.setAvatar(webUser.getAvatar());
            WebNote note = noteMapper.selectById(commentVo.getNid());
            commentVo.setTitle(note.getTitle());
        }
        return commentVoList;
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
