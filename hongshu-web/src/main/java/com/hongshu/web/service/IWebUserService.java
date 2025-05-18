package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.NoteSearchVO;

import java.util.List;

/**
 * 用户
 *
 * @Author hongshu
 */
public interface IWebUserService extends IService<WebUser> {

    /**
     * 获取当前用户信息
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param userId      用户ID
     * @param type        类型
     */
    Page<NoteSearchVO> getTrendByUser(long currentPage, long pageSize, String userId, Integer type);

    WebUser getUserById(String userId);

    /**
     * 更新用户信息
     *
     * @param user 用户
     */
    WebUser updateUser(WebUser user);

    /**
     * 查找用户信息
     *
     * @param keyword 关键词
     */
    Page<WebUser> getUserByKeyword(long currentPage, long pageSize, String keyword);

    /**
     * 保存用户的搜索记录
     *
     * @param keyword 关键词
     */
    void saveUserSearchRecord(String keyword);

    /**
     * 根据条件分页查询会员数据
     *
     * @param user 会员
     */
    List<WebUser> getUserList(WebUser user);

}
