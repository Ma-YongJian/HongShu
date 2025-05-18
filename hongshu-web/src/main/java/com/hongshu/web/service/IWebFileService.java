package com.hongshu.web.service;

import com.hongshu.common.core.domain.entity.SystemConfig;
import com.hongshu.web.domain.entity.WebFile;
import com.hongshu.web.domain.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文件服务类
 *
 * @Author hongshu
 */
public interface IWebFileService extends SuperService<WebFile> {

    /**
     * 截图上传
     *
     * @param multipartFileList
     * @return
     */
    String cropperPicture(List<MultipartFile> multipartFileList);

    /**
     * 通过fileIds获取图片信息
     *
     * @param fileIds
     * @param code
     * @return
     */
    String getPicture(String fileIds, String code);

    /**
     * 批量文件上传
     *
     * @param request
     * @param multipartFileList
     * @param systemConfig
     * @return
     */
    String batchUploadFile(HttpServletRequest request, List<MultipartFile> multipartFileList, SystemConfig systemConfig);

    /**
     * 通过URL上传图片
     *
     * @param fileVO
     * @return
     */
    String uploadPictureByUrl(FileVO fileVO);

    /**
     * CKeditor图像中的图片上传
     *
     * @param request
     * @return
     */
    Object ckeditorUploadFile(HttpServletRequest request);

    /**
     * CKeditor上传 复制的图片
     *
     * @return
     */
    Object ckeditorUploadCopyFile();

    /**
     * 工具栏 “插入\编辑超链接”的文件上传
     *
     * @return
     */
    Object ckeditorUploadToolFile(HttpServletRequest request);
}
