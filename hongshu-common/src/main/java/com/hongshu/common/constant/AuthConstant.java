package com.hongshu.common.constant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author hongshu
 */
@ApiModel("权限管理常量")
public interface AuthConstant {

    @ApiModelProperty("accessToken过期时间")
    long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24;
    @ApiModelProperty("refreshToken过期时间")
    long REFRESH_TOKEN_EXPIRATION_TIME = ACCESS_TOKEN_EXPIRATION_TIME * 2;
    @ApiModelProperty("refreshToken保留时间")
    String REFRESH_TOKEN_START_TIME = "refreshTokenStartTime:";
    @ApiModelProperty("用户key")
    String USER_KEY = "userKey:";
    @ApiModelProperty("用户信息")
    String USER_INFO = "userInfo";
    @ApiModelProperty("验证码key")
    String CODE = "code:";
    @ApiModelProperty("默认头像")
    String DEFAULT_AVATAR = "https://foruda.gitee.com/avatar/1677084428450863653/7573881_xzjsccz_1604058944.png!avatar200";
    @ApiModelProperty("默认背景")
    String DEFAULT_COVER = "https://cc-video-oss.oss-accelerate.aliyuncs.com/2023/06/02/c6a167251a194484ac7b25c5e3ae366720200725103959_K8EJa.jpeg";
    @ApiModelProperty("默认密码")
    String DEFAULT_PASSWORD = "123456";
    @ApiModelProperty("登录失败")
    String LOGIN_FAIL = "登录失败";
}
