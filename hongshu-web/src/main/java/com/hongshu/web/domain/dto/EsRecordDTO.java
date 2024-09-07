package com.hongshu.web.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * ES-笔记
 *
 * @author: hongshu
 */
@Data
public class EsRecordDTO implements Serializable {

    private String keyword;

    private String uid;

}
