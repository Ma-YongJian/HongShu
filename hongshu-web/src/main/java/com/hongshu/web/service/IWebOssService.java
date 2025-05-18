package com.hongshu.web.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * OSS
 *
 * @Author hongshu
 */
public interface IWebOssService {

    /**
     * 上传文件
     *
     * @param file 文件
     */
    String save(MultipartFile file);

    /**
     * 批量上传文件
     *
     * @param files 文件集
     */
    List<String> saveBatch(MultipartFile[] files);

    /**
     * 删除文件
     *
     * @param path 路径
     */
    void delete(String path);

    /**
     * 批量删除文件
     *
     * @param filePaths 文件路径集
     */
    void batchDelete(List<String> filePaths);
}
