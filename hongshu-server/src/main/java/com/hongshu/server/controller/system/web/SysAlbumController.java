package com.hongshu.server.controller.system.web;

import com.hongshu.common.annotation.Log;
import com.hongshu.common.core.controller.BaseController;
import com.hongshu.common.core.domain.AjaxResult;
import com.hongshu.common.core.domain.Query;
import com.hongshu.common.core.page.TableDataInfo;
import com.hongshu.common.enums.BusinessType;
import com.hongshu.web.domain.entity.WebAlbum;
import com.hongshu.web.service.ISysAlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 标签操作处理
 *
 * @Author hongshu
 */
@RestController
@RequestMapping("/album")
public class SysAlbumController extends BaseController {

    @Autowired
    private ISysAlbumService albumService;


    /**
     * 获取会员列表
     */
    @PreAuthorize("@ss.hasPermi('web:album:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam Map map) {
        this.startPage();
        List<WebAlbum> albumList = albumService.getAlbumList(new Query(map));
        return getDataTable(albumList);
    }

    /**
     * 根据会员编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('web:album:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(albumService.getAlbumById(id));
    }

    /**
     * 新增会员
     */
    @PreAuthorize("@ss.hasPermi('web:album:add')")
    @Log(title = "会员管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WebAlbum album) {
        return toAjax(albumService.insertAlbum(album));
    }

    /**
     * 修改会员
     */
    @PreAuthorize("@ss.hasPermi('web:album:edit')")
    @Log(title = "会员管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WebAlbum album) {
        return toAjax(albumService.updateAlbum(album));
    }

    /**
     * 删除会员
     */
    @PreAuthorize("@ss.hasPermi('web:album:remove')")
    @Log(title = "会员管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(albumService.deleteAlbumByIds(ids));
    }
}
