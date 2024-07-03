package com.hongshu.web.service.impl;

import com.hongshu.common.utils.MinioUtil;
import com.hongshu.common.utils.QiniuUtil;
import com.hongshu.web.service.IWebOssService;
import com.hongshu.web.websocket.factory.OssFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * OSS
 *
 * @author: hongshu
 */
@Service
public class WebOssServiceImpl implements IWebOssService {

    @Autowired
    QiniuUtil qiniuUtil;
    @Autowired
    MinioUtil minioUtil;

    /**
     * 上传文件
     *
     * @param file 文件
     * @param type 上传类型(0:本地 1：七牛云 2：minio)
     */
    @Override
    public String save(MultipartFile file, Integer type) {
        OssFactory factory = null;
        switch (type) {
            case 0:
                // 本地上传图片
                factory = new UploadFileToLoacl();
                break;
            case 1:
                // 七牛云
                factory = new QiNiuYunUploadFile();
//                qiniuUtil.uploadQiniu();
                break;
            case 2:
                // Minio
//                minioUtil.uploadFile(file);
                break;
            default:
                break;
        }
        if (factory != null) {
            return factory.save(file);
        }
        return null;
    }

    /**
     * 批量上传文件
     *
     * @param files 文件集
     * @param type  类型
     */
    @Override
    public List<String> saveBatch(MultipartFile[] files, Integer type) {
        List<String> result = new ArrayList<>();
        // 需要进行加锁，不然会出现多次添加
        for (MultipartFile file : files) {
            result.add(this.save(file, type));
        }
        return result;
    }

    /**
     * 删除文件
     *
     * @param path 路径
     * @param type 类型
     */
    @Override
    public void delete(String path, Integer type) {
        OssFactory factory = null;
        switch (type) {
            case 0:
                // 本地上传图片
                factory = new UploadFileToLoacl();
                break;
            case 1:
                factory = new QiNiuYunUploadFile();
                break;
            default:
                break;
        }
        factory.delete(path);
    }

    /**
     * 批量删除文件
     *
     * @param filePaths 文件路径集
     * @param type      类型
     */
    @Override
    public void batchDelete(List<String> filePaths, Integer type) {
        for (String path : filePaths) {
            delete(path, type);
        }
    }
}
