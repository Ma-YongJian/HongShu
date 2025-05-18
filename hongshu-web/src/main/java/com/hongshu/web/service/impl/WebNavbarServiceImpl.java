package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.utils.DozerUtil;
import com.hongshu.common.utils.TreeUtils;
import com.hongshu.web.domain.entity.WebNavbar;
import com.hongshu.web.domain.vo.NavbarVO;
import com.hongshu.web.mapper.WebNavbarMapper;
import com.hongshu.web.service.IWebNavbarService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类
 *
 * @Author hongshu
 */
@Service
public class WebNavbarServiceImpl extends ServiceImpl<WebNavbarMapper, WebNavbar> implements IWebNavbarService {

    /**
     * 获取树形分类数据
     */
    @Override
    public List<NavbarVO> getNavbarTreeData() {
        List<WebNavbar> list = this.list(new QueryWrapper<WebNavbar>().orderByAsc("sort"));
        List<NavbarVO> convertor = DozerUtil.convertor(list, NavbarVO.class);
        return TreeUtils.build(convertor);
    }
}
