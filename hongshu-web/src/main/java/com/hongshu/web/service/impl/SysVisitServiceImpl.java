package com.hongshu.web.service.impl;

import com.hongshu.common.constant.Constantss;
import com.hongshu.common.global.RedisConf;
import com.hongshu.common.utils.DateUtilss;
import com.hongshu.common.utils.JsonUtils;
import com.hongshu.common.utils.RedisUtil;
import com.hongshu.web.mapper.SysLoginInforMapper;
import com.hongshu.web.mapper.SysVisitMapper;
import com.hongshu.web.service.ISysVisitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 会员信息 服务层处理
 *
 * @Author hongshu
 */
@Slf4j
@Service
public class SysVisitServiceImpl implements ISysVisitService {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SysVisitMapper visitMapper;
    @Autowired
    private SysLoginInforMapper loginInforMapper;


    @Override
    public int getWebVisitCount() {
        // 获取今日开始和结束时间
        String startTime = DateUtilss.getToDayStartTime();
        String endTime = DateUtilss.getToDayEndTime();
//        return visitMapper.getIpCount(startTime, endTime);
        return loginInforMapper.getIpCount(startTime, endTime);
    }

    @Override
    public Map<String, Object> getVisitByWeek() {
        // 从Redis中获取一周访问量
//        String weekVisitJson = redisUtil.get(RedisConf.DASHBOARD + Constantss.SYMBOL_COLON + RedisConf.WEEK_VISIT);
//        if (StringUtilss.isNotEmpty(weekVisitJson)) {
//            Map<String, Object> weekVisitMap = JsonUtils.jsonToMap(weekVisitJson);
//            return weekVisitMap;
//        }

        // 获取到今天结束的时间
        String todayEndTime = DateUtilss.getToDayEndTime();
        //获取最近七天的日期
        Date sevenDaysDate = DateUtilss.getDate(todayEndTime, -6);
        String sevenDays = DateUtilss.getOneDayStartTime(sevenDaysDate);
        // 获取最近七天的数组列表
        List<String> sevenDaysList = DateUtilss.getDaysByN(7, "yyyy-MM-dd");
        // 获得最近七天的访问量
        List<Map<String, Object>> pvMap = visitMapper.getPVByWeek(sevenDays, todayEndTime);
        // 获得最近七天的独立用户
        List<Map<String, Object>> uvMap = visitMapper.getUVByWeek(sevenDays, todayEndTime);

        Map<String, Object> countPVMap = new HashMap<>();
        Map<String, Object> countUVMap = new HashMap<>();

        for (Map<String, Object> item : pvMap) {
            countPVMap.put(item.get("DATE").toString(), item.get("COUNT"));
        }
        for (Map<String, Object> item : uvMap) {
            countUVMap.put(item.get("DATE").toString(), item.get("COUNT"));
        }
        // 访问量数组
        List<Integer> pvList = new ArrayList<>();
        // 独立用户数组
        List<Integer> uvList = new ArrayList<>();

        for (String day : sevenDaysList) {
            if (countPVMap.get(day) != null) {
                Number pvNumber = (Number) countPVMap.get(day);
                Number uvNumber = (Number) countUVMap.get(day);
                pvList.add(pvNumber.intValue());
                uvList.add(uvNumber.intValue());
            } else {
                pvList.add(0);
                uvList.add(0);
            }
        }
        Map<String, Object> resultMap = new HashMap<>(Constantss.NUM_THREE);
        // 不含年份的数组格式
        List<String> resultSevenDaysList = DateUtilss.getDaysByN(7, "MM-dd");
        resultMap.put("date", resultSevenDaysList);
        resultMap.put("pv", pvList);
        resultMap.put("uv", uvList);

        //TODO 可能会存在短期的数据不一致的问题，即零点时不能准时更新，而是要在0:10才会重新刷新纪录。 后期考虑加入定时器处理这个问题
        // 将一周访问量存入Redis中【过期时间10分钟】
        redisUtil.setEx(RedisConf.DASHBOARD + Constantss.SYMBOL_COLON + RedisConf.WEEK_VISIT, JsonUtils.objectToJson(resultMap), 10, TimeUnit.MINUTES);
        return resultMap;
    }
}
