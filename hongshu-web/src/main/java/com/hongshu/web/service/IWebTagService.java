package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.entity.WebTag;
import com.hongshu.web.domain.vo.NoteVO;
import com.hongshu.web.domain.vo.TagVO;

import java.util.List;

/**
 * 标签
 *
 * @Author hongshu
 */
public interface IWebTagService extends IService<WebTag> {

    /**
     * 获取热门标签
     */
    List<TagVO> getHotTagList();

    /**
     * 根据标签ID获取图片信息
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param tagId       标签id
     * @param type        类型
     */
    Page<NoteVO> getNoteByTagId(long currentPage, long pageSize, String tagId, Integer type);

    /**
     * 根据关键词获取标签
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param keyword     关键词
     */
    Page<WebTag> getTagByKeyword(long currentPage, long pageSize, String keyword);
}
