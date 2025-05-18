package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.common.core.domain.SuperEntity;
import lombok.Data;

/**
 * 图片实体类
 *
 * @Author xuzhixiang
 * @date 2018年9月17日16:08:58
 */
@Data
@TableName("t_picture")
public class WebPicture extends SuperEntity<WebPicture> {

    private static final long serialVersionUID = 2646201532621057453L;

    /**
     * 图片的UID
     */
    private String fileUid;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 所属相册分类UID
     */
    private String pictureSortUid;

    // 以下字段不存入数据库，封装为了方便使用

    /**
     * 图片路径
     */
    @TableField(exist = false)
    private String pictureUrl;

}
