package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hongshu.common.core.domain.Query;
import com.hongshu.common.validator.ValidatorUtil;
import com.hongshu.web.domain.entity.WebTag;
import com.hongshu.web.mapper.SysTagMapper;
import com.hongshu.web.service.ISysTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 会员信息 服务层处理
 *
 * @Author hongshu
 */
@Slf4j
@Service
public class SysTagServiceImpl implements ISysTagService {

    @Autowired
    private SysTagMapper tagMapper;


    /**
     * 查询会员信息集合
     *
     * @param query 会员信息
     */
    @Override
    public List<WebTag> selectTagList(Query query) {
        QueryWrapper<WebTag> qw = new QueryWrapper<>();
        qw.lambda().like(ValidatorUtil.isNotNull(query.getTitle()), WebTag::getTitle, query.getTitle());
        qw.lambda().ge(ValidatorUtil.isNotNull(query.get("params[beginTime]")), WebTag::getCreateTime, query.get("params[beginTime]"));
        qw.lambda().le(ValidatorUtil.isNotNull(query.get("params[endTime]")), WebTag::getCreateTime, query.get("params[endTime]"));
        qw.orderByDesc("like_count");
        return tagMapper.selectList(qw);
    }

    /**
     * 查询所有会员
     */
    @Override
    public List<WebTag> selectTagAll() {
        return tagMapper.selectList(new QueryWrapper<>());
    }

    /**
     * 通过会员ID查询会员信息
     *
     * @param id 会员ID
     */
    @Override
    public WebTag selectTagById(Long id) {
        return tagMapper.selectById(id);
    }

    /**
     * 新增会员信息
     *
     * @param tag 会员信息
     */
    @Override
    public int insertTag(WebTag tag) {
        tag.setTitle(tag.getTitle());
        tag.setSort(tag.getSort());
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());
        return tagMapper.insert(tag);
    }

    /**
     * 修改保存会员信息
     *
     * @param tag 会员信息
     */
    @Override
    public int updateTag(WebTag tag) {
        WebTag webTag = tagMapper.selectById(tag.getId());
        webTag.setTitle(tag.getTitle());
        webTag.setSort(tag.getSort());
        webTag.setUpdater("System");
        webTag.setUpdateTime(new Date());
        return tagMapper.updateById(webTag);
    }

    /**
     * 删除会员信息
     *
     * @param id 会员ID
     */
    @Override
    public int deleteTagById(Long id) {
        return tagMapper.deleteById(id);
    }

    /**
     * 批量删除会员信息
     *
     * @param ids 需要删除的会员ID
     */
    @Override
    public int deleteTagByIds(Long[] ids) {
        List<Long> longs = Arrays.asList(ids);
        for (Long id : ids) {
            WebTag tag = selectTagById(id);
            if (ValidatorUtil.isNull(tag)) {
                log.info("用户不存在:{}", id);
                longs.remove(id);
            }
        }
        return tagMapper.deleteBatchIds(longs);
    }
}
