package com.hongshu.web.service;

import com.hongshu.common.core.domain.entity.SystemConfig;
import com.hongshu.web.domain.vo.SystemConfigVO;

import java.util.List;

/**
 * 系统配置表 服务类
 *
 * @Author hongshu
 */
public interface SystemConfigService extends SuperService<SystemConfig> {

    /**
     * 获取系统配置
     *
     * @return
     */
    SystemConfig getConfig();

    /**
     * 通过Key前缀清空Redis缓存
     *
     * @param key
     * @return
     */
    String cleanRedisByKey(List<String> key);

    /**
     * 修改系统配置
     *
     * @param systemConfigVO
     * @return
     */
    String editSystemConfig(SystemConfigVO systemConfigVO);

    /**
     * 获取系统配置中的搜索模式
     *
     * @return
     */
    String getSearchModel();

}
