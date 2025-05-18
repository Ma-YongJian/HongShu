package com.hongshu.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.constant.UserConstant;
import com.hongshu.common.enums.ResultCodeEnum;
import com.hongshu.common.exception.web.HongshuException;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.common.utils.WebUtils;
import com.hongshu.web.domain.entity.WebLikeOrCollect;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.NoteSearchVO;
import com.hongshu.web.mapper.WebLikeOrCollectMapper;
import com.hongshu.web.mapper.WebNoteMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.IWebEsNoteService;
import com.hongshu.web.service.IWebUserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户
 *
 * @Author hongshu
 */
@Service
public class WebUserServiceImpl extends ServiceImpl<WebUserMapper, WebUser> implements IWebUserService {

    @Autowired
    private WebUserMapper userMapper;
    @Autowired
    private WebNoteMapper noteMapper;
    @Autowired
    private IWebEsNoteService esNoteService;
    @Autowired
    private WebLikeOrCollectMapper likeOrCollectionMapper;


    /**
     * 获取当前用户信息
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param userId      用户ID
     * @param type        类型
     */
    @Override
    public Page<NoteSearchVO> getTrendByUser(long currentPage, long pageSize, String userId, Integer type) {
        Page<NoteSearchVO> resultPage;
        if (type == 1) {
            resultPage = this.getLikeOrCollectionPageByUser(currentPage, pageSize, userId);
        } else {
            resultPage = this.getLikeOrCollectionPageByUser(currentPage, pageSize, userId, type);
        }
        return resultPage;
    }

    @Override
    public WebUser getUserById(String userId) {
        WebUser user = userMapper.selectById(userId);
        if (ObjectUtils.isEmpty(user)) {
            throw new HongshuException(ResultCodeEnum.FAIL);
        }
        List<WebNote> noteList = noteMapper.selectList(new QueryWrapper<WebNote>().like("uid", userId));
        user.setTrendCount((long) noteList.size());
        return user;
    }

    /**
     * 更新用户信息
     *
     * @param user 用户
     */
    @Override
    public WebUser updateUser(WebUser user) {
        WebUser webUser = userMapper.selectById(user.getId());
        if (ObjectUtils.isEmpty(webUser)) {
            throw new HongshuException(ResultCodeEnum.FAIL);
        }
        webUser.setAvatar(user.getAvatar());
        webUser.setUsername(user.getUsername());
        webUser.setDescription(user.getDescription());
        webUser.setTags(user.getTags());
        webUser.setUpdateTime(new Date());
        userMapper.updateById(webUser);

        // TODO 更新用户ES数据(待优化)
        esNoteService.refreshNoteData();

        return webUser;
    }

    /**
     * 查找用户信息
     *
     * @param keyword 关键词
     * @return
     */
    @Override
    public Page<WebUser> getUserByKeyword(long currentPage, long pageSize, String keyword) {
        Page<WebUser> resultPage;
        resultPage = userMapper.selectPage(new Page<>((int) currentPage, (int) pageSize), new QueryWrapper<WebUser>().like("username", keyword));
        return resultPage;
    }

    /**
     * 保存用户的搜索记录
     *
     * @param keyword 关键词
     */
    @Override
    public void saveUserSearchRecord(String keyword) {
    }

