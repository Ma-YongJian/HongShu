package com.hongshu.web.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * 专辑
 *
 * @Author hongshu
 */
@Data
@Accessors(chain = true)
public class AlbumVO implements Serializable {

    /**
     *
     */
    private String id;
    /**
     *
     */
    private String title;

    /**
     *
     */
    private String albumCover;
    /**
     *
     */
    private Integer sort;

    /**
     * 图片数量
     */
    private Long imgCount;

    /**
     * 收藏数量
     */
    private Long collectionCount;

    private String userId;

    private String username;

    private String avatar;
}
