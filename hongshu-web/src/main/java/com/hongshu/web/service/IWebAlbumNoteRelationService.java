package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.entity.WebAlbumNoteRelation;
import com.hongshu.web.domain.vo.NoteSearchVO;

/**
 * 专辑-笔记
 *
 * @Author hongshu
 */
public interface IWebAlbumNoteRelationService extends IService<WebAlbumNoteRelation> {

    /**
     * 得到当前专辑下的所有笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param albumId     专辑ID
     * @param userId      用户ID
     */
    Page<NoteSearchVO> getNoteByAlbumId(long currentPage, long pageSize, String albumId, String userId);
}
