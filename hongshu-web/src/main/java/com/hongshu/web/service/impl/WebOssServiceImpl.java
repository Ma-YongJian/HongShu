package com.hongshu.web.service.impl;

import com.hongshu.common.config.HongshuConfig;
import com.hongshu.common.config.OssConfig;
import com.hongshu.common.utils.MinioUtil;
import com.hongshu.common.utils.QiniuUtil;
import com.hongshu.common.utils.file.FileUploadUtils;
import com.hongshu.common.utils.file.MimeTypeUtils;
import com.hongshu.web.service.IWebOssService;
import com.hongshu.web.websocket.factory.OssFactory;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * OSS
 *
 * @Author hongshu
 */
@Service
public class WebOssServiceImpl implements IWebOssService {

    @Autowired
    QiniuUtil qiniuUtil;
    @Autowired
    MinioUtil minioUtil;
    @Autowired
    private OssConfig ossConfig;

    /**
     * 上传文件
     *
     * @param file 文件
     */
    @SneakyThrows
    @Override
    public String save(MultipartFile file) {
        Integer type = ossConfig.getType();
        switch (type) {
            case 0:
                // 本地存储
                return FileUploadUtils.upload(HongshuConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
            case 1:
                // 七牛云存储
                OssFactory qiniuFactory = new QiNiuYunUploadFile();
                return qiniuFactory.save(file);
            case 2:
                // Minio存储
                return minioUtil.uploadFile(file);
            default:
                throw new IllegalArgumentException("不支持的存储类型: " + type);
        }
    }

    /**
     * 批量上传文件
     *
     * @param files 文件集
     */
    @Override
    public List<String> saveBatch(MultipartFile[] files) {
        List<String> result = new ArrayList<>();
        // 需要进行加锁，不然会出现多次添加
        for (MultipartFile file : files) {
            result.add(this.save(file));
        }
        return result;
    }

    /**
     * 删除文件
     *
     * @param path 路径
     */
    @Override
    public void delete(String path) {
        OssFactory factory = null;
        Integer type = ossConfig.getType();
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
     */
    @Override
    public void batchDelete(List<String> filePaths) {
        for (String path : filePaths) {
            delete(path);
        }
    }
}
