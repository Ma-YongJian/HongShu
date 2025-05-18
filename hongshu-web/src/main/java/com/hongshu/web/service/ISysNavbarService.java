package com.hongshu.web.service;

import com.hongshu.web.domain.NavbarTreeSelect;
import com.hongshu.web.domain.entity.WebNavbar;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 导航栏 业务层
 *
 * @Author hongshu
 */
public interface ISysNavbarService {

    /**
     * 根据用户查询系统导航栏列表
     *
     * @param category 导航栏信息
     */
    List<WebNavbar> selectNavbarList(WebNavbar category);

    /**
     * 根据导航栏ID查询信息
     *
     * @param id 导航栏ID
     * @return 导航栏信息
     */
    WebNavbar selectById(Long id);

    /**
     * 构建前端所需要树结构
     *
     * @param categoryList 菜单列表
     * @return 树结构列表
     */
    List<WebNavbar> buildNavbarTree(List<WebNavbar> categoryList);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param categoryList 导航栏列表
     */
    List<NavbarTreeSelect> buildNavbarTreeSelect(List<WebNavbar> categoryList);

    /**
     * 是否存在导航栏子节点
     *
     * @param id 导航栏ID
     * @return 结果 true 存在 false 不存在
     */
    boolean hasChildById(Long id);

//    /**
//     * 构建前端所需要树结构
//     *
//     * @param categoryList 导航栏列表
//     */
//    List<WebCategory> buildNavbarTree(List<WebCategory> categoryList);

//    /**
//     * 构建前端所需要下拉树结构
//     *
//     * @param categoryList 导航栏列表
//     */
//    List<NavbarTreeSelect> buildNavbarTreeSelect(List<WebCategory> categoryList);

    /**
     * 新增导航栏信息
     *
     * @param category 导航栏信息
     * @return 结果
     */
    int insertNavbar(WebNavbar category, MultipartFile file);

    /**
     * 修改导航栏信息
     *
     * @param category 导航栏信息
     * @return 结果
     */
    int updateNavbar(WebNavbar category, MultipartFile file);

    /**
     * 删除导航栏信息
     *
     * @param id 导航栏ID
     * @return 结果
     */
    int deleteById(Long id);

//    /**
//     * 校验导航栏名称是否唯一
//     *
//     * @param category 导航栏信息
//     * @return 结果
//     */
//    boolean checkWebNavbarNameUnique(WebCategory category);
}
