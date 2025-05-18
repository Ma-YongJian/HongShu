package com.hongshu.server.controller.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.enums.Result;
import com.hongshu.common.validator.myVaildator.noLogin.NoLoginIntercept;
import com.hongshu.web.domain.vo.NoteVO;
import com.hongshu.web.service.IWebNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 笔记
 *
 * @Author hongshu
 */
@RequestMapping("/web/note")
@RestController
public class WebNoteController {

    @Autowired
    private IWebNoteService noteService;


    /**
     * 获取笔记
     *
     * @param noteId 笔记ID
     */
    @GetMapping("getNoteById")
    @NoLoginIntercept
    public Result<?> getNoteById(String noteId) {
        NoteVO noteVo = noteService.getNoteById(noteId);
        return Result.ok(noteVo);
    }

    /**
     * 新增笔记
     *
     * @param noteData 笔记对象
     * @param files    图片文件
     */
    @PostMapping("saveNoteByDTO")
    public Result<?> saveNoteByDTO(@RequestParam("noteData") String noteData, @RequestParam("uploadFiles") MultipartFile[] files) {
        noteService.saveNoteByDTO(noteData, files);
        return Result.ok();
    }

    /**
     * 删除笔记
     *
     * @param noteIds 笔记ID集合
     */
    @PostMapping("deleteNoteByIds")
    public Result<?> deleteNoteByIds(@RequestBody List<String> noteIds) {
        noteService.deleteNoteByIds(noteIds);
        return Result.ok();
    }

    /**
     * 更新笔记
     *
     * @param noteData 笔记对象
     * @param files    图片文件
     */
    @PostMapping("updateNoteByDTO")
    public Result<?> updateNoteByDTO(@RequestParam("noteData") String noteData, @RequestParam("uploadFiles") MultipartFile[] files) {
        noteService.updateNoteByDTO(noteData, files);
        return Result.ok();
    }

    /**
     * 获取热门笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @GetMapping("getHotPage/{currentPage}/{pageSize}")
    public Result<?> getHotPage(@PathVariable long currentPage, @PathVariable long pageSize) {
        Page<NoteVO> pageInfo = noteService.getHotPage(currentPage, pageSize);
        return Result.ok(pageInfo);
    }

    /**
     * 置顶笔记
     *
     * @param noteId 笔记ID
     */
    @GetMapping("pinnedNote")
    public Result<?> pinnedNote(String noteId) {
        boolean flag = noteService.pinnedNote(noteId);
        return Result.ok(flag);
    }
}
