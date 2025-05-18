package com.hongshu.common.constant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 全局配置
 *
 * @Author hongshu
 */
@ApiModel("全局配置变量")
public interface GlobalConstant {

    @ApiModelProperty("开发环境")
    String SPRING_PROFILE_DEVELOPMENT = "dev";

    @ApiModelProperty("测试环境")
    String SPRING_PROFILE_TEST = "test";
}
