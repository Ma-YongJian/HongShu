package com.hongshu.web.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hongshu.common.core.domain.Query;
import com.hongshu.common.validator.ValidatorUtil;
import com.hongshu.web.domain.entity.WebLoginInfor;
import com.hongshu.web.mapper.sys.SysLoginInforMapper;
import com.hongshu.web.service.sys.ISysLoginInforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author hongshu
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
    public void insertLoginInfor(WebLoginInfor loginInfor) {
        loginInforMapper.insert(loginInfor);
    }

    /**
     * 查询系统登录日志集合
     *
     * @param query 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<WebLoginInfor> selectLoginInforList(Query query) {
        QueryWrapper<WebLoginInfor> qw = new QueryWrapper<>();
        qw.lambda().like(ValidatorUtil.isNotNull(query.get("ipaddr")), WebLoginInfor::getIpaddr, query.get("ipaddr"));
        qw.lambda().like(ValidatorUtil.isNotNull(query.get("username")), WebLoginInfor::getUsername, query.get("username"));
        qw.lambda().like(ValidatorUtil.isNotNull(query.getStatus()), WebLoginInfor::getStatus, query.getStatus());
        qw.lambda().ge(ValidatorUtil.isNotNull(query.get("params[beginTime]")), WebLoginInfor::getCreateTime, query.get("params[beginTime]"));
        qw.lambda().le(ValidatorUtil.isNotNull(query.get("params[endTime]")), WebLoginInfor::getCreateTime, query.get("params[endTime]"));
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
