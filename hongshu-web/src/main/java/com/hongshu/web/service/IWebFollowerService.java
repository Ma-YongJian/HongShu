package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.entity.WebFollower;
import com.hongshu.web.domain.vo.FollowerVo;
import com.hongshu.web.domain.vo.TrendVo;

/**
 * 关注
 *
 * @author: hongshu
 */
public interface IWebFollowerService extends IService<WebFollower> {

    /**
     * 获取关注用户的所有动态
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    Page<TrendVo> getFollowTrend(long currentPage, long pageSize);

    /**
     * 获取关注列表
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    Page<TrendVo> getFollowList(long currentPage, long pageSize);

    /**
     * 获取当前用户所有的关注和粉丝
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param type        类型
     */
    Page<FollowerVo> getFriend(long currentPage, long pageSize, String uid, Integer type);

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
    Page<FollowerVo> getNoticeFollower(long currentPage, long pageSize);
}