    /**
     * 用户数据
     */
    private Page<NoteSearchVO> getLikeOrCollectionPageByUser(long currentPage, long pageSize, String userId, Integer type) {
        Page<NoteSearchVO> noteSearchVoPage = new Page<>();
        Page<WebLikeOrCollect> likeOrCollectionPage;
        // 得到当前用户发布的所有图片
        if (type == 2) {
            // 所有点赞图片
            likeOrCollectionPage = likeOrCollectionMapper.selectPage(new Page<>(currentPage, pageSize), new QueryWrapper<WebLikeOrCollect>().eq("uid", userId).eq("type", 1).orderByDesc("create_time"));
        } else {
            // 所有收藏图片
            likeOrCollectionPage = likeOrCollectionMapper.selectPage(new Page<>(currentPage, pageSize), new QueryWrapper<WebLikeOrCollect>().eq("uid", userId).eq("type", 3).orderByDesc("create_time"));
        }
        List<WebLikeOrCollect> likeOrCollectionList = likeOrCollectionPage.getRecords();
        long total = likeOrCollectionPage.getTotal();

        // 是否点赞
        List<WebLikeOrCollect> likeOrCollections = likeOrCollectionMapper.selectList(new QueryWrapper<WebLikeOrCollect>().eq("uid", userId).eq("type", 1));
        List<String> likeOrCollectionIds = likeOrCollections.stream().map(WebLikeOrCollect::getLikeOrCollectionId).collect(Collectors.toList());


        Set<String> uids = likeOrCollectionList.stream().map(WebLikeOrCollect::getPublishUid).collect(Collectors.toSet());
        Map<String, WebUser> userMap = this.listByIds(uids).stream().collect(Collectors.toMap(WebUser::getId, user -> user));

        Set<String> nids = likeOrCollectionList.stream().map(WebLikeOrCollect::getLikeOrCollectionId).collect(Collectors.toSet());
        Map<String, WebNote> noteMap = noteMapper.selectBatchIds(nids).stream().collect(Collectors.toMap(WebNote::getId, note -> note));

        List<NoteSearchVO> noteSearchVOList = new ArrayList<>();

        for (WebLikeOrCollect model : likeOrCollectionList) {
            WebNote note = noteMap.get(model.getLikeOrCollectionId());
            NoteSearchVO noteSearchVo = ConvertUtils.sourceToTarget(note, NoteSearchVO.class);
            WebUser user = userMap.get(model.getPublishUid());
            noteSearchVo.setUsername(user.getUsername())
                    .setIsLike(likeOrCollectionIds.contains(note.getId()))
                    .setAvatar(user.getAvatar());
            noteSearchVOList.add(noteSearchVo);
        }
        noteSearchVoPage.setRecords(noteSearchVOList);
        noteSearchVoPage.setTotal(total);
        return noteSearchVoPage;
    }

    /**
     * 根据条件分页查询角色数据
     *
     * @param user 角色信息
     * @return 角色数据集合信息
     */
    @Override
//    @DataScope(deptAlias = "d")
    public List<WebUser> getUserList(WebUser user) {
        return userMapper.getUserList(user);
    }

    /**
     * 用户数据
     */
    private Page<NoteSearchVO> getLikeOrCollectionPageByUser(long currentPage, long pageSize, String userId) {
        Page<NoteSearchVO> noteSearchVoPage = new Page<>();
        // 得到当前用户发布的所有专辑
        String currentUserId = WebUtils.getRequestHeader(UserConstant.USER_ID);
        Page<WebNote> notePage;
        notePage = noteMapper.selectPage(new Page<>(currentPage, pageSize), new QueryWrapper<WebNote>().eq("uid", userId).orderByDesc("pinned", "update_time"));
        List<WebNote> noteList = notePage.getRecords();
        long total = notePage.getTotal();

        // 得到所有用户的信息
        Set<String> uids = noteList.stream().map(WebNote::getUid).collect(Collectors.toSet());

        // 是否点赞
        List<WebLikeOrCollect> likeOrCollections = likeOrCollectionMapper.selectList(new QueryWrapper<WebLikeOrCollect>().eq("uid", userId).eq("type", 1));
        List<String> likeOrCollectionIds = likeOrCollections.stream().map(WebLikeOrCollect::getLikeOrCollectionId).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(uids)) {
            Map<String, WebUser> userMap = this.listByIds(uids).stream().collect(Collectors.toMap(WebUser::getId, user -> user));
            List<NoteSearchVO> noteSearchVOList = new ArrayList<>();
            for (WebNote note : noteList) {
                NoteSearchVO noteSearchVo = ConvertUtils.sourceToTarget(note, NoteSearchVO.class);
                WebUser user = userMap.get(note.getUid());
                noteSearchVo.setUsername(user.getUsername())
                        .setAvatar(user.getAvatar())
                        .setIsLike(likeOrCollectionIds.contains(note.getId()))
                        .setTime(note.getUpdateTime().getTime());
                if (!currentUserId.equals(userId)) {
                    noteSearchVo.setViewCount(null);
                }
                noteSearchVOList.add(noteSearchVo);
            }
            noteSearchVoPage.setRecords(noteSearchVOList);
            noteSearchVoPage.setTotal(total);
        }
        return noteSearchVoPage;
    }
}
