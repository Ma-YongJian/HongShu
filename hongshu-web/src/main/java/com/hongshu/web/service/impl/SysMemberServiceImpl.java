package com.hongshu.web.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hongshu.common.core.domain.Query;
import com.hongshu.common.global.BaseSQLConf;
import com.hongshu.common.utils.RandomUtil;
import com.hongshu.common.utils.ip.AddressUtils;
import com.hongshu.common.utils.ip.IpUtils;
import com.hongshu.common.validator.ValidatorUtil;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.mapper.SysMemberMapper;
import com.hongshu.web.service.ISysMemberService;
import com.hongshu.web.service.IWebOssService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 会员信息 服务层处理
 *
 * @Author hongshu
 */
@Slf4j
@Service
public class SysMemberServiceImpl implements ISysMemberService {

    @Autowired
    private SysMemberMapper memberMapper;
    @Autowired
    private IWebOssService ossService;


    /**
     * 查询会员信息集合
     *
     * @param query 会员信息
     */
    @Override
    public List<WebUser> selectMemberList(Query query) {
        QueryWrapper<WebUser> qw = new QueryWrapper<>();
        qw.lambda().like(ValidatorUtil.isNotNull(query.getHsId()), WebUser::getHsId, query.getHsId());
        qw.lambda().like(ValidatorUtil.isNotNull(query.getUsername()), WebUser::getUsername, query.getUsername());
        qw.lambda().like(ValidatorUtil.isNotNull(query.getPhone()), WebUser::getPhone, query.getPhone());
        qw.lambda().like(ValidatorUtil.isNotNull(query.getStatus()), WebUser::getStatus, query.getStatus());
        qw.lambda().ge(ValidatorUtil.isNotNull(query.get("params[beginTime]")), WebUser::getCreateTime, query.get("params[beginTime]"));
        qw.lambda().le(ValidatorUtil.isNotNull(query.get("params[endTime]")), WebUser::getCreateTime, query.get("params[endTime]"));
        qw.orderByDesc("create_time");
        return memberMapper.selectList(qw);
    }

    /**
     * 查询所有会员
     */
    @Override
    public List<WebUser> selectMemberAll() {
        return memberMapper.selectList(new QueryWrapper<>());
    }

    /**
     * 通过会员ID查询会员信息
     *
     * @param id 会员ID
     */
    @Override
    public WebUser selectMemberById(Long id) {
        return memberMapper.selectById(id);
    }

    /**
     * 新增会员信息
     *
     * @param user 会员信息
     */
    @Override
    public int insertMember(WebUser user, MultipartFile file) {
        // 上传头像
        if (ObjectUtils.isNotEmpty(file)) {
            String avatar = ossService.save(file);
            user.setAvatar(avatar);
        }
        user.setHsId(Long.valueOf(RandomUtil.randomNumbers(10)));
        user.setPassword(SecureUtil.md5(user.getPassword()));
        user.setLoginIp(IpUtils.getIpAddr());
        user.setAddress(AddressUtils.getRealAddressByIP(user.getLoginIp()));
        user.setCreator("System");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        return memberMapper.insert(user);
    }

    /**
     * 修改保存会员信息
     *
     * @param user 会员信息
     */
    @Override
    public int updateMember(WebUser user, MultipartFile file) {
        WebUser webUser = memberMapper.selectById(user.getId());
        // 更新头像
        if (ObjectUtils.isNotEmpty(file)) {
            String avatar = ossService.save(file);
            user.setAvatar(avatar);
        }
        String newPassword = webUser.getPassword();
        String originPassword = SecureUtil.md5(user.getPassword());
        // 确认更换了新密码
        if (!originPassword.equals(newPassword)) {
            user.setPassword(SecureUtil.md5(user.getPassword()));
        }
        user.setPassword(webUser.getPassword());
        user.setLoginIp(IpUtils.getIpAddr());
        user.setAddress(AddressUtils.getRealAddressByIP(user.getLoginIp()));
        user.setUpdater("System");
        user.setUpdateTime(new Date());
        return memberMapper.updateById(user);
    }

    /**
     * 删除会员信息
     *
     * @param id 会员ID
     */
    @Override
    public int deleteMemberById(Long id) {
        return memberMapper.deleteById(id);
    }

    /**
     * 批量删除会员信息
     *
     * @param ids 需要删除的会员ID
     */
    @Override
    public int deleteMemberByIds(Long[] ids) {
        List<Long> longs = Arrays.asList(ids);
        for (Long id : ids) {
            WebUser user = selectMemberById(id);
            if (ValidatorUtil.isNull(user)) {
                log.info("用户不存在:{}", id);
                longs.remove(id);
            }
        }
        return memberMapper.deleteBatchIds(longs);
    }

    @Override
    public Integer getMemberCount(int status) {
        QueryWrapper<WebUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(BaseSQLConf.STATUS, status);
        return Math.toIntExact(memberMapper.selectCount(queryWrapper));
    }
}
