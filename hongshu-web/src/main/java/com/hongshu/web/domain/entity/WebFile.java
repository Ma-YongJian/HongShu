package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.common.core.domain.SuperEntity;
import lombok.Data;

/**
 * 文件实体类
 *
 * @Author hongshu
 * @date 2018-09-17
 */
@TableName("t_file")
@Data
public class WebFile extends SuperEntity<WebFile> {

    private static final long serialVersionUID = 1L;

    private String fileOldName;

    private Long fileSize;

    private String fileSortUid;

    /**
     * 图片扩展名
     */
    private String picExpandedName;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 图片url地址
     */
    private String picUrl;

    /**
     * 管理员Uid
     */
    private String adminUid;

    /**
     * 用户Uid
     */
    private String userUid;

    /**
     * 七牛云Url
     */
    private String qiNiuUrl;

    /**
     * Minio文件URL
     */
    private String minioUrl;
}
