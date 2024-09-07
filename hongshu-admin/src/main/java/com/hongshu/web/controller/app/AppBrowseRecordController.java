package com.hongshu.web.controller.app;

import com.hongshu.common.enums.Result;
import com.hongshu.web.domain.dto.BrowseRecordDTO;
import com.hongshu.web.domain.vo.NoteSearchVo;
import com.hongshu.web.service.IWebBrowseRecordService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 浏览记录
 *
 * @author: hongshu
 */
@RestController
@RequestMapping("/app/browseRecord")
@Api(tags = "浏览记录")
public class AppBrowseRecordController {

    @Autowired
    private IWebBrowseRecordService browseRecordService;


    /**
     * 获取浏览记录
     */
    @RequestMapping("getAllBrowseRecordByUser/{page}/{limit}")
    public Result<?> getAllBrowseRecordByUser(@PathVariable("page") long page, @PathVariable("limit") long limit, String uid) {
        List<NoteSearchVo> browseRecordVoList = browseRecordService.getAllBrowseRecordByUser(page, limit, uid);
        return Result.ok(browseRecordVoList);
    }

    /**
     * 添加浏览记录
     */
    @RequestMapping("addBrowseRecord")
    public Result<?> addBrowseRecord(@RequestBody BrowseRecordDTO browseRecordDTO) {
        browseRecordService.addBrowseRecord(browseRecordDTO);
        return Result.ok(null);
    }

    /**
     * 删除浏览记录
     */
    @RequestMapping("delRecord/{uid}")
    public Result<?> delRecord(@RequestBody List<String> idList, @PathVariable String uid) {
        browseRecordService.delRecord(uid, idList);
        return Result.ok(null);
    }
}
