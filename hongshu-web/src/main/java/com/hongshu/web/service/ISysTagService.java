package com.hongshu.web.service;

import com.hongshu.common.core.domain.Query;
import com.hongshu.web.domain.entity.WebTag;
import com.hongshu.web.domain.entity.WebUser;

import java.util.List;

/**
 * 会员信息 服务层
 *
 * @Author hongshu
 */
public interface ISysTagService {

    /**
     * 查询会员信息集合
     *
     * @param query 会员信息
     */
    List<WebTag> selectTagList(Query query);

    /**
     * 查询所有会员
     */
    List<WebTag> selectTagAll();

    /**
     * 通过会员ID查询会员信息
     *
     * @param id 会员ID
     */
    WebTag selectTagById(Long id);

    /**
     * 删除会员信息
     *
     * @param id 会员ID
     */
    int deleteTagById(Long id);

    /**
     * 新增保存会员信息
     *
     * @param tag 会员信息
     */
    int insertTag(WebTag tag);

    /**
     * 修改保存会员信息
     *
     * @param tag 会员信息
     */
    int updateTag(WebTag tag);

    /**
     * 批量删除会员信息
     *
     * @param ids 需要删除的会员ID
     */
    int deleteTagByIds(Long[] ids);
}
