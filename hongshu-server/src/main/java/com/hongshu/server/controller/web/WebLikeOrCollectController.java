package com.hongshu.server.controller.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.enums.Result;
import com.hongshu.common.validator.ValidatorUtils;
import com.hongshu.common.validator.group.AddGroup;
import com.hongshu.web.domain.dto.LikeOrCollectDTO;
import com.hongshu.web.domain.vo.LikeOrCollectVO;
import com.hongshu.web.service.IWebLikeOrCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞/收藏
 *
 * @Author hongshu
 */
@RequestMapping("/web/likeOrCollection")
@RestController
public class WebLikeOrCollectController {

    @Autowired
    private IWebLikeOrCollectService likeOrCollectionService;


    /**
     * 点赞或收藏
     *
     * @param likeOrCollectDTO 点赞收藏实体
     */
    @PostMapping("likeOrCollectionByDTO")
    public Result<?> likeOrCollectionByDTO(@RequestBody LikeOrCollectDTO likeOrCollectDTO) {
        ValidatorUtils.validateEntity(likeOrCollectDTO, AddGroup.class);
        likeOrCollectionService.likeOrCollectionByDTO(likeOrCollectDTO);
        return Result.ok();
    }

    /**
     * 是否点赞或收藏
     *
     * @param likeOrCollectDTO 点赞收藏实体
     */
    @PostMapping("isLikeOrCollection")
    public Result<?> isLikeOrCollection(@RequestBody LikeOrCollectDTO likeOrCollectDTO) {
        boolean flag = likeOrCollectionService.isLikeOrCollection(likeOrCollectDTO);
        return Result.ok(flag);
    }

    /**
     * 获取当前用户最新的点赞和收藏信息
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @GetMapping("getNoticeLikeOrCollection/{currentPage}/{pageSize}")
    public Result<?> getNoticeLikeOrCollection(@PathVariable long currentPage, @PathVariable long pageSize) {
        Page<LikeOrCollectVO> pageInfo = likeOrCollectionService.getNoticeLikeOrCollection(currentPage, pageSize);
        return Result.ok(pageInfo);
    }
}
