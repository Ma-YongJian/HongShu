package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.entity.WebCategory;
import com.hongshu.web.domain.vo.CategoryVo;

import java.util.List;

/**
 * 分类
 *
 * @author: hongshu
 */
public interface IWebCategoryService extends IService<WebCategory> {

    /**
     * 获取树形分类数据
     */
    List<CategoryVo> getCategoryTreeData();

}
