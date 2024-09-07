package com.hongshu.web.service.sys;

import com.hongshu.common.core.domain.Query;
import com.hongshu.web.domain.entity.WebComment;
import com.hongshu.web.domain.vo.CommentVo;

import java.util.List;

/**
 * 评论信息 服务层
 *
 * @author: hongshu
 */
public interface ISysCommentService {

    /**
     * 查询评论信息集合
     *
     * @param query 评论信息
     */
    List<CommentVo> selectCommentList(Query query);

    /**
     * 通过评论ID查询评论信息
     *
     * @param id 评论ID
     */
    WebComment selectCommentById(Long id);

    /**
     * 通过笔记ID查询评论信息
     *
     * @param nid 笔记ID
     */
    List<WebComment> selectCommentByNid(Long nid);

    /**
     * 批量删除评论信息
     *
     * @param ids 需要删除的评论ID
     */
    int deleteCommentByIds(Long[] ids);

    Object getCommentCount(int enable);
}
