package com.hongshu.server.controller.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.enums.Result;
import com.hongshu.common.validator.ValidatorUtils;
import com.hongshu.common.validator.group.AddGroup;
import com.hongshu.common.validator.group.UpdateGroup;
import com.hongshu.web.domain.dto.AlbumDTO;
import com.hongshu.web.domain.entity.WebAlbum;
import com.hongshu.web.domain.vo.AlbumVO;
import com.hongshu.web.service.IWebAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 专辑
 *
 * @Author hongshu
 */
@RequestMapping("/web/album")
@RestController
public class WebAlbumController {

    @Autowired
    private IWebAlbumService albumService;


    /**
     * 根据用户ID获取专辑
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param userId      用户ID
     */
    @GetMapping("getAlbumByUserId/{currentPage}/{pageSize}")
    public Result<?> getAlbumByUserId(@PathVariable long currentPage, @PathVariable long pageSize, String userId) {
        Page<WebAlbum> page = albumService.getAlbumByUserId(currentPage, pageSize, userId);
        return Result.ok(page);
    }

    /**
     * 保存专辑
     *
     * @param albumDTO 专辑
     */
    @PostMapping("saveAlbum")
    public Result<?> saveAlbumByDTO(@RequestBody AlbumDTO albumDTO) {
        ValidatorUtils.validateEntity(albumDTO, AddGroup.class);
        albumService.saveAlbum(albumDTO);
        return Result.ok();
    }

    /**
     * 根据专辑ID获取专辑
     *
     * @param albumId 专辑ID
     */
    @GetMapping("getAlbumById")
    public Result<?> getAlbumById(String albumId) {
        AlbumVO albumVo = albumService.getAlbumById(albumId);
        return Result.ok(albumVo);
    }

    /**
     * 删除专辑
     *
     * @param albumId 专辑ID
     */
    @GetMapping("deleteAlbum")
    public Result<?> deleteAlbum(String albumId) {
        albumService.deleteAlbum(albumId);
        return Result.ok();
    }

    /**
     * 更新专辑
     *
     * @param albumDTO 专辑
     */
    @PostMapping("updateAlbum")
    public Result<?> updateAlbum(@RequestBody AlbumDTO albumDTO) {
        ValidatorUtils.validateEntity(albumDTO, UpdateGroup.class);
        albumService.updateAlbum(albumDTO);
        return Result.ok();
    }
}
