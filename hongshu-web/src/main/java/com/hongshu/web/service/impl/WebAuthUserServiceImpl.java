package com.hongshu.web.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.constant.AuthConstant;
import com.hongshu.common.constant.Constants;
import com.hongshu.common.constant.TokenConstant;
import com.hongshu.common.enums.ResultCodeEnum;
import com.hongshu.common.exception.web.HongshuException;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.common.utils.JwtUtils;
import com.hongshu.common.utils.MessageUtils;
import com.hongshu.common.utils.RedisUtils;
import com.hongshu.common.utils.ip.AddressUtils;
import com.hongshu.common.utils.ip.IpUtils;
import com.hongshu.web.domain.dto.AuthUserDTO;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.manager.AsyncManager;
import com.hongshu.web.manager.factory.AsyncFactory;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.IWebAuthUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 权限
 *
 * @Author hongshu
 */
@Slf4j
@Service
public class WebAuthUserServiceImpl extends ServiceImpl<WebUserMapper, WebUser> implements IWebAuthUserService {

    @Autowired
    RedisUtils redisUtils;


    /**
     * 用户登录
     *
     * @param authUserDTO 用户
     */
    @Override
    @Transactional
    public Map<String, Object> login(AuthUserDTO authUserDTO) {
        Map<String, Object> map = new HashMap<>();
        WebUser authUser;
        // 查询用户
        authUser = this.getOne(new QueryWrapper<WebUser>().eq("phone", authUserDTO.getPhone()));
        // 注册新用户
        if (ObjectUtil.isEmpty(authUser)) {
            authUser = this.register(authUserDTO);
        } else {
            // 校验登录密码
            if (!authUser.getPassword().equals(SecureUtil.md5(authUserDTO.getPassword()))) {
                throw new HongshuException(ResultCodeEnum.ERROR_PASSWORD);
            }
        }
        // 更新登录信息
        authUser.setLoginDate(new Date());
        authUser.setLoginIp(IpUtils.getHostIp());
        authUser.setAddress(AddressUtils.getRealAddressByIP(authUser.getLoginIp()));
        this.updateById(authUser);
        // 缓存Token
        this.setUserInfoAndToken(map, authUser);
        // 保存日志
        AsyncManager.me().execute(AsyncFactory.recordLoginInfor(
                authUser.getId(),
                authUser.getUsername(),
                Constants.LOGIN_SUCCESS,
                MessageUtils.message("user.login.success")));
        return map;
    }

    /**
     * 验证码登录
     *
     * @param authUserDTO 用户
     */
    @Override
    public Map<String, Object> loginByCode(AuthUserDTO authUserDTO) {
        Map<String, Object> map = new HashMap<>(2);
        WebUser currentUser;
        if (StringUtils.isNotBlank(authUserDTO.getPhone())) {
            currentUser = this.getOne(new QueryWrapper<WebUser>().eq("phone", authUserDTO.getPhone()));
        } else {
            currentUser = this.getOne(new QueryWrapper<WebUser>().eq("email", authUserDTO.getEmail()));
        }
        if (this.checkCode(authUserDTO) || currentUser == null) {
            throw new HongshuException(AuthConstant.LOGIN_FAIL);
        }
        this.setUserInfoAndToken(map, currentUser);
        return map;
    }

    /**
     * 根据token获取当前用户
     *
     * @param accessToken accessToken
     */
    @Override
    public WebUser getUserInfoByToken(String accessToken) {
        String userId = JwtUtils.getUserId(accessToken);
        return this.getById(userId);
    }

    /**
     * 用户注册
     *
     * @param authUserDTO 前台传递用户信息
     */
    @Override
    public WebUser register(AuthUserDTO authUserDTO) {
        Map<String, Object> map = new HashMap<>();
        WebUser user = ConvertUtils.sourceToTarget(authUserDTO, WebUser.class);
        user.setHsId(Long.valueOf(RandomUtil.randomNumbers(10)));
        user.setUsername("小红薯");
        user.setAvatar(AuthConstant.DEFAULT_AVATAR);
        user.setUserCover(AuthConstant.DEFAULT_COVER);
        user.setPassword(SecureUtil.md5(authUserDTO.getPassword()));
        user.setLoginIp(IpUtils.getHostIp());
        user.setAddress(AddressUtils.getRealAddressByIP(user.getLoginIp()));
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        this.save(user);
        return user;
    }

    /**
     * 用户是否注册
     *
     * @param authUserDTO 用户
     */
    @Override
    public boolean isRegister(AuthUserDTO authUserDTO) {
        long count = this.count(new QueryWrapper<WebUser>().eq("phone", authUserDTO.getPhone()).or().eq("email", authUserDTO.getEmail()));
        return count > 0;
    }

