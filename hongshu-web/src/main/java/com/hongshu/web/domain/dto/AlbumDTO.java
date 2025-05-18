package com.hongshu.web.domain.dto;

import com.hongshu.common.validator.group.DefaultGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * 专辑
 *
 * @Author hongshu
 */
@Data
@ApiModel(value = "专辑")
public class AlbumDTO implements Serializable {

    @ApiModelProperty(value = "专辑id")
    private String id;

    @ApiModelProperty(value = "专辑名称")
    @NotBlank(message = "内容不能为空", groups = DefaultGroup.class)
    private String name;

    @ApiModelProperty(value = "专辑发布的用户id")
    @NotNull(message = "用户id不能为空", groups = DefaultGroup.class)
    private String uid;

    @ApiModelProperty(value = "专辑封面")
    private String albumCover;

    @ApiModelProperty(value = "专辑排序")
    private Integer sort;
}
