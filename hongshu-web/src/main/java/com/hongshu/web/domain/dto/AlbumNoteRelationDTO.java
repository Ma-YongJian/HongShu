package com.hongshu.web.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 专辑-笔记
 *
 * @Author hongshu
 */
@Data
public class AlbumNoteRelationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String aid;

    private String nid;
}
