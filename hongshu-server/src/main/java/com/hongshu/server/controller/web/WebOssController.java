package com.hongshu.server.controller.web;

import com.hongshu.common.enums.Result;
import com.hongshu.web.service.IWebOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OSS
 *
 * @Author hongshu
 */
@RequestMapping("/web/oss")
@RestController
public class WebOssController {

    @Autowired
    private IWebOssService ossService;


    /**
     * 上传文件
     *
     * @param file 文件
     */
    @PostMapping("save/{type}")
    public Result<?> save(MultipartFile file) {
        String path = ossService.save(file);
        return Result.ok(path);
    }

    /**
     * 批量上传文件
     *
     * @param files 文件集
     */
    @PostMapping(value = "saveBatch")
    public Result<List<String>> saveBatch(@RequestParam("uploadFiles") MultipartFile[] files) {
        if (files.length == 0) {
            return Result.fail(null);
        }
        List<String> stringList = ossService.saveBatch(files);
        return Result.ok(stringList);
    }

    /**
     * 删除文件
     *
     * @param path 路径
     */
    @GetMapping("delete")
    public Result<?> delete(String path) {
        ossService.delete(path);
        return Result.ok();
    }

    /**
     * 批量删除文件
     *
     * @param filePaths 文件路径集
     */
    @PostMapping(value = "deleteBatch")
    public Result<?> deleteBatch(@RequestBody List<String> filePaths) {
        if (filePaths.isEmpty()) {
            return Result.fail(null);
        }
        ossService.batchDelete(filePaths);
        return Result.ok();
    }
}
