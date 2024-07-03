package com.hongshu.web.domain.vo;

import com.hongshu.common.utils.TreeNode;
import lombok.Data;

import java.io.Serializable;

/**
 * 分类
 *
 * @author: hongshu
 */
@Data
public class CategoryVo extends TreeNode<CategoryVo> implements Serializable {

    private String title;
    private long likeCount;
}
