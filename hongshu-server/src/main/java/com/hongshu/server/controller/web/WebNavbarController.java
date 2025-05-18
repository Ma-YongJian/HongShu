package com.hongshu.server.controller.web;

import com.hongshu.common.enums.Result;
import com.hongshu.common.validator.myVaildator.noLogin.NoLoginIntercept;
import com.hongshu.web.domain.vo.NavbarVO;
import com.hongshu.web.service.IWebNavbarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类
 *
 * @Author hongshu
 */
@RequestMapping("/web/category")
@RestController
public class WebNavbarController {

    @Autowired
    private IWebNavbarService navbarService;


    /**
     * 获取树形分类数据
     */
    @GetMapping("getCategoryTreeData")
    @NoLoginIntercept
    public Result<?> getCategoryTreeData() {
        List<NavbarVO> navbarList = navbarService.getNavbarTreeData();
        return Result.ok(navbarList);
    }
}
