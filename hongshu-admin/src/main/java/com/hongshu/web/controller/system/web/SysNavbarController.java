package com.hongshu.web.controller.system.web;

import cn.hutool.json.JSONUtil;
import com.hongshu.common.annotation.Log;
import com.hongshu.common.core.controller.BaseController;
import com.hongshu.common.core.domain.AjaxResult;
import com.hongshu.common.enums.BusinessType;
import com.hongshu.web.domain.entity.WebCategory;
import com.hongshu.web.service.sys.ISysNavbarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 导航栏信息
 *
 * @author hongshu
 */
@RestController
@RequestMapping("/navbar")
public class SysNavbarController extends BaseController {

    @Autowired
    private ISysNavbarService navbarService;


    /**
     * 获取导航栏列表
     */
    @PreAuthorize("@ss.hasPermi('web:navbar:list')")
    @GetMapping("/list")
    public AjaxResult list(WebCategory category) {
        List<WebCategory> categoryList = navbarService.selectNavbarList(category);
        return success(categoryList);
    }

    /**
     * 根据导航栏编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('web:navbar:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(navbarService.selectById(id));
    }

    /**
     * 加载导航栏列表树
     */
    @GetMapping(value = "/navbarTreeSelect")
    public AjaxResult roleMenuTreeSelect(WebCategory category) {
        List<WebCategory> categoryList = navbarService.selectNavbarList(category);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("categoryList", navbarService.buildNavbarTreeSelect(categoryList));
        return ajax;
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeSelect")
    public AjaxResult treeSelect(WebCategory category) {
        List<WebCategory> categoryList = navbarService.selectNavbarList(category);
        return success(navbarService.buildNavbarTreeSelect(categoryList));
    }

    /**
     * 新增导航栏
     */
    @PreAuthorize("@ss.hasPermi('web:navbar:add')")
    @Log(title = "导航栏管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestParam("category") String category, @RequestParam("file") MultipartFile file) {
//        if (!navbarService.checkWebNavbarNameUnique(category)) {
//            return error("新增导航栏'" + category.getTitle() + "'失败，导航栏名称已存在");
//        }
//        category.setCreator(getUsername());
        WebCategory webCategory = JSONUtil.toBean(category, WebCategory.class);
        return toAjax(navbarService.insertNavbar(webCategory, file));
    }

    /**
     * 修改导航栏
     */
    @PreAuthorize("@ss.hasPermi('web:navbar:edit')")
    @Log(title = "导航栏管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestParam("category") String category, @RequestParam(value = "file", required = false) MultipartFile file) {
//    public AjaxResult edit(@Validated @RequestBody WebCategory category) {
//        if (!navbarService.checkWebNavbarNameUnique(category)) {
//            return error("修改导航栏'" + category.getTitle() + "'失败，导航栏名称已存在");
//        } else if (category.getId().equals(category.getPid())) {
//            return error("修改导航栏'" + category.getTitle() + "'失败，上级导航栏不能选择自己");
//        }
//        category.setUpdater(getUsername());
        WebCategory webCategory = JSONUtil.toBean(category, WebCategory.class);
        return toAjax(navbarService.updateNavbar(webCategory, file));
    }

    /**
     * 删除导航栏
     */
    @PreAuthorize("@ss.hasPermi('web:navbar:remove')")
    @Log(title = "导航栏管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id) {
        if (navbarService.hasChildById(id)) {
            return warn("存在子导航栏,不允许删除");
        }
        return toAjax(navbarService.deleteById(id));
    }
}
