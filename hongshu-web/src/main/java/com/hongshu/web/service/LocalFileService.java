package com.hongshu.web.service;

import com.hongshu.web.domain.entity.WebFileSort;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 本地文件系统服务类【通过IO流存储到本地】
 *
 * @Author hongshu
 */
public interface LocalFileService {

    /**
     * 多文件上传
     *
     * @param multipartFileList
     * @param fileSort
     * @return
     * @throws IOException
     */
    List<String> batchUploadFile(List<MultipartFile> multipartFileList, WebFileSort fileSort) throws IOException;

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param fileSort
     * @return
     * @throws IOException
     */
    String uploadFile(MultipartFile multipartFile, WebFileSort fileSort) throws IOException;

    /**
     * 通过URL上传图片
     *
     * @param url
     * @param fileSort
     * @return
     */
    String uploadPictureByUrl(String url, WebFileSort fileSort);
}
