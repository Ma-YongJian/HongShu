package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.dto.AuthUserDTO;
import com.hongshu.web.domain.entity.WebUser;

import java.util.Map;

/**
 * 权限
 *
 * @Author hongshu
 */
public interface IWebAuthUserService extends IService<WebUser> {

    /**
     * 用户登录
     *
     * @param authUserDTO 用户
     */
    Map<String, Object> login(AuthUserDTO authUserDTO);

    /**
     * 验证码登录
     *
     * @param authUserDTO 用户
     */
    Map<String, Object> loginByCode(AuthUserDTO authUserDTO);

    /**
     * 根据token获取当前用户
     *
     * @param accessToken accessToken
     */
    WebUser getUserInfoByToken(String accessToken);

    /**
     * 用户注册
     *
     * @param authUserDTO 前台传递用户信息
     */
    WebUser register(AuthUserDTO authUserDTO);

    /**
     * 用户是否注册
     *
     * @param authUserDTO 用户
     */
    boolean isRegister(AuthUserDTO authUserDTO);

    /**
     * 退出登录
     *
     * @param userId 用户ID
     */
    void loginOut(String userId);

    /**
     * 修改密码
     *
     * @param authUserDTO 用户
     */
    boolean updatePassword(AuthUserDTO authUserDTO);

//    /**
//     * 第三方登录
//     *
//     * @param userOtherLoginRelationDTO
//     */
//     @Deprecated
//     Map<String, Object> otherLogin(UserOtherLoginRelationDTO userOtherLoginRelationDTO);

    /**
     * 刷新token
     *
     * @param refreshToken refreshToken
     */
    Map<String, Object> refreshToken(String refreshToken);
}
