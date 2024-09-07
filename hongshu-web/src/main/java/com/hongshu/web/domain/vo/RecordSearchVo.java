package com.hongshu.web.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ES-记录
 *
 * @author: hongshu
 */
@Data
public class RecordSearchVo implements Serializable {

    private String uid;

    private String content;

    private String highlightContent;

    private Long searchCount;

    private Long time;
}
