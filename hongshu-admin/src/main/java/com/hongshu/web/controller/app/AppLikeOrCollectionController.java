package com.hongshu.web.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.enums.Result;
import com.hongshu.common.validator.ValidatorUtils;
import com.hongshu.common.validator.group.AddGroup;
import com.hongshu.web.domain.dto.LikeOrCollectionDTO;
import com.hongshu.web.domain.vo.LikeOrCollectionVo;
import com.hongshu.web.service.IWebLikeOrCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞/收藏
 *
 * @author: hongshu
 */
@RequestMapping("/app/likeOrCollection")
@RestController
public class AppLikeOrCollectionController {

    @Autowired
    IWebLikeOrCollectionService likeOrCollectionService;


    /**
     * 点赞或收藏
     *
     * @param likeOrCollectionDTO 点赞收藏实体
     */
    @PostMapping("likeOrCollectionByDTO")
    public Result<?> likeOrCollectionByDTO(@RequestBody LikeOrCollectionDTO likeOrCollectionDTO) {
        ValidatorUtils.validateEntity(likeOrCollectionDTO, AddGroup.class);
        likeOrCollectionService.likeOrCollectionByDTO(likeOrCollectionDTO);
        return Result.ok();
    }

    /**
     * 是否点赞或收藏
     *
     * @param likeOrCollectionDTO 点赞收藏实体
     */
    @PostMapping("isLikeOrCollection")
    public Result<?> isLikeOrCollection(@RequestBody LikeOrCollectionDTO likeOrCollectionDTO) {
        boolean flag = likeOrCollectionService.isLikeOrCollection(likeOrCollectionDTO);
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
        Page<LikeOrCollectionVo> pageInfo = likeOrCollectionService.getNoticeLikeOrCollection(currentPage, pageSize);
        return Result.ok(pageInfo);
    }
}
