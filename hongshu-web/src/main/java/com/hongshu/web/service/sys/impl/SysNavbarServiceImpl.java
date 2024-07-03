package com.hongshu.web.service.sys.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hongshu.common.validator.ValidatorUtil;
import com.hongshu.web.domain.NavbarTreeSelect;
import com.hongshu.web.domain.entity.WebCategory;
import com.hongshu.web.mapper.sys.SysNavbarMapper;
import com.hongshu.web.service.IWebOssService;
import com.hongshu.web.service.sys.ISysNavbarService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 导航栏 业务层处理
 *
 * @author hongshu
 */
@Service
public class SysNavbarServiceImpl implements ISysNavbarService {

    @Autowired
    private IWebOssService ossService;
    @Autowired
    private SysNavbarMapper navbarMapper;
    @Value("${oss.type}")
    Integer type;


    /**
     * 查询系统导航栏列表
     *
     * @param category 导航栏信息
     * @return 导航栏列表
     */
    @Override
    public List<WebCategory> selectNavbarList(WebCategory category) {
        QueryWrapper<WebCategory> qw = new QueryWrapper<>();
        qw.lambda().like(ValidatorUtil.isNotNull(category.getTitle()), WebCategory::getTitle, category.getTitle());
        qw.lambda().like(ValidatorUtil.isNotNull(category.getStatus()), WebCategory::getStatus, category.getStatus());
        qw.orderByAsc("sort");
        return navbarMapper.selectList(qw);
    }

    /**
     * 根据导航栏ID查询信息
     *
     * @param id 导航栏ID
     * @return 导航栏信息
     */
    @Override
    public WebCategory selectById(Long id) {
        return navbarMapper.selectById(id);
    }

    /**
     * 是否存在导航栏子节点
     *
     * @param id 导航栏ID
     * @return 结果
     */
    @Override
    public boolean hasChildById(Long id) {
        int result = navbarMapper.hasChildById(id);
        return result > 0;
    }

    /**
     * 新增导航栏信息
     *
     * @param category 导航栏信息
     * @return 结果
     */
    @Override
    public int insertNavbar(WebCategory category, MultipartFile file) {
        String normalCover = ossService.save(file, type);
        category.setNormalCover(normalCover);
        category.setCreator("system");
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        return navbarMapper.insert(category);
    }

    /**
     * 修改导航栏信息
     *
     * @param category 导航栏信息
     * @return 结果
     */
    @Override
    public int updateNavbar(WebCategory category, MultipartFile file) {
        if (ObjectUtils.isNotEmpty(file)) {
            String normalCover = ossService.save(file, type);
            category.setNormalCover(normalCover);
        }
        category.setUpdater("system");
        category.setUpdateTime(new Date());
        return navbarMapper.updateById(category);
    }

    /**
     * 删除导航栏信息
     *
     * @param id 导航栏ID
     * @return 结果
     */
    @Override
    public int deleteById(Long id) {
        return navbarMapper.deleteById(id);
    }

//    /**
//     * 校验导航栏名称是否唯一
//     *
//     * @param category 导航栏信息
//     * @return 结果
//     */
//    @Override
//    public boolean checkWebNavbarNameUnique(WebCategory category) {
//        Long id = StringUtils.isNull(category.getId()) ? -1L : category.getId();
//        WebCategory info = category.checkWebNavbarNameUnique(category.getTitle(), category.getPid());
//        if (StringUtils.isNotNull(info) && info.getId().longValue() != id.longValue()) {
//            return UserConstants.NOT_UNIQUE;
//        }
//        return UserConstants.UNIQUE;
//    }

    /**
     * 递归列表
     *
     * @param list 分类表
     * @param t    子节点
     */
    private void recursionFn(List<WebCategory> list, WebCategory t) {
        // 得到子节点列表
        List<WebCategory> childList = this.getChildList(list, t);
        t.setChildren(childList);
        for (WebCategory tChild : childList) {
            if (hasChild(list, tChild)) {
                this.recursionFn(list, tChild);
            }
        }
    }

    /**
     * 构建前端所需要树结构
     *
     * @param categoryList 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<WebCategory> buildNavbarTree(List<WebCategory> categoryList) {
        List<WebCategory> returnList = new ArrayList<>();
        List<String> tempList = categoryList.stream().map(WebCategory::getId).collect(Collectors.toList());
        for (Iterator<WebCategory> iterator = categoryList.iterator(); iterator.hasNext(); ) {
            WebCategory category = iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(category.getPid())) {
                this.recursionFn(categoryList, category);
                returnList.add(category);
            }
        }
        if (returnList.isEmpty()) {
            returnList = categoryList;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param categoryList 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<NavbarTreeSelect> buildNavbarTreeSelect(List<WebCategory> categoryList) {
        List<WebCategory> navbarTrees = this.buildNavbarTree(categoryList);
        return navbarTrees.stream().map(NavbarTreeSelect::new).collect(Collectors.toList());
    }

//    /**
//     * 根据父节点的ID获取所有子节点
//     *
//     * @param list 分类表
//     * @param pid  传入的父节点ID
//     * @return String
//     */
//    public List<WebCategory> getChildPerms(List<WebCategory> list, int pid) {
//        List<WebCategory> returnList = new ArrayList<WebCategory>();
//        for (Iterator<WebCategory> iterator = list.iterator(); iterator.hasNext(); ) {
//            WebCategory t = iterator.next();
//            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
//            if (t.getPid() == pid) {
//                recursionFn(list, t);
//                returnList.add(t);
//            }
//        }
//        return returnList;
//    }

//    /**
//     * 递归列表
//     *
//     * @param list 分类表
//     * @param t    子节点
//     */
//    private void recursionFn(List<WebCategory> list, WebCategory t) {
//        // 得到子节点列表
//        List<WebCategory> childList = getChildList(list, t);
//        t.setChildren(childList);
//        for (WebCategory tChild : childList) {
//            if (hasChild(list, tChild)) {
//                recursionFn(list, tChild);
//            }
//        }
//    }

    /**
     * 得到子节点列表
     */
    private List<WebCategory> getChildList(List<WebCategory> list, WebCategory t) {
        List<WebCategory> tlist = new ArrayList<>();
        Iterator<WebCategory> it = list.iterator();
        while (it.hasNext()) {
            WebCategory n = it.next();
            if (n.getPid() == (t.getId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<WebCategory> list, WebCategory t) {
        return getChildList(list, t).size() > 0;
    }
}
