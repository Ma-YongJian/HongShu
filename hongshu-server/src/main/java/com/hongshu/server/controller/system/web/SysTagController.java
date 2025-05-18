package com.hongshu.server.controller.system.web;

import com.hongshu.common.annotation.Log;
import com.hongshu.common.core.controller.BaseController;
import com.hongshu.common.core.domain.AjaxResult;
import com.hongshu.common.core.domain.Query;
import com.hongshu.common.core.page.TableDataInfo;
import com.hongshu.common.enums.BusinessType;
import com.hongshu.web.domain.entity.WebTag;
import com.hongshu.web.service.ISysTagService;
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
@RequestMapping("/tag")
public class SysTagController extends BaseController {

    @Autowired
    private ISysTagService tagService;


    /**
     * 获取会员列表
     */
    @PreAuthorize("@ss.hasPermi('web:tag:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam Map map) {
        this.startPage();
        List<WebTag> tagList = tagService.selectTagList(new Query(map));
        return getDataTable(tagList);
    }

    /**
     * 根据会员编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('web:tag:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(tagService.selectTagById(id));
    }

    /**
     * 新增会员
     */
    @PreAuthorize("@ss.hasPermi('web:tag:add')")
    @Log(title = "会员管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WebTag tag) {
        return toAjax(tagService.insertTag(tag));
    }

    /**
     * 修改会员
     */
    @PreAuthorize("@ss.hasPermi('web:tag:edit')")
    @Log(title = "会员管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WebTag tag) {
        return toAjax(tagService.updateTag(tag));
    }

    /**
     * 删除会员
     */
    @PreAuthorize("@ss.hasPermi('web:tag:remove')")
    @Log(title = "会员管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(tagService.deleteTagByIds(ids));
    }
}
