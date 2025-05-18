package com.hongshu.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hongshu.web.domain.entity.WebVisit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 会员信息 数据层
 *
 * @Author hongshu
 */
@Mapper
public interface SysVisitMapper extends BaseMapper<WebVisit> {

    /**
     * 获取IP数目
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT COUNT(ip) FROM (SELECT ip FROM web_visit WHERE create_time >= #{startTime} AND create_time <= #{endTime} GROUP BY ip) AS tmp")
    Integer getIpCount(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 统计最近七天内的访问量(PV)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT DISTINCT DATE_FORMAT(create_time, '%Y-%m-%d') DATE, COUNT(id) COUNT FROM  web_visit where create_time >= #{startTime} AND create_time <= #{endTime} GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d')")
    List<Map<String, Object>> getPVByWeek(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 统计七天内独立用户数(UV) 目前通过IP来统计
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT DATE, COUNT(ip) COUNT FROM (SELECT DISTINCT DATE_FORMAT(create_time, '%Y-%m-%d') DATE, ip FROM web_visit WHERE create_time >= #{startTime} AND create_time <= #{endTime} GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d'),ip) AS tmp GROUP BY DATE ")
    List<Map<String, Object>> getUVByWeek(@Param("startTime") String startTime, @Param("endTime") String endTime);

}
