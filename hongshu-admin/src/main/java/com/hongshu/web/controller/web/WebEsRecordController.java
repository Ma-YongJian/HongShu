package com.hongshu.web.controller.web;

import com.hongshu.common.enums.Result;
import com.hongshu.common.validator.myVaildator.noLogin.NoLoginIntercept;
import com.hongshu.web.service.IWebEsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ES
 *
 * @author: hongshu
 */
@RequestMapping("/web/es/record")
@RestController
public class WebEsRecordController {

    @Autowired
    IWebEsRecordService esRecordService;

    /**
     * 获取搜索记录
     *
     * @param keyword 关键词
     */
    @GetMapping("getRecordByKeyWord")
    public Result<?> getRecordByKeyWord(String keyword) {
        return Result.ok(esRecordService.getRecordByKeyWord(keyword));
    }

    /**
     * 热门关键词
     */
    @NoLoginIntercept
    @GetMapping("getHotRecord")
    public Result<?> getHotRecord() {
        return Result.ok(esRecordService.getHotRecord());
    }

    /**
     * 增加搜索记录
     *
     * @param keyword 关键词
     */
    @GetMapping("addRecord")
    public Result<?> addRecord(String keyword) {
        esRecordService.addRecord(keyword);
        return Result.ok();
    }
}
