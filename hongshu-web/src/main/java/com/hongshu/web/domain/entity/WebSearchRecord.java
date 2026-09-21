package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.web.domain.HongshuBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 搜索记录（开源版走 MySQL）
 *
 * @Author hongshu
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("web_search_record")
public class WebSearchRecord extends HongshuBaseEntity {

    private String uid;

    private String content;

    private Long searchCount;
}
