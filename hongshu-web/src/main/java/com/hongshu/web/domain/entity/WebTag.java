package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.web.domain.HongshuBaseEntity;
import lombok.Data;

/**
 * 标签
 *
 * @Author hongshu
 */
@Data
@TableName("web_tag")
public class WebTag extends HongshuBaseEntity {

    /**
     * 使用次数
     */
    private Long likeCount;

    /**
     * 标题
     */
    private String title;

    /**
     * 排序
     */
    private Integer sort;
}
