package com.hongshu.web.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.dto.LikeOrCollectionDTO;
import com.hongshu.web.domain.entity.WebLikeOrCollection;
import com.hongshu.web.domain.vo.LikeOrCollectionVo;

/**
 * 点赞/收藏
 *
 * @author: hongshu
 */
public interface IWebLikeOrCollectionService extends IService<WebLikeOrCollection> {

    /**
     * 点赞或收藏
     *
     * @param likeOrCollectionDTO 点赞收藏
     */
    void likeOrCollectionByDTO(LikeOrCollectionDTO likeOrCollectionDTO);

    /**
     * 是否点赞或收藏
     *
     * @param likeOrCollectionDTO 点赞收藏
     */
    boolean isLikeOrCollection(LikeOrCollectionDTO likeOrCollectionDTO);

    /**
     * 获取当前用户最新的点赞和收藏信息
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    Page<LikeOrCollectionVo> getNoticeLikeOrCollection(long currentPage, long pageSize);
}
