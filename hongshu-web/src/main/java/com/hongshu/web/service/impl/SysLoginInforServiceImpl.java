package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hongshu.common.core.domain.Query;
import com.hongshu.common.validator.ValidatorUtil;
import com.hongshu.web.domain.entity.WebLoginLog;
import com.hongshu.web.mapper.SysLoginInforMapper;
import com.hongshu.web.service.ISysLoginInforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @Author hongshu
 */
@Service
public class SysLoginInforServiceImpl implements ISysLoginInforService {

    @Autowired
    private SysLoginInforMapper loginInforMapper;


    /**
     * 新增系统登录日志
     *
     * @param loginInfor 访问日志对象
     */
    @Override
    public void insertLoginInfor(WebLoginLog loginInfor) {
        loginInforMapper.insert(loginInfor);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param query 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<WebLoginLog> selectLoginInforList(Query query) {
        QueryWrapper<WebLoginLog> qw = new QueryWrapper<>();
        qw.lambda().like(ValidatorUtil.isNotNull(query.get("ipaddr")), WebLoginLog::getIpaddr, query.get("ipaddr"));
        qw.lambda().like(ValidatorUtil.isNotNull(query.get("username")), WebLoginLog::getUsername, query.get("username"));
        qw.lambda().like(ValidatorUtil.isNotNull(query.getStatus()), WebLoginLog::getStatus, query.getStatus());
        qw.lambda().ge(ValidatorUtil.isNotNull(query.get("params[beginTime]")), WebLoginLog::getCreateTime, query.get("params[beginTime]"));
        qw.lambda().le(ValidatorUtil.isNotNull(query.get("params[endTime]")), WebLoginLog::getCreateTime, query.get("params[endTime]"));
        qw.orderByDesc("login_time");

        return loginInforMapper.selectList(qw);
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果
     */
    @Override
    public int deleteLoginInforByIds(Long[] infoIds) {
        return loginInforMapper.deleteBatchIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLoginInfor() {
        loginInforMapper.cleanLoginInfor();
    }
}
