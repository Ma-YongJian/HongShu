package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.web.domain.HongshuBaseEntity;
import lombok.Data;

/**
 * 用户-笔记
 *
 * @Author hongshu
 */
@Data
@TableName("web_user_note_relation")
public class WebUserNoteRelation extends HongshuBaseEntity {

    /**
     * 笔记ID
     */
    private String nid;

    /**
     * 用户ID
     */
    private String uid;
}
