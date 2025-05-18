package com.hongshu.web.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hongshu.common.core.domain.SuperEntity;
import lombok.Data;

/**
 * 文件分类
 *
 * @Author hongshu
 * @since 2018-09-17
 */
@TableName("t_file_sort")
@Data
public class WebFileSort extends SuperEntity<WebFileSort> {

    private static final long serialVersionUID = 1L;

    /**
     * 项目名
     */
    private String projectName;

    /**
     * 模块分类名
     */
    private String sortName;

    /**
     * 存储路径
     */
    private String url;
}
