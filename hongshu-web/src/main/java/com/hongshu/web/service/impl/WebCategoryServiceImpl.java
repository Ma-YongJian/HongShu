package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.common.utils.DozerUtil;
import com.hongshu.common.utils.TreeUtils;
import com.hongshu.web.domain.entity.WebCategory;
import com.hongshu.web.domain.vo.CategoryVo;
import com.hongshu.web.mapper.WebCategoryMapper;
import com.hongshu.web.service.IWebCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类
 *
 * @author: hongshu
 */
@Service
public class WebCategoryServiceImpl extends ServiceImpl<WebCategoryMapper, WebCategory> implements IWebCategoryService {

    /**
     * 获取树形分类数据
     */
    @Override
    public List<CategoryVo> getCategoryTreeData() {
        List<WebCategory> list = this.list(new QueryWrapper<WebCategory>().orderByAsc("sort"));
        List<CategoryVo> convertor = DozerUtil.convertor(list, CategoryVo.class);
        return TreeUtils.build(convertor);
    }
}
