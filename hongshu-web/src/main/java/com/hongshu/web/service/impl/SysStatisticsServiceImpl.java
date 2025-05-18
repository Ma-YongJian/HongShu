package com.hongshu.web.service.impl;

import com.hongshu.common.core.domain.Query;
import com.hongshu.common.utils.DateUtil;
import com.hongshu.web.mapper.SysStatisticsMapper;
import com.hongshu.web.service.ISysStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 统计接口实现类
 *
 * @Author hongshu
 */
@Service
public class SysStatisticsServiceImpl implements ISysStatisticsService {

    @Autowired
    private SysStatisticsMapper statisticsMapper;


    /**
     * 获取首页总数据
     *
     * @param query 查询条件
     * @return
     */
    @Override
    public Map<String, Object> getTotalData(Query query) {
        return statisticsMapper.getTotalData(query);
    }

    /**
     * 获取折线图数据
     *
     * @param query 查询条件
     * @return
     */
    @Override
    public List<Map<String, Object>> getLineData(Query query) {
        return statisticsMapper.getLineData(query);
    }

    /**
     * 获取雷达图数据
     *
     * @param query 查询条件
     * @return
     */
    @Override
    public List<List<Object>> getRaddarData(Query query) {
        List<Map<String, Object>> lists = statisticsMapper.getRaddarData(query);
        List<List<Object>> result = new ArrayList<>();
        lists.stream().forEach(v -> {
            List<Object> object = new ArrayList<>();
            String date = String.valueOf(v.get("date"));
            object.add(DateUtil.getCurrentWeek(DateUtil.parseLocalDate(date).getDayOfWeek().getValue()));
            object.add(v.get("chatCount"));
            object.add(v.get("drawCount"));
            result.add(object);
        });
        return result;
    }

    /**
     * 获取饼图数据
     *
     * @param query 查询条件
     * @return
     */
    @Override
    public Map<String, Object> getPieData(Query query) {
        return statisticsMapper.getPieData(query);
    }

    /**
     * 获取柱状图总数据
     *
     * @param query 查询条件
     * @return
     */
    @Override
    public List<Map<String, Object>> getBarData(Query query) {
        return statisticsMapper.getBarData(query);
    }

}
