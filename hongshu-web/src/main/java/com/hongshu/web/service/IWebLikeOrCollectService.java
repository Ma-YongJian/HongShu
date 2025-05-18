package com.hongshu.web.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.dto.LikeOrCollectDTO;
import com.hongshu.web.domain.entity.WebLikeOrCollect;
import com.hongshu.web.domain.vo.LikeOrCollectVO;

/**
 * 点赞/收藏
 *
 * @Author hongshu
 */
public interface IWebLikeOrCollectService extends IService<WebLikeOrCollect> {

    /**
     * 点赞或收藏
     *
     * @param likeOrCollectDTO 点赞收藏
     */
    void likeOrCollectionByDTO(LikeOrCollectDTO likeOrCollectDTO);

    /**
     * 是否点赞或收藏
     *
     * @param likeOrCollectDTO 点赞收藏
     */
    boolean isLikeOrCollection(LikeOrCollectDTO likeOrCollectDTO);

    /**
     * 获取当前用户最新的点赞和收藏信息
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    Page<LikeOrCollectVO> getNoticeLikeOrCollection(long currentPage, long pageSize);
}
