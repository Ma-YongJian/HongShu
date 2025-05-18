package com.hongshu.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hongshu.web.domain.entity.WebLoginLog;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 系统访问日志情况信息 数据层
 *
 * @Author hongshu
 */
@Mapper
public interface SysLoginInforMapper extends BaseMapper<WebLoginLog> {

    /**
     * 获取IP数目
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT COUNT(ipaddr) FROM (SELECT ipaddr FROM web_login_log WHERE create_time >= #{startTime} AND create_time <= #{endTime} GROUP BY ipaddr) AS tmp")
    Integer getIpCount(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Delete("DELETE FROM web_login_log")
    void cleanLoginInfor();

//    /**
//     * 新增系统登录日志
//     *
//     * @param loginInfor 访问日志对象
//     */
//    void insertLoginInfor(WebLoginInfor loginInfor);
//
//    /**
//     * 查询系统登录日志集合
//     *
//     * @param loginInfor 访问日志对象
//     * @return 登录记录集合
//     */
//    List<WebLoginInfor> selectLoginInforList(WebLoginInfor loginInfor);
//
//    /**
//     * 批量删除系统登录日志
//     *
//     * @param infoIds 需要删除的登录日志ID
//     * @return 结果
//     */
//    int deleteLoginInforByIds(Long[] infoIds);
//
//    /**
//     * 清空系统登录日志
//     *
//     */
//    void cleanLoginInfor();
}
