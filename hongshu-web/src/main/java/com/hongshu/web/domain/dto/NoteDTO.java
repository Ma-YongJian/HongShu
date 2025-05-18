package com.hongshu.web.domain.dto;

import com.hongshu.common.validator.group.AddGroup;
import com.hongshu.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 笔记
 *
 * @Author hongshu
 */
@Data
@ApiModel(value = "笔记DTO")
public class NoteDTO implements Serializable {

    @ApiModelProperty("id")
    @NotBlank(message = "标题不能为空", groups = UpdateGroup.class)
    private String id;

    @ApiModelProperty("笔记标题")
    @NotBlank(message = "标题不能为空", groups = AddGroup.class)
    private String title;

    @ApiModelProperty("笔记内容")
    @NotBlank(message = "内容不能为空", groups = AddGroup.class)
    private String content;

    @ApiModelProperty("笔记封面")
    private String noteCover;

    @ApiModelProperty("笔记封面高度")
    private Integer noteCoverHeight;

    @ApiModelProperty("笔记分类")
    @NotBlank(message = "二级分类不能为空", groups = AddGroup.class)
    private String cid;

    @ApiModelProperty("笔记父分类")
    @NotBlank(message = "一级分类不能为空", groups = AddGroup.class)
    private String cpid;

    @ApiModelProperty("笔记图片地址")
    private List<String> urls;

    @ApiModelProperty("笔记标签")
    private List<String> tagList;

    @ApiModelProperty("笔记图片数量")
    private Integer count;

    @ApiModelProperty("笔记类型")
    private Integer type;
}
