package com.hongshu.web.service;

import com.hongshu.common.core.domain.Query;
import com.hongshu.web.domain.entity.WebComment;
import com.hongshu.web.domain.vo.CommentVO;

import java.util.List;

/**
 * 评论信息 服务层
 *
 * @Author hongshu
 */
public interface ISysCommentService {

    /**
     * 获取所有一级分类列表
     *
     * @param query 评论信息
     */
    List<CommentVO> selectCommentList(Query query);

    /**
     * 查询一级以下评论
     *
     * @param query 评论信息
     */
    List<CommentVO> selectTreeList(Query query);

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
