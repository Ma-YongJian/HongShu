package com.hongshu.web.domain.vo;

import com.hongshu.common.utils.TreeNode;
import lombok.Data;

import java.io.Serializable;

/**
 * 分类
 *
 * @Author hongshu
 */
@Data
public class NavbarVO extends TreeNode<NavbarVO> implements Serializable {

    private String title;
    private long likeCount;
}
