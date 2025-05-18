package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.dto.AlbumDTO;
import com.hongshu.web.domain.entity.WebAlbum;
import com.hongshu.web.domain.vo.AlbumVO;

/**
 * 专辑
 *
 * @Author hongshu
 */
public interface IWebAlbumService extends IService<WebAlbum> {

    /**
     * 根据用户ID获取专辑
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param userId      用户ID
     */
    Page<WebAlbum> getAlbumByUserId(long currentPage, long pageSize, String userId);

    /**
     * 保存专辑
     *
     * @param albumDTO 专辑
     */
    void saveAlbum(AlbumDTO albumDTO);

    /**
     * 根据专辑ID获取专辑
     *
     * @param albumId 专辑ID
     */
    AlbumVO getAlbumById(String albumId);

    /**
     * 删除专辑
     *
     * @param albumId 专辑ID
     */
    void deleteAlbum(String albumId);

    /**
     * 更新专辑
     *
     * @param albumDTO 专辑
     */
    void updateAlbum(AlbumDTO albumDTO);
}
