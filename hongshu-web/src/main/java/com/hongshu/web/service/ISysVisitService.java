package com.hongshu.web.service;

import java.util.Map;

/**
 * 会员信息 服务层
 *
 * @Author hongshu
 */
public interface ISysVisitService {


    /**
     * 获取今日网站访问人数
     */
    int getWebVisitCount();

    /**
     * 获取近七天的访问量
     *
     * @return {
     * date: ["2019-6-20","2019-6-21","2019-6-22","2019-6-23","2019-6-24",,"2019-6-25","2019-6-26"]
     * pv: [10,5,6,7,5,3,2]
     * uv: [5,3,4,4,5,2,1]
     * }
     * 注：PV表示访问量   UV表示独立用户数
     */
    Map<String, Object> getVisitByWeek();

}
