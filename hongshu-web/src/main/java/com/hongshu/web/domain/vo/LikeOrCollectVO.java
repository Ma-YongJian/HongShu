package com.hongshu.web.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 点赞/收藏
 *
 * @Author hongshu
 */
@Data
@Accessors(chain = true)
public class LikeOrCollectVO implements Serializable {

    private String itemId;

    private String itemCover;

    private String uid;

    private String username;

    private String avatar;

    private String content;

    private Long time;

    private Integer type;

}
