package com.hongshu.server.controller.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.enums.Result;
import com.hongshu.web.domain.dto.AlbumNoteRelationDTO;
import com.hongshu.web.domain.vo.NoteSearchVO;
import com.hongshu.web.service.IWebAlbumNoteRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 专辑-笔记
 *
 * @Author hongshu
 */
@RequestMapping("/web/albumNoteRelation")
@RestController
public class WebAlbumNoteRelationController {

    @Autowired
    private IWebAlbumNoteRelationService albumNoteRelationService;


    /**
     * 得到当前专辑下的所有笔记
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param albumId     专辑ID
     * @param userId      用户ID
     */
    @GetMapping("getNoteByAlbumId/{currentPage}/{pageSize}")
    public Result<?> getNoteByAlbumId(@PathVariable long currentPage, @PathVariable long pageSize, String albumId, String userId) {
        Page<NoteSearchVO> pageInfo = albumNoteRelationService.getNoteByAlbumId(currentPage, pageSize, albumId, userId);
        return Result.ok(pageInfo);
    }

    /**
     * 把笔记加入专辑
     */
    @PostMapping("addAlbumNoteRelation")
    public Result<?> addAlbumNoteRelation(@RequestBody AlbumNoteRelationDTO dto) {
        albumNoteRelationService.addAlbumNoteRelation(dto);
        return Result.ok();
    }

    /**
     * 从专辑移除笔记
     */
    @PostMapping("deleteAlbumNoteRelation")
    public Result<?> deleteAlbumNoteRelation(@RequestBody AlbumNoteRelationDTO dto) {
        albumNoteRelationService.deleteAlbumNoteRelation(dto);
        return Result.ok();
    }
}
