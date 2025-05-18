package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.entity.WebFollow;
import com.hongshu.web.domain.vo.FollowVO;
import com.hongshu.web.domain.vo.TrendVO;

/**
 * 关注
 *
 * @Author hongshu
 */
public interface IWebFollowService extends IService<WebFollow> {

    /**
     * 获取关注用户的所有动态
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    Page<TrendVO> getFollowTrend(long currentPage, long pageSize);

    /**
     * 获取关注列表
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    Page<TrendVO> getFollowList(long currentPage, long pageSize);

    /**
     * 获取当前用户所有的关注和粉丝
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param type        类型
     */
    Page<FollowVO> getFriend(long currentPage, long pageSize, String uid, Integer type);

    /**
     * 关注用户
     *
     * @param followerId 关注用户ID
     */
    void followById(String followerId);

    /**
     * 当前用户是否关注
     *
     * @param followerId 关注的用户ID
     */
    boolean isFollow(String followerId);

    /**
     * 获取当前用户的最新关注信息
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    Page<FollowVO> getNoticeFollower(long currentPage, long pageSize);
}
