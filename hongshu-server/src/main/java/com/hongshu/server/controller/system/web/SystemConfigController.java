package com.hongshu.server.controller.system.web;


import com.hongshu.common.annotation.AuthorityVerify.AuthorityVerify;
import com.hongshu.common.core.domain.AjaxResult;
import com.hongshu.web.domain.vo.SystemConfigVO;
import com.hongshu.web.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hongshu.common.core.domain.AjaxResult.success;

/**
 * 系统配置表 RestApi
 *
 * @Author hongshu
 * @date 2020年1月21日09:24:37
 */
@Api(value = "系统配置相关接口", tags = {"系统配置相关接口"})
@RestController
@RequestMapping("/systemConfig")
@Slf4j
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;


    @AuthorityVerify
    @ApiOperation(value = "获取系统配置", notes = "获取系统配置")
    @GetMapping("/getSystemConfig")
    public AjaxResult getSystemConfig() {
        return success(systemConfigService.getConfig());
    }

    @AuthorityVerify
    @ApiOperation(value = "通过Key前缀清空Redis缓存", notes = "通过Key前缀清空Redis缓存")
    @PostMapping("/cleanRedisByKey")
    public String cleanRedisByKey(@RequestBody List<String> key) {
        return systemConfigService.cleanRedisByKey(key);
    }

    @AuthorityVerify
//    @OperationLogger(value = "修改系统配置")
    @ApiOperation(value = "修改系统配置", notes = "修改系统配置")
    @PostMapping("/editSystemConfig")
    public String editSystemConfig(@RequestBody SystemConfigVO systemConfigVO) {
        return systemConfigService.editSystemConfig(systemConfigVO);
    }
}

