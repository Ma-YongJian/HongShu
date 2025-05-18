package com.hongshu.server.controller.web;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.enums.Result;
import com.hongshu.common.validator.ValidatorUtils;
import com.hongshu.common.validator.group.AddGroup;
import com.hongshu.web.domain.dto.CommentDTO;
import com.hongshu.web.domain.vo.CommentVO;
import com.hongshu.web.service.IWebCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 评论
 *
 * @Author hongshu
 */
@RequestMapping("/web/comment")
@RestController
public class WebCommentController {

    @Autowired
    private IWebCommentService commentService;


    /**
     * 获取所有一级分类
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param noteId      笔记ID
     */
    @GetMapping("getOneCommentByNoteId/{currentPage}/{pageSize}")
    public Result<?> getOneCommentByNoteId(@PathVariable long currentPage, @PathVariable long pageSize, String noteId) {
        Page<CommentVO> pageInfo = commentService.getOneCommentByNoteId(currentPage, pageSize, noteId);
        return Result.ok(pageInfo);
    }

    /**
     * 获取当前评论
     *
     * @param commentId 评论ID
     */
    @GetMapping("getCommentById")
    public Result<?> getCommentById(String commentId) {
        return Result.ok(commentService.getCommentById(commentId));
    }

    /**
     * 保存评论
     *
     * @param commentDTO 评论
     */
    @PostMapping("saveCommentByDTO")
    public Result<?> saveCommentByDTO(@RequestBody CommentDTO commentDTO) {
        ValidatorUtils.validateEntity(commentDTO, AddGroup.class);
        CommentVO commentVo = commentService.saveCommentByDTO(commentDTO);
        return Result.ok(commentVo);
    }

    /**
     * 根据评论ID同步评论集
     *
     * @param commentIds 评论ID数据集
     */
    @PostMapping("syncCommentByIds")
    public Result<?> syncCommentByIds(@RequestBody List<String> commentIds) {
        commentService.syncCommentByIds(commentIds);
        return Result.ok();
    }

    /**
     * 根据一级评论ID获取所有二级评论
     *
     * @param currentPage  当前页
     * @param pageSize     分页数
     * @param oneCommentId 一级评论ID
     */
    @GetMapping("getTwoCommentByOneCommentId/{currentPage}/{pageSize}")
    public Result<?> getTwoCommentByOneCommentId(@PathVariable long currentPage, @PathVariable long pageSize, String oneCommentId) {
        IPage<CommentVO> pageInfo = commentService.getTwoCommentByOneCommentId(currentPage, pageSize, oneCommentId);
        return Result.ok(pageInfo);
    }

    /**
     * 获取当前用户通知的评论集
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @GetMapping("getNoticeComment/{currentPage}/{pageSize}")
    public Result<?> getNoticeComment(@PathVariable long currentPage, @PathVariable long pageSize) {
        IPage<CommentVO> pageInfo = commentService.getNoticeComment(currentPage, pageSize);
        return Result.ok(pageInfo);
    }

    /**
     * 获取所有的一级评论并携带二级评论
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param noteId      笔记ID
     */
    @GetMapping("getCommentWithCommentByNoteId/{currentPage}/{pageSize}")
    public Result<?> getCommentWithCommentByNoteId(@PathVariable long currentPage, @PathVariable long pageSize, String noteId) {
        Page<CommentVO> pageInfo = commentService.getCommentWithCommentByNoteId(currentPage, pageSize, noteId);
        return Result.ok(pageInfo);
    }

    /**
     * 自动滚动到当前评论
     *
     * @param commentId 评论ID
     */
    @GetMapping("scrollComment")
    public Result<?> scrollComment(String commentId) {
        Map<String, Object> resMap = commentService.scrollComment(commentId);
        return Result.ok(resMap);
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     */
    @GetMapping("deleteCommentById")
    public Result<?> deleteCommentById(String commentId) {
        commentService.deleteCommentById(commentId);
        return Result.ok();
    }
}