    /**
     * 退出登录
     *
     * @param userId 用户ID
     */
    @Override
    public void loginOut(String userId) {
        String userKey = AuthConstant.USER_KEY + userId;
        String refreshTokenStartTimeKey = AuthConstant.REFRESH_TOKEN_START_TIME + userId;
        List<String> keyList = new ArrayList<>();
        keyList.add(userKey);
        keyList.add(refreshTokenStartTimeKey);
        redisUtils.delete(keyList);
        // 保存日志
        WebUser authUser = this.getOne(new QueryWrapper<WebUser>().eq("id", userId));
        AsyncManager.me().execute(AsyncFactory.recordLoginInfor(
                authUser.getId(),
                authUser.getUsername(),
                Constants.LOGOUT,
                MessageUtils.message("user.logout.success")));
    }

    /**
     * 修改密码
     *
     * @param authUserDTO 用户
     */
    @Override
    public boolean updatePassword(AuthUserDTO authUserDTO) {
        if (!authUserDTO.getPassword().equals(authUserDTO.getCheckPassword())) {
            return false;
        }
        String pwd = SecureUtil.md5(authUserDTO.getPassword());
        WebUser user;
        if (StringUtils.isBlank(authUserDTO.getId())) {
            user = this.getOne(new QueryWrapper<WebUser>().eq("phone", authUserDTO.getPhone()).or().eq("email", authUserDTO.getEmail()));
        } else {
            user = this.getById(authUserDTO.getId());
        }
        user.setPassword(pwd);
        return this.updateById(user);
    }

    /**
     * 刷新token
     *
     * @param refreshToken refreshToken
     */
    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        HashMap<String, Object> res = new HashMap<>(2);
        String userId = JwtUtils.getUserId(refreshToken);
        log.info("userId:{}", userId);
        // 创建新的accessToken
        String accessToken = JwtUtils.getJwtToken(userId, AuthConstant.ACCESS_TOKEN_EXPIRATION_TIME);

        // minTimeOfRefreshToken  最短刷新时间
        // refreshTokenStartTime  刷新令牌开始时间

        // 下面判断是否刷新 refreshToken，如果refreshToken 快过期了 需要重新生成一个替换掉
        // refreshToken 有效时长是应该为accessToken有效时长的2倍
        long minTimeOfRefreshToken = 2 * AuthConstant.ACCESS_TOKEN_EXPIRATION_TIME;
        String refreshTokenStr = redisUtils.get(AuthConstant.REFRESH_TOKEN_START_TIME + userId);

        if (StringUtils.isBlank(refreshTokenStr)) {
            return res;
        }

        // refreshToken创建的起始时间点
        long refreshTokenStartTime = Long.parseLong(refreshTokenStr);
        // (refreshToken上次创建的时间点 + refreshToken的有效时长 - 当前时间点) 表示refreshToken还剩余的有效时长，如果小于2倍accessToken时长 ，则刷新 refreshToken
        if (refreshTokenStartTime + AuthConstant.REFRESH_TOKEN_EXPIRATION_TIME - System.currentTimeMillis() <= minTimeOfRefreshToken) {
            // 刷新refreshToken
            refreshToken = JwtUtils.getJwtToken(userId, refreshTokenStartTime);
            redisUtils.setEx(AuthConstant.REFRESH_TOKEN_START_TIME + userId, String.valueOf(System.currentTimeMillis()), AuthConstant.REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
        }

        res.put(TokenConstant.ACCESS_TOKEN, accessToken);
        res.put(TokenConstant.REFRESH_TOKEN, refreshToken);
        return res;
    }

    /**
     * 校验数据
     */
    private boolean checkCode(AuthUserDTO authUserDTO) {
        String code = "";
        if (StringUtils.isNotBlank(redisUtils.get(AuthConstant.CODE + authUserDTO.getPhone()))) {
            code = redisUtils.get(AuthConstant.CODE + authUserDTO.getPhone());
        } else if (StringUtils.isNotBlank(redisUtils.get(AuthConstant.CODE + authUserDTO.getEmail()))) {
            code = redisUtils.get(AuthConstant.CODE + authUserDTO.getEmail());
        }
        return StringUtils.isBlank(code) || !code.equals(authUserDTO.getCode());
    }

    /**
     * 缓存token
     */
    private void setUserInfoAndToken(Map<String, Object> map, WebUser authUser) {
        String accessToken = JwtUtils.getJwtToken(String.valueOf(authUser.getId()), AuthConstant.ACCESS_TOKEN_EXPIRATION_TIME);
        String refreshToken = JwtUtils.getJwtToken(String.valueOf(authUser.getId()), AuthConstant.REFRESH_TOKEN_EXPIRATION_TIME);
        //缓存当前登录用户 refreshToken 创建的起始时间，这个会在刷新accessToken方法中 判断是否要重新生成(刷新)refreshToken时用到
        redisUtils.setEx(AuthConstant.REFRESH_TOKEN_START_TIME + authUser.getId(), String.valueOf(System.currentTimeMillis()), AuthConstant.REFRESH_TOKEN_EXPIRATION_TIME, TimeUnit.MILLISECONDS);
        //将用户信息保存在redis中
        redisUtils.set(AuthConstant.USER_KEY + authUser.getId(), JSONUtil.toJsonStr(authUser));
        map.put(TokenConstant.ACCESS_TOKEN, accessToken);
        map.put(TokenConstant.REFRESH_TOKEN, refreshToken);
        map.put(AuthConstant.USER_INFO, authUser);
    }
}
