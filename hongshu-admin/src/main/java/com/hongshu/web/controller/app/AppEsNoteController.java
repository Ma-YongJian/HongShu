package com.hongshu.web.controller.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.enums.Result;
import com.hongshu.common.validator.myVaildator.noLogin.NoLoginIntercept;
import com.hongshu.web.domain.dto.EsNoteDTO;
import com.hongshu.web.domain.entity.WebCategory;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.NoteSearchVo;
import com.hongshu.web.service.IWebEsNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ES
 *
 * @author: hongshu
 */
@RestController
@RequestMapping("/app/es/note")
public class AppEsNoteController {

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
        Page<NoteSearchVo> page = esNoteService.getNoteByDTO(currentPage, pageSize, esNoteDTO);
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
        List<WebCategory> categoryList = esNoteService.getCategoryAgg(esNoteDTO);
        return Result.ok(categoryList);
    }

    /**
     * 获取推荐笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @NoLoginIntercept
    @GetMapping("getRecommendNote/{currentPage}/{pageSize}")
    public Result<?> getRecommendNote(@PathVariable long currentPage, @PathVariable long pageSize) {
        Page<NoteSearchVo> page = esNoteService.getRecommendNote(currentPage, pageSize);
        return Result.ok(page);
    }

    /**
     * 获取推荐用户
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @NoLoginIntercept
    @GetMapping("getRecommendUser/{currentPage}/{pageSize}")
    public Result<?> getRecommendUser(@PathVariable long currentPage, @PathVariable long pageSize) {
        Page<WebUser> page = esNoteService.getRecommendUser(currentPage, pageSize);
        return Result.ok(page);
    }

    /**
     * 获取热榜笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @NoLoginIntercept
    @GetMapping("getHotNote/{currentPage}/{pageSize}")
    public Result<?> getHotNote(@PathVariable long currentPage, @PathVariable long pageSize) {
        Page<NoteSearchVo> page = esNoteService.getHotNote(currentPage, pageSize);
        return Result.ok(page);
    }

    /**
     * 增加笔记
     *
     * @param noteSearchVo 笔记
     */
    @PostMapping("addNote")
    public void addNote(@RequestBody NoteSearchVo noteSearchVo) {
        esNoteService.addNote(noteSearchVo);
    }

    /**
     * 修改笔记
     *
     * @param noteSearchVo 笔记
     */
    @PostMapping("updateNote")
    public void updateNote(@RequestBody NoteSearchVo noteSearchVo) {
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
