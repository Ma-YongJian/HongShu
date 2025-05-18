package com.hongshu.web.service;

import com.hongshu.common.core.domain.Query;

import java.util.List;
import java.util.Map;

/**
 * 统计接口
 *
 * @Author hongshu
 * @date: 2023/1/13
 * @version: 1.0.0
 */
public interface ISysStatisticsService {

    /**
     * 获取首页总数据
     *
     * @param query 查询条件
     * @return
     */
    Map<String, Object> getTotalData(Query query);

    /**
     * 获取折线图数据
     *
     * @param query 查询条件
     * @return
     */
    List<Map<String, Object>> getLineData(Query query);

    /**
     * 获取雷达图数据
     *
     * @param query 查询条件
     * @return
     */
    List<List<Object>> getRaddarData(Query query);

    /**
     * 获取饼图数据
     *
     * @param query 查询条件
     * @return
     */
    Map<String, Object> getPieData(Query query);

    /**
     * 获取柱状图总数据
     *
     * @param query 查询条件
     * @return
     */
    List<Map<String, Object>> getBarData(Query query);

}
