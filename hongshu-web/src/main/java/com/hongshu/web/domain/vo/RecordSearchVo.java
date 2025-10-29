package com.hongshu.web.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * ES-记录
 *
 * @Author hongshu
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecordSearchVO implements Serializable {

    private String uid;

    private String content;

    private String highlightContent;

    private Long searchCount;

    private Long time;
}
