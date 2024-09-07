package com.hongshu.web.controller.app;

import com.hongshu.common.enums.Result;
import com.hongshu.common.validator.myVaildator.noLogin.NoLoginIntercept;
import com.hongshu.web.domain.dto.EsRecordDTO;
import com.hongshu.web.service.IWebEsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ES
 *
 * @author: hongshu
 */
@RequestMapping("/app/es/record")
@RestController
public class AppEsRecordController {

    @Autowired
    private IWebEsRecordService esRecordService;


    /**
     * 获取搜索记录
     */
    @GetMapping("getRecordByKeyWord")
    public Result<?> getRecordByKeyWord(EsRecordDTO esRecordDTO) {
        return Result.ok(esRecordService.getRecordByKeyWord(esRecordDTO));
    }

    /**
     * 热门搜索
     */
    @NoLoginIntercept
    @GetMapping("getHotRecord")
    public Result<?> getHotRecord() {
        return Result.ok(esRecordService.getHotRecord());
    }

    /**
     * 增加搜索记录
     */
    @PostMapping("addRecord")
    public Result<?> addRecord(@RequestBody EsRecordDTO esRecordDTO) {
        esRecordService.addRecord(esRecordDTO);
        return Result.ok();
    }

    /**
     * 删除搜索记录
     */
    @PostMapping("clearRecord")
    public Result<?> clearRecordByUser(@RequestBody EsRecordDTO esRecordDTO) {
        esRecordService.clearRecordByUser(esRecordDTO);
        return Result.ok();
    }

    /**
     * 清空搜索记录
     */
    @PostMapping("clearAllRecord")
    public Result<?> clearAllRecord() {
        esRecordService.clearAllRecord();
        return Result.ok();
    }
}
