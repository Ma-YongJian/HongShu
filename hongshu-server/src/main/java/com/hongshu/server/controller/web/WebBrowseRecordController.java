package com.hongshu.server.controller.web;

import com.hongshu.common.enums.Result;
import com.hongshu.web.domain.dto.BrowseRecordDTO;
import com.hongshu.web.domain.vo.NoteSearchVO;
import com.hongshu.web.service.IWebBrowseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 浏览记录
 *
 * @Author hongshu
 */
@RestController
@RequestMapping("/web/browseRecord")
public class WebBrowseRecordController {

    @Autowired
    private IWebBrowseRecordService browseRecordService;

    /**
     * 获取浏览记录
     */
    @GetMapping("getAllBrowseRecordByUser/{page}/{limit}")
    public Result<?> getAllBrowseRecordByUser(@PathVariable long page, @PathVariable long limit, String uid) {
        List<NoteSearchVO> list = browseRecordService.getAllBrowseRecordByUser(page, limit, uid);
        return Result.ok(list);
    }

    /**
     * 添加浏览记录
     */
    @PostMapping("addBrowseRecord")
    public Result<?> addBrowseRecord(@RequestBody BrowseRecordDTO browseRecordDTO) {
        browseRecordService.addBrowseRecord(browseRecordDTO);
        return Result.ok();
    }

    /**
     * 删除浏览记录
     */
    @PostMapping("delRecord/{uid}")
    public Result<?> delRecord(@PathVariable String uid, @RequestBody List<String> idList) {
        browseRecordService.delRecord(uid, idList);
        return Result.ok();
    }
}
