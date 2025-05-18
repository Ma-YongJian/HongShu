package com.hongshu.web.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ES-记录
 *
 * @Author hongshu
 */
@Data
public class RecordSearchVO implements Serializable {

    private String uid;

    private String content;

    private String highlightContent;

    private Long searchCount;

    private Long time;
}
