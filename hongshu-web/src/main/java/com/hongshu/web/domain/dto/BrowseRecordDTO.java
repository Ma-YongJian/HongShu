package com.hongshu.web.domain.dto;

import com.hongshu.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author hongshu
 */
@Data
@ApiModel(value = "浏览记录")
public class BrowseRecordDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "uid不能为空", groups = DefaultGroup.class)
    private String uid;

    @NotNull(message = "笔记不能为空", groups = DefaultGroup.class)
    private String nid;
}
