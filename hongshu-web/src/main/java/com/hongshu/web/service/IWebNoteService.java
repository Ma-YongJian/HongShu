package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.vo.NoteVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 笔记
 *
 * @Author hongshu
 */
public interface IWebNoteService extends IService<WebNote> {

    /**
     * 获取笔记
     *
     * @param noteId 笔记ID
     */
    NoteVO getNoteById(String noteId);

    /**
     * 新增笔记
     *
     * @param noteData 笔记对象
     * @param files    图片文件
     */
    String saveNoteByDTO(String noteData, MultipartFile[] files);

    /**
     * 删除笔记
     *
     * @param noteIds 笔记ID集合
     */
    void deleteNoteByIds(List<String> noteIds);

    /**
     * 更新笔记
     *
     * @param noteData 笔记对象
     * @param files    图片文件
     */
    void updateNoteByDTO(String noteData, MultipartFile[] files);

    /**
     * 获取热门笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    Page<NoteVO> getHotPage(long currentPage, long pageSize);

    /**
     * 置顶笔记
     *
     * @param noteId 笔记ID
     */
    boolean pinnedNote(String noteId);
}
