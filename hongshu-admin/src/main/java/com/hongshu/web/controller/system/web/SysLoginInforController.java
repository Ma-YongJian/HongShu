package com.hongshu.web.controller.system.web;

import com.hongshu.common.annotation.Log;
import com.hongshu.common.core.controller.BaseController;
import com.hongshu.common.core.domain.AjaxResult;
import com.hongshu.common.core.domain.Query;
import com.hongshu.common.core.page.TableDataInfo;
import com.hongshu.common.enums.BusinessType;
import com.hongshu.common.utils.poi.ExcelUtil;
import com.hongshu.framework.web.service.SysPasswordService;
import com.hongshu.system.domain.SysLogininfor;
import com.hongshu.system.service.ISysLogininforService;
import com.hongshu.web.domain.entity.WebLoginInfor;
import com.hongshu.web.service.sys.ISysLoginInforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 系统访问记录
 *
 * @author: hongshu
 */
@RestController
@RequestMapping("/loginInfo")
public class SysLoginInforController extends BaseController {

    @Autowired
    private ISysLoginInforService loginInforService;


    @PreAuthorize("@ss.hasPermi('web:logininfor:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam Map map) {
        startPage();
        List<WebLoginInfor> list = loginInforService.selectLoginInforList(new Query(map));
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('web:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public AjaxResult remove(@PathVariable Long[] infoIds) {
        return toAjax(loginInforService.deleteLoginInforByIds(infoIds));
    }

    @PreAuthorize("@ss.hasPermi('web:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public AjaxResult clean() {
        loginInforService.cleanLoginInfor();
        return success();
    }
}
