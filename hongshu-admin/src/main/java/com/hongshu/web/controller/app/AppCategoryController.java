package com.hongshu.web.controller.app;

import com.hongshu.common.enums.Result;
import com.hongshu.common.validator.myVaildator.noLogin.NoLoginIntercept;
import com.hongshu.web.domain.vo.CategoryVo;
import com.hongshu.web.service.IWebCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类
 *
 * @author: hongshu
 */
@RequestMapping("/app/category")
@RestController
public class AppCategoryController {

    @Autowired
    private IWebCategoryService categoryService;


    /**
     * 获取树形分类数据
     */
    @GetMapping("getCategoryTreeData")
    @NoLoginIntercept
    public Result<?> getCategoryTreeData() {
        List<CategoryVo> categoryList = categoryService.getCategoryTreeData();
        return Result.ok(categoryList);
    }
}
