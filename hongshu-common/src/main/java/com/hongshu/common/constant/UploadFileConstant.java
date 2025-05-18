package com.hongshu.common.constant;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 文件上传路径配置
 *
 * @Author hongshu
 */
@ApiModel("文件上传路径配置")
public interface UploadFileConstant {

    @ApiModelProperty("文件上传地址")
    String ADDRESS = "XXX";

    @ApiModelProperty("文件访问前缀")
    String OSS = "/oss/";
}
