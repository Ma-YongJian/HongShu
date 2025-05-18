package com.hongshu.server.controller.web;

import com.hongshu.common.enums.Result;
import com.hongshu.common.enums.ResultCodeEnum;
import com.hongshu.common.utils.JwtUtils;
import com.hongshu.common.validator.myVaildator.noLogin.NoLoginIntercept;
import com.hongshu.web.domain.dto.AuthUserDTO;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.service.IWebAuthUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户权限
 *
 * @Author hongshu
 */
@Api(tags = "权限模块")
@RestController
@RequestMapping("/web/auth")
@Slf4j
public class WebAuthController {

    @Autowired
    private IWebAuthUserService authUserService;


    /**
     * 用户登录
     *
     * @param authUserDTO 用户
     */
    @ApiOperation("用户登录")
    @PostMapping("login")
    @NoLoginIntercept
    public Result<?> login(@RequestBody AuthUserDTO authUserDTO) {
        Map<String, Object> map = authUserService.login(authUserDTO);
        return Result.ok(map);
    }

    /**
     * 验证码登录
     *
     * @param authUserDTO 用户
     */
    @ApiOperation("验证码登录")
    @PostMapping("loginByCode")
    @NoLoginIntercept
    public Result<?> loginByCode(@RequestBody AuthUserDTO authUserDTO) {
        Map<String, Object> map = authUserService.loginByCode(authUserDTO);
        return Result.ok(map);
    }

    /**
     * 根据token获取当前用户
     *
     * @param accessToken accessToken
     */
    @ApiOperation("根据token获取当前用户")
    @GetMapping("getUserInfoByToken")
    public Result<?> getUserInfoByToken(String accessToken) {
        boolean checkToken = JwtUtils.checkToken(accessToken);
        if (!checkToken) {
            // accessToken过期，刷新accessToken
            return Result.build(ResultCodeEnum.TOKEN_EXIST.getCode(), ResultCodeEnum.TOKEN_EXIST.getMessage());
        }
        WebUser user = authUserService.getUserInfoByToken(accessToken);
        return Result.ok(user);
    }

    /**
     * 用户注册
     *
     * @param authUserDTO 前台传递用户信息
     */
    @ApiOperation("用户注册")
    @PostMapping("register")
    @NoLoginIntercept
    public Result<?> register(@RequestBody AuthUserDTO authUserDTO) {
        WebUser user = authUserService.register(authUserDTO);
        return Result.ok(user);
    }

    /**
     * 用户是否注册
     *
     * @param authUserDTO 用户
     */
    @ApiOperation("用户是否注册")
    @PostMapping("isRegister")
    public Result<?> isRegister(@RequestBody AuthUserDTO authUserDTO) {
        boolean flag = authUserService.isRegister(authUserDTO);
        return Result.ok(flag);
    }

    /**
     * 退出登录
     *
     * @param userId 用户ID
     */
    @ApiOperation("退出登录")
    @GetMapping("loginOut")
    public Result<?> loginOut(String userId) {
        authUserService.loginOut(userId);
        return Result.ok();
    }

    /**
     * 修改密码
     *
     * @param authUserDTO 用户
     */
    @ApiOperation("修改密码")
    @PostMapping("updatePassword")
    public Result<?> updatePassword(@RequestBody AuthUserDTO authUserDTO) {
        Boolean flag = authUserService.updatePassword(authUserDTO);
        return Result.ok(flag);
    }

    /**
     * 刷新token
     *
     * @param refreshToken refreshToken
     */
    @ApiOperation("刷新token")
    @GetMapping("refreshToken")
    @NoLoginIntercept
    public Result<?> refreshToken(String refreshToken) {
        boolean checkToken = JwtUtils.checkToken(refreshToken);
        if (!checkToken) {
            //refreshToken过期，跳转登录界面
            return Result.build(ResultCodeEnum.TOKEN_FAIL.getCode(), ResultCodeEnum.TOKEN_FAIL.getMessage());
        }
        Map<String, Object> map = authUserService.refreshToken(refreshToken);
        return Result.ok(map);
    }
}
