package com.hongshu.web.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 关注
 *
 * @Author hongshu
 */
@Data
@Accessors(chain = true)
public class FollowVO implements Serializable {

    private String uid;

    private String username;

    private String avatar;

    private Long hsId;

    private Long fanCount;

    private Boolean isFollow;

    private Long time;


}
