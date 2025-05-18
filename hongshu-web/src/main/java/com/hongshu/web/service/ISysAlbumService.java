package com.hongshu.web.service;

import com.hongshu.common.core.domain.Query;
import com.hongshu.web.domain.entity.WebAlbum;

import java.util.List;

/**
 * 会员信息 服务层
 *
 * @Author hongshu
 */
public interface ISysAlbumService {

    /**
     * 查询会员信息集合
     *
     * @param query 会员信息
     */
    List<WebAlbum> getAlbumList(Query query);

    /**
     * 查询所有会员
     */
    List<WebAlbum> selectAlbumAll();

    /**
     * 通过会员ID查询会员信息
     *
     * @param id 会员ID
     */
    WebAlbum getAlbumById(Long id);

    /**
     * 删除会员信息
     *
     * @param id 会员ID
     */
    int deleteAlbumById(Long id);

    /**
     * 新增保存会员信息
     *
     * @param album 会员信息
     */
    int insertAlbum(WebAlbum album);

    /**
     * 修改保存会员信息
     *
     * @param album 会员信息
     */
    int updateAlbum(WebAlbum album);

    /**
     * 批量删除会员信息
     *
     * @param ids 需要删除的会员ID
     */
    int deleteAlbumByIds(Long[] ids);
}
