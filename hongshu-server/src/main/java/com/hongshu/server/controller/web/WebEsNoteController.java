package com.hongshu.server.controller.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.enums.Result;
import com.hongshu.common.validator.myVaildator.noLogin.NoLoginIntercept;
import com.hongshu.web.domain.dto.EsNoteDTO;
import com.hongshu.web.domain.entity.WebNavbar;
import com.hongshu.web.domain.vo.NoteSearchVO;
import com.hongshu.web.service.IWebEsNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ES
 *
 * @Author hongshu
 */
@RestController
@RequestMapping("/web/es/note")
public class WebEsNoteController {

    @Autowired
    private IWebEsNoteService esNoteService;


    /**
     * 搜索对应的笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param esNoteDTO   笔记
     */
    @NoLoginIntercept
    @PostMapping("getNoteByDTO/{currentPage}/{pageSize}")
    public Result<?> getNoteByDTO(@PathVariable long currentPage, @PathVariable long pageSize, @RequestBody EsNoteDTO esNoteDTO) {
        Page<NoteSearchVO> page = esNoteService.getNoteByDTO(currentPage, pageSize, esNoteDTO);
        return Result.ok(page);
    }

    /**
     * 搜索对应的笔记
     *
     * @param esNoteDTO 笔记
     */
    @NoLoginIntercept
    @PostMapping("getCategoryAgg")
    public Result<?> getCategoryAgg(@RequestBody EsNoteDTO esNoteDTO) {
        List<WebNavbar> categoryList = esNoteService.getCategoryAgg(esNoteDTO);
        return Result.ok(categoryList);
    }

    /**
     * 分页查询笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @NoLoginIntercept
    @GetMapping("getRecommendNote/{currentPage}/{pageSize}")
    public Result<?> getRecommendNote(@PathVariable long currentPage, @PathVariable long pageSize) {
        Page<NoteSearchVO> page = esNoteService.getRecommendNote(currentPage, pageSize);
        return Result.ok(page);
    }

    @GetMapping("getHotNote/{currentPage}/{pageSize}")
    public Result<?> getHotNote(@PathVariable long currentPage, @PathVariable long pageSize) {
        Page<NoteSearchVO> page = esNoteService.getHotNote(currentPage, pageSize);
        return Result.ok(page);
    }

    /**
     * 增加笔记
     *
     * @param noteSearchVo 笔记
     */
    @PostMapping("addNote")
    public void addNote(@RequestBody NoteSearchVO noteSearchVo) {
        esNoteService.addNote(noteSearchVo);
    }

    /**
     * 修改笔记
     *
     * @param noteSearchVo 笔记
     */
    @PostMapping("updateNote")
    public void updateNote(@RequestBody NoteSearchVO noteSearchVo) {
        esNoteService.updateNote(noteSearchVo);
    }

    /**
     * 删除es中的笔记
     *
     * @param noteId 笔记ID
     */
    @RequestMapping("deleteNote/{noteId}")
    public void deleteNote(@PathVariable String noteId) {
        esNoteService.deleteNote(noteId);
    }

    /**
     * 批量增加笔记
     */
    @PostMapping("addNoteBulkData")
    @NoLoginIntercept
    public void addNoteBulkData() {
        esNoteService.addNoteBulkData();
    }

    /**
     * 清空笔记
     */
    @DeleteMapping("delNoteBulkData")
    @NoLoginIntercept
    public void delNoteBulkData() {
        esNoteService.delNoteBulkData();
    }

    /**
     * 重置
     */
    @PostMapping("refreshNoteData")
    @NoLoginIntercept
    public Result<?> refreshNoteData() {
        esNoteService.refreshNoteData();
        return Result.ok();
    }
}
