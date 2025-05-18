package com.hongshu.web.service;

import com.hongshu.common.core.domain.Query;
import com.hongshu.web.domain.entity.WebLoginLog;

import java.util.List;

/**
 * 系统访问日志情况信息 服务层
 *
 * @Author hongshu
 */
public interface ISysLoginInforService {

    /**
     * 新增系统登录日志
     *
     * @param loginInfor 访问日志对象
     */
    void insertLoginInfor(WebLoginLog loginInfor);

    /**
     * 查询系统登录日志集合
     *
     * @param query 访问日志对象
     * @return 登录记录集合
     */
    List<WebLoginLog> selectLoginInforList(Query query);

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    int deleteLoginInforByIds(Long[] infoIds);

    /**
     * 清空系统登录日志
     */
    void cleanLoginInfor();
}
