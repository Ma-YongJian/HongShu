package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.dto.EsNoteDTO;
import com.hongshu.web.domain.entity.WebCategory;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.vo.NoteSearchVo;

import java.util.List;

/**
 * ES
 *
 * @author: hongshu
 */
public interface IWebEsNoteService extends IService<WebNote> {

    /**
     * 搜索对应的笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param esNoteDTO   笔记
     */
    Page<NoteSearchVo> getNoteByDTO(long currentPage, long pageSize, EsNoteDTO esNoteDTO);

    /**
     * 搜索对应的笔记
     *
     * @param esNoteDTO 笔记
     */
    List<WebCategory> getCategoryAgg(EsNoteDTO esNoteDTO);

    /**
     * 分页查询笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    Page<NoteSearchVo> getRecommendNote(long currentPage, long pageSize);

    /**
     * 增加笔记
     *
     * @param noteSearchVo 笔记
     */
    void addNote(NoteSearchVo noteSearchVo);

    /**
     * 修改笔记
     *
     * @param noteSearchVo 笔记
     */
    void updateNote(NoteSearchVo noteSearchVo);

    /**
     * 批量增加笔记
     */
    void addNoteBulkData();

    /**
     * 删除es中的笔记
     *
     * @param noteId 笔记 ID
     */
    void deleteNote(String noteId);

    /**
     * 清空笔记
     */
    void delNoteBulkData();

    /**
     * 重置
     */
    void refreshNoteData();
}
