package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.web.domain.HongshuBaseEntity;
import lombok.Data;

/**
 * 关注
 *
 * @Author hongshu
 */
@Data
@TableName("web_follow")
public class WebFollow extends HongshuBaseEntity {

    /**
     * 用户ID
     */
    private String uid;

    /**
     * 关注用户ID
     */
    private String fid;

}
