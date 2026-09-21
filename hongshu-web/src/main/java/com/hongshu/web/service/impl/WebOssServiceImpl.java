package com.hongshu.web.service.impl;

import com.hongshu.common.config.HongshuConfig;
import com.hongshu.common.config.OssConfig;
import com.hongshu.common.constant.Constants;
import com.hongshu.common.utils.MinioUtil;
import com.hongshu.common.utils.file.FileUploadUtils;
import com.hongshu.common.utils.file.FileUtils;
import com.hongshu.common.utils.file.MimeTypeUtils;
import com.hongshu.web.service.IWebOssService;
import com.hongshu.web.websocket.factory.OssFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * OSS：开源版仅本地 / Minio
 *
 * @Author hongshu
 */
@Slf4j
@Service
public class WebOssServiceImpl implements IWebOssService {

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
        if (type == null || type == 0) {
            return FileUploadUtils.upload(HongshuConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
        }
        if (type == 1 || type == 2) {
            return minioUtil.uploadFile(file);
        }
        throw new IllegalArgumentException("开源版仅支持本地或 Minio 存储: " + type);
    }

    /**
     * 批量上传文件
     *
     * @param files 文件集
     */
    @Override
    public List<String> saveBatch(MultipartFile[] files) {
        List<String> result = new ArrayList<>();
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
        if (StringUtils.isBlank(path)) {
            return;
        }
        Integer type = ossConfig.getType();
        if (type == null || type == 0) {
            deleteLocal(path);
            return;
        }
        if (type == 1 || type == 2) {
            String objectName = path;
            int slash = path.lastIndexOf('/');
            if (slash >= 0 && slash < path.length() - 1) {
                objectName = path.substring(slash + 1);
            }
            minioUtil.deleteFile(objectName);
        }
    }

    /**
     * 批量删除文件
     *
     * @param filePaths 文件路径集
     */
    @Override
    public void batchDelete(List<String> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) {
            return;
        }
        for (String path : filePaths) {
            try {
                delete(path);
            } catch (Exception e) {
                log.warn("删除文件失败: {}", path, e);
            }
        }
    }

    /**
     * 开源版本地上传走 /profile，旧路径才带 /oss/
     */
    private void deleteLocal(String path) {
        String value = path.trim();
        int profileIdx = value.indexOf(Constants.RESOURCE_PREFIX + "/");
        if (profileIdx >= 0) {
            value = value.substring(profileIdx);
        }
        if (value.startsWith(Constants.RESOURCE_PREFIX + "/")) {
            String abs = HongshuConfig.getProfile() + value.substring(Constants.RESOURCE_PREFIX.length());
            FileUtils.deleteFile(abs);
            return;
        }
        if (value.contains("/oss/")) {
            OssFactory factory = new UploadFileToLoacl();
            factory.delete(value);
        }
    }
}
