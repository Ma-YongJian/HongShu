package com.hongshu.server.controller.web;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.enums.Result;
import com.hongshu.web.domain.vo.NoteSearchVO;
import com.hongshu.web.service.IWebAlbumNoteRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
