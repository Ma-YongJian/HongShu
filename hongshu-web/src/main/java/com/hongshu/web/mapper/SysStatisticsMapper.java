package com.hongshu.web.mapper;

import com.hongshu.common.core.domain.Query;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 统计接口 数据层
 *
 * @Author hongshu
 * @date: 2023/1/13
 * @version: 1.0.0
 */
@Mapper
public interface SysStatisticsMapper {

    /**
     * 获取首页总数据
     *
     * @param query 查询条件
     * @return
     */
    Map<String, Object> getTotalData(@Param("q") Query query);

    /**
     * 获取折线图数据
     *
     * @param query 查询条件
     * @return
     */
    List<Map<String, Object>> getLineData(@Param("q") Query query);

    /**
     * 获取雷达图数据
     *
     * @param query 查询条件
     * @return
     */
    List<Map<String, Object>> getRaddarData(@Param("q") Query query);

    /**
     * 获取饼图数据
     *
     * @param query 查询条件
     * @return
     */
    Map<String, Object> getPieData(@Param("q") Query query);

    /**
     * 获取柱状图总数据
     *
     * @param query 查询条件
     * @return
     */
    List<Map<String, Object>> getBarData(@Param("q") Query query);

}
