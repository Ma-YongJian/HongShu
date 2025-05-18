package com.hongshu.web.websocket.im;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author hongshu
 */
@Data
public class CountMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String uid;

    private Long likeOrCollectionCount;

    private Long commentCount;

    private Long followCount;
}
