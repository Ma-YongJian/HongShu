package com.hongshu.web.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 关注
 *
 * @author: hongshu
 */
@Data
@Accessors(chain = true)
public class FollowerVo implements Serializable {

    private String uid;

    private String username;

    private String avatar;

    private Long hsId;

    private Long fanCount;

    private Boolean isFollow;

    private Long time;
}
