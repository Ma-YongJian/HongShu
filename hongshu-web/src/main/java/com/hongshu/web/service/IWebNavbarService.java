package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.entity.WebNavbar;
import com.hongshu.web.domain.vo.NavbarVO;

import java.util.List;

/**
 * 分类
 *
 * @Author hongshu
 */
public interface IWebNavbarService extends IService<WebNavbar> {

    /**
     * 获取树形分类数据
     */
    List<NavbarVO> getNavbarTreeData();

}
