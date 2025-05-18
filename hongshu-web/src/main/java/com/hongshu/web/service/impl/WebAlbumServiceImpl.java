package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.web.domain.dto.AlbumDTO;
import com.hongshu.web.domain.entity.WebAlbum;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.AlbumVO;
import com.hongshu.web.mapper.WebAlbumMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.IWebAlbumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 专辑
 *
 * @Author hongshu
 */
@Slf4j
@Service
public class WebAlbumServiceImpl extends ServiceImpl<WebAlbumMapper, WebAlbum> implements IWebAlbumService {

    @Autowired
    private WebUserMapper userMapper;


    /**
     * 根据用户ID获取专辑
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param userId      用户ID
     */
    @Override
    public Page<WebAlbum> getAlbumByUserId(long currentPage, long pageSize, String userId) {
        return this.page(new Page<>(currentPage, pageSize), new QueryWrapper<WebAlbum>().eq("uid", userId).orderByDesc("update_time"));
    }

    /**
     * 保存专辑
     *
     * @param albumDTO 专辑
     */
    @Override
    public void saveAlbum(AlbumDTO albumDTO) {
        WebAlbum album = ConvertUtils.sourceToTarget(albumDTO, WebAlbum.class);
        this.save(album);
    }

    /**
     * 根据专辑ID获取专辑
     *
     * @param albumId 专辑ID
     */
    @Override
    public AlbumVO getAlbumById(String albumId) {
        WebAlbum album = this.getById(albumId);
        AlbumVO albumVo = ConvertUtils.sourceToTarget(album, AlbumVO.class);
        WebUser user = userMapper.selectById(album.getUid());
        albumVo.setUsername(user.getUsername());
        albumVo.setAvatar(user.getAvatar());
        return albumVo;
    }


    /**
     * 删除专辑
     *
     * @param albumId 专辑ID
     */
    @Override
    public void deleteAlbum(String albumId) {

    }

    /**
     * 更新专辑
     *
     * @param albumDTO 专辑
     */
    @Override
    public void updateAlbum(AlbumDTO albumDTO) {
        WebAlbum album = ConvertUtils.sourceToTarget(albumDTO, WebAlbum.class);
        this.updateById(album);
    }
}
