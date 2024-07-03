package com.hongshu.web.domain.vo;

import com.hongshu.web.domain.entity.WebCategory;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类
 *
 * @author: hongshu
 */
@Data
public class NavbarVo implements Serializable {

    private Long id;

    /**
     * 父节点id
     */
    private Long pid;

    /**
     * 部门名称
     */
    private String title;

    /**
     * 子部门
     */
    private List<WebCategory> children;
}
