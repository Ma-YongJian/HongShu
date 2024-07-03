package com.hongshu.web.controller.web;

import com.hongshu.common.enums.Result;
import com.hongshu.web.service.IWebOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OSS
 *
 * @author: hongshu
 */
@RequestMapping("/web/oss")
@RestController
public class WebOssController {

    @Autowired
    IWebOssService ossService;


    /**
     * 上传文件
     *
     * @param file 文件
     * @param type 上传类型
     */
    @PostMapping("save/{type}")
    public Result<?> save(MultipartFile file, @PathVariable Integer type) {
        String path = ossService.save(file, type);
        return Result.ok(path);
    }

    /**
     * 批量上传文件
     *
     * @param files 文件集
     * @param type  类型
     */
    @PostMapping(value = "saveBatch/{type}")
    public Result<List<String>> saveBatch(@RequestParam("uploadFiles") MultipartFile[] files, @PathVariable Integer type) {
        if (files.length == 0) {
            return Result.fail(null);
        }
        List<String> stringList = ossService.saveBatch(files, type);
        return Result.ok(stringList);
    }

    /**
     * 删除文件
     *
     * @param path 路径
     * @param type 类型
     */
    @GetMapping("delete/{type}")
    public Result<?> delete(String path, @PathVariable Integer type) {
        ossService.delete(path, type);
        return Result.ok();
    }

    /**
     * 批量删除文件
     *
     * @param filePaths 文件路径集
     * @param type      类型
     */
    @PostMapping(value = "deleteBatch/{type}")
    public Result<?> deleteBatch(@RequestBody List<String> filePaths, @PathVariable Integer type) {
        if (filePaths.isEmpty()) {
            return Result.fail(null);
        }
        ossService.batchDelete(filePaths, type);
        return Result.ok();
    }
}
