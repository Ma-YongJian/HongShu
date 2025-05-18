package com.hongshu.server.controller.system.web;

import com.hongshu.common.constant.Constantss;
import com.hongshu.common.core.controller.BaseController;
import com.hongshu.common.core.domain.AjaxResult;
import com.hongshu.common.enums.EStatus;
import com.hongshu.common.global.SysConf;
import com.hongshu.web.service.ISysCommentService;
import com.hongshu.web.service.ISysMemberService;
import com.hongshu.web.service.ISysNoteService;
import com.hongshu.web.service.ISysVisitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页RestApi
 *
 * @Author hongshu
 * @date 2018年10月22日下午3:27:24
 */
@RestController
@RequestMapping("/index")
@Api(value = "首页相关接口", tags = {"首页相关接口"})
@Slf4j
public class SysDashboardController extends BaseController {

    @Autowired
    private ISysNoteService noteService;
    @Autowired
    private ISysCommentService commentService;
    @Autowired
    private ISysVisitService visitService;
    @Autowired
    private ISysMemberService memberService;


    @ApiOperation(value = "首页初始化数据", notes = "首页初始化数据", response = String.class)
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public AjaxResult init() {
        Map<String, Object> map = new HashMap<>(Constantss.NUM_FOUR);
        map.put(SysConf.VISIT_COUNT, visitService.getWebVisitCount());
        map.put(SysConf.USER_COUNT, memberService.getMemberCount(0));
        map.put(SysConf.BLOG_COUNT, noteService.getNoteCount(EStatus.ENABLE));
        map.put(SysConf.COMMENT_COUNT, commentService.getCommentCount(EStatus.ENABLE));
        return success(map);
    }

    @ApiOperation(value = "获取最近一周用户独立IP数和访问量", notes = "获取最近一周用户独立IP数和访问量", response = String.class)
    @RequestMapping(value = "/getVisitByWeek", method = RequestMethod.GET)
    public AjaxResult getVisitByWeek() {
        Map<String, Object> visitByWeek = visitService.getVisitByWeek();
        return success(visitByWeek);
    }

    @ApiOperation(value = "获取每个标签下文章数目", notes = "获取每个标签下文章数目", response = String.class)
    @RequestMapping(value = "/getBlogCountByTag", method = RequestMethod.GET)
    public AjaxResult getBlogCountByTag() {
        List<Map<String, Object>> blogCountByTag = noteService.getNoteCountByCategory();
        return success(blogCountByTag);
    }

    @ApiOperation(value = "获取每个分类下文章数目", notes = "获取每个分类下文章数目", response = String.class)
    @RequestMapping(value = "/getBlogCountByBlogSort", method = RequestMethod.GET)
    public AjaxResult getBlogCountByBlogSort() {
        List<Map<String, Object>> blogCountByTag = noteService.getNoteCountByCategory();
        return success(blogCountByTag);
    }

    @ApiOperation(value = "获取一年内的文章贡献数", notes = "获取一年内的文章贡献度", response = String.class)
    @RequestMapping(value = "/getBlogContributeCount", method = RequestMethod.GET)
    public AjaxResult getBlogContributeCount() {
        Map<String, Object> resultMap = noteService.getNoteContributeCount();
        return success(resultMap);
    }
}
