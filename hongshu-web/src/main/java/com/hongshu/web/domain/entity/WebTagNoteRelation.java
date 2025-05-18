package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.web.domain.HongshuBaseEntity;
import lombok.Data;

/**
 * 标签-笔记
 *
 * @Author hongshu
 */
@Data
@TableName("web_tag_note_relation")
public class WebTagNoteRelation extends HongshuBaseEntity {

    /**
     * 笔记ID
     */
    private String nid;

    /**
     * 标签ID
     */
    private String tid;
}
