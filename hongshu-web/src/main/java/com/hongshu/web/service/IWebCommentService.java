package com.hongshu.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.dto.CommentDTO;
import com.hongshu.web.domain.entity.WebComment;
import com.hongshu.web.domain.vo.CommentVO;

import java.util.List;
import java.util.Map;

/**
 * 评论
 *
 * @Author hongshu
 */
public interface IWebCommentService extends IService<WebComment> {

    /**
     * 获取所有一级分类
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param noteId      笔记ID
     */
    Page<CommentVO> getOneCommentByNoteId(long currentPage, long pageSize, String noteId);

    /**
     * 根据评论ID获取当前评论
     *
     * @param commentId 评论ID
     */
    Object getCommentById(String commentId);

    /**
     * 保存评论
     *
     * @param commentDTO 评论
     */
    CommentVO saveCommentByDTO(CommentDTO commentDTO);

    /**
     * 根据评论ID同步评论集
     *
     * @param commentIds 评论ID数据集
     */
    void syncCommentByIds(List<String> commentIds);

    /**
     * 根据一级评论ID获取所有的二级评论
     *
     * @param currentPage  当前页
     * @param pageSize     分页数
     * @param oneCommentId 一级评论ID
     */
    Page<CommentVO> getTwoCommentByOneCommentId(long currentPage, long pageSize, String oneCommentId);

    /**
     * 获取当前用户通知的评论集
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    IPage<CommentVO> getNoticeComment(long currentPage, long pageSize);

    /**
     * 获取所有的一级评论并携带二级评论
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param noteId      笔记ID
     */
    Page<CommentVO> getCommentWithCommentByNoteId(long currentPage, long pageSize, String noteId);

    /**
     * 自动滚动到当前评论
     *
     * @param commentId 评论ID
     */
    Map<String, Object> scrollComment(String commentId);

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     */
    void deleteCommentById(String commentId);
}
