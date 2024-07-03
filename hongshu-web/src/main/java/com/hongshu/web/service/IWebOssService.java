package com.hongshu.web.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OSS
 *
 * @author: hongshu
 */
public interface IWebOssService {

    /**
     * 上传文件
     *
     * @param file 文件
     * @param type 上传类型
     */
    String save(MultipartFile file, Integer type);

    /**
     * 批量上传文件
     *
     * @param files 文件集
     * @param type  类型
     */
    List<String> saveBatch(MultipartFile[] files, Integer type);

    /**
     * 删除文件
     *
     * @param path 路径
     * @param type 类型
     */
    void delete(String path, Integer type);

    /**
     * 批量删除文件
     *
     * @param filePaths 文件路径集
     * @param type      类型
     */
    void batchDelete(List<String> filePaths, Integer type);
}
