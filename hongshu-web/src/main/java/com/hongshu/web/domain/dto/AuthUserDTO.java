package com.hongshu.web.domain.dto;

import com.hongshu.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 权限用户
 *
 * @Author hongshu
 */
@ApiModel("权限用户dto")
@Data
public class AuthUserDTO implements Serializable {

    @ApiModelProperty("用户id")
    private String id;
    @ApiModelProperty("用户名")
    private String username;

    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$", message = "请输入8-20位由字母和数字组成的密码", groups = DefaultGroup.class)
    @ApiModelProperty("登录密码")
    private String password;

    @ApiModelProperty("校验密码")
    private String checkPassword;

    @Pattern(regexp = "^1[0-9]{10}$", message = "手机号格式有误", groups = DefaultGroup.class)
    @ApiModelProperty("手机号")
    private String phone;

    //@Pattern(regexp = "^([a-zA-Z0-9._-])+@([a-zA-Z0-9_-])+((.[a-zA-Z0-9_-]{2,3}){1,2})$", message = "邮箱输入有误", groups = DefaultGroup.class)
    @ApiModelProperty("email")
    private String email;

    @ApiModelProperty("登录验证码")
    private String code;

}
