package com.hongshu.web.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.constant.UserConstant;
import com.hongshu.common.utils.WebUtils;
import com.hongshu.web.auth.AuthContextHolder;
import com.hongshu.web.domain.entity.WebFollow;
import com.hongshu.web.domain.entity.WebLikeOrCollect;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.FollowVO;
import com.hongshu.web.domain.vo.TrendVO;
import com.hongshu.web.mapper.WebFollowMapper;
import com.hongshu.web.mapper.WebLikeOrCollectMapper;
import com.hongshu.web.mapper.WebNoteMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.IWebFollowService;
import com.hongshu.web.websocket.im.ChatUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author hongshu
 */
@Service
public class WebFollowServiceImpl extends ServiceImpl<WebFollowMapper, WebFollow> implements IWebFollowService {

    @Autowired
    WebNoteMapper noteMapper;
    @Autowired
    WebUserMapper userMapper;
    @Autowired
    WebLikeOrCollectMapper likeOrCollectMapper;
    @Autowired
    ChatUtils chatUtils;


    /**
     * 获取关注用户的所有动态
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @Override
    public Page<TrendVO> getFollowTrend(long currentPage, long pageSize) {
        Page<TrendVO> page = new Page<>();
        String currentUid = AuthContextHolder.getUserId();
        if (StringUtils.isBlank(currentUid)) {
            page.setTotal(0);
            page.setRecords(new ArrayList<>());
            return page;
        }
        List<WebFollow> followers = this.list(new QueryWrapper<WebFollow>().eq("uid", currentUid));
        List<String> fids = followers.stream().map(WebFollow::getFid).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        fids.add(currentUid);
        Page<WebNote> notePage = noteMapper.selectPage(new Page<>((int) currentPage, (int) pageSize),
                new QueryWrapper<WebNote>()
                        .eq("status", "1")
                        .and(w -> w.ne("audit_status", "2").or().isNull("audit_status"))
                        .and(w -> w.ne("note_type", "2").or().isNull("note_type"))
                        .in("uid", fids)
                        .orderByDesc("update_time"));
        List<WebNote> notes = notePage.getRecords();
        List<TrendVO> trendVOS = new ArrayList<>();
        if (!notes.isEmpty()) {
            List<String> ids = notes.stream().map(WebNote::getUid).collect(Collectors.toList());
            List<WebUser> users = userMapper.selectBatchIds(ids);
            HashMap<String, WebUser> userMap = new HashMap<>();
            users.forEach(item -> {
                if (item != null && item.getId() != null) {
                    userMap.put(String.valueOf(item.getId()), item);
                }
            });
            List<WebLikeOrCollect> likeOrCollections = likeOrCollectMapper.selectList(new QueryWrapper<WebLikeOrCollect>().eq("uid", currentUid).eq("type", 1));
            List<String> likeOrCollectionIds = likeOrCollections.stream().map(WebLikeOrCollect::getLikeOrCollectionId).collect(Collectors.toList());
            for (WebNote note : notes) {
                WebUser user = userMap.get(String.valueOf(note.getUid()));
                TrendVO trendVo = new TrendVO();
                String text = StringUtils.isNotBlank(note.getTitle()) ? note.getTitle() : note.getContent();
                trendVo.setUid(String.valueOf(note.getUid()))
                        .setUsername(user != null ? user.getUsername() : "用户")
                        .setAvatar(user != null ? user.getAvatar() : null)
                        .setNid(String.valueOf(note.getId()))
                        .setTime(note.getUpdateTime() == null ? System.currentTimeMillis() : note.getUpdateTime().getTime())
                        .setContent(text)
                        .setCommentCount(note.getCommentCount())
                        .setLikeCount(note.getLikeCount())
                        .setIsLike(likeOrCollectionIds.contains(note.getId()) || likeOrCollectionIds.contains(String.valueOf(note.getId())))
                        .setIsLoading(false);
                trendVo.setImgUrls(parseNoteUrls(note.getUrls(), note.getNoteCover()));
                trendVOS.add(trendVo);
            }
        }
        page.setTotal(notePage.getTotal());
        page.setRecords(trendVOS);
        return page;
    }

    /**
     * 获取关注列表
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @Override
    public Page<TrendVO> getFollowList(long currentPage, long pageSize) {
        Page<TrendVO> page = new Page<>();
        String currentUid = AuthContextHolder.getUserId();
        List<WebFollow> followers = this.list(new QueryWrapper<WebFollow>().eq("uid", currentUid));
        List<String> fids = followers.stream().map(WebFollow::getFid).collect(Collectors.toList());
        if (fids.isEmpty()) {
            page.setTotal(0);
            page.setRecords(new ArrayList<>());
            return page;
        }
        Page<WebNote> notePage = noteMapper.selectPage(new Page<>((int) currentPage, (int) pageSize),
                new QueryWrapper<WebNote>()
                        .eq("audit_status", "1")
                        .in("uid", fids)
                        .last("ORDER BY RAND()")); // 添加随机排序
        List<WebNote> notes = notePage.getRecords();
        List<TrendVO> trendVOS = new ArrayList<>();
        if (!notes.isEmpty()) {
            // 得到所有用户的图片
            List<String> ids = notes.stream().map(WebNote::getUid).collect(Collectors.toList());
            List<WebUser> users = userMapper.selectBatchIds(ids);
            HashMap<String, WebUser> userMap = new HashMap<>();
            users.forEach(item -> userMap.put(String.valueOf(item.getId()), item));
            // 是否点赞
            List<WebLikeOrCollect> likeOrCollections = likeOrCollectMapper.selectList(new QueryWrapper<WebLikeOrCollect>().eq("uid", currentUid).eq("type", 1));
            List<String> likeOrCollectionIds = likeOrCollections.stream().map(WebLikeOrCollect::getLikeOrCollectionId).collect(Collectors.toList());
            for (WebNote note : notes) {
                WebUser user = userMap.get(note.getUid());
                if (user == null) {
                    continue;
                }
                TrendVO trendVo = new TrendVO();
                trendVo.setUid(String.valueOf(user.getId()))
                        .setUsername(user.getUsername())
                        .setAvatar(user.getAvatar())
                        .setNid(String.valueOf(note.getId()))
                        .setTime(note.getUpdateTime() == null ? System.currentTimeMillis() : note.getUpdateTime().getTime())
                        .setContent(note.getContent())
                        .setCommentCount(note.getCommentCount())
                        .setLikeCount(note.getLikeCount())
                        .setViewCount(note.getViewCount())
                        .setIsLike(likeOrCollectionIds.contains(note.getId()))
                        .setIsLoading(false);
                List<String> imgList = parseNoteUrls(note.getUrls(), note.getNoteCover());
                trendVo.setImgUrls(imgList.size() > 4 ? imgList.subList(0, 4) : imgList);
                trendVOS.add(trendVo);
            }
        }
        long total = notePage.getTotal();
        page.setTotal(total);
        page.setRecords(trendVOS);
        return page;
    }

    /**
     * 获取当前用户所有的关注和粉丝
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     * @param type        类型
     */
    @Override
    public Page<FollowVO> getFriend(long currentPage, long pageSize, String uid, Integer type) {

        Page<FollowVO> result = new Page<>();
        Page<WebFollow> followerPage = null;
        List<WebFollow> followerList = null;
        Set<String> uids = null;
        if (type == 0) {
            // 粉丝列表
            followerPage = this.page(new Page<>((int) currentPage, (int) pageSize), new QueryWrapper<WebFollow>().eq("fid", uid));
            followerList = followerPage.getRecords();
            uids = followerList.stream().map(WebFollow::getUid).collect(Collectors.toSet());
        } else if (type == 1) {
            // 关注列表
            followerPage = this.page(new Page<>((int) currentPage, (int) pageSize), new QueryWrapper<WebFollow>().eq("uid", uid));
            followerList = followerPage.getRecords();
            uids = followerList.stream().map(WebFollow::getFid).collect(Collectors.toSet());
        }
        long total = followerPage != null ? followerPage.getTotal() : 0;
        Map<String, WebUser> userMap = userMapper.selectBatchIds(uids).stream().collect(Collectors.toMap(WebUser::getId, user -> user));

        // 得到当前用户的所有关注
//        List<WebFollower> followers = this.list(new QueryWrapper<WebFollower>().eq("uid", uid));
//        Set<String> followerSet = followers.stream().map(WebFollower::getFid).collect(Collectors.toSet());

        List<FollowVO> followVOList = new ArrayList<>();
        followerList.forEach(item -> {
            WebUser user = null;
            FollowVO followVo = new FollowVO();
            if (type == 0) {
                user = userMap.get(item.getUid());
            } else if (type == 1) {
                user = userMap.get(item.getFid());
            }
            followVo.setUid(user.getId())
                    .setUsername(user.getUsername())
                    .setAvatar(user.getAvatar())
                    .setHsId(user.getHsId())
                    .setFanCount(user.getFanCount())
                    .setTime(item.getCreateTime().getTime());
//                    .setIsFollow(followerSet.contains(item.getUid()));
            followVOList.add(followVo);
        });
        result.setRecords(followVOList);
        result.setTotal(total);
        return result;
    }

    /**
     * 关注用户
     *
     * @param followerId 关注用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void followById(String followerId) {
        WebFollow follower = new WebFollow();
        String userId = AuthContextHolder.getUserId();
        follower.setUid(userId);
        follower.setFid(followerId);
        follower.setCreateTime(new Date());
        follower.setUpdateTime(new Date());
        // 得到当前用户
        WebUser currentUser = userMapper.selectById(userId);
        WebUser followerUser = userMapper.selectById(followerId);
        if (isFollow(followerId)) {
            currentUser.setFollowerCount(currentUser.getFollowerCount() - 1);
            followerUser.setFanCount(followerUser.getFanCount() - 1);
            this.remove(new QueryWrapper<WebFollow>().eq("uid", userId).eq("fid", followerId));
        } else {
            currentUser.setFollowerCount(currentUser.getFollowerCount() + 1);
            followerUser.setFanCount(followerUser.getFanCount() + 1);
            this.save(follower);
            chatUtils.sendMessage(followerId, 2);
        }
        userMapper.updateById(currentUser);
        userMapper.updateById(followerUser);
    }

    /**
     * 当前用户是否关注
     *
     * @param followerId 关注的用户ID
     */
    @Override
    public boolean isFollow(String followerId) {
        String userId = AuthContextHolder.getUserId();
        long count = this.count(new QueryWrapper<WebFollow>().eq("uid", userId).eq("fid", followerId));
        return count > 0;
    }

    /**
     * 获取当前用户的最新关注信息
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @Override
    public Page<FollowVO> getNoticeFollower(long currentPage, long pageSize) {
        Page<FollowVO> result = new Page<>();
        String userId = AuthContextHolder.getUserId();

        Page<WebFollow> followerPage = this.page(new Page<>((int) currentPage, (int) pageSize), new QueryWrapper<WebFollow>().eq("fid", userId).ne("uid", userId).orderByDesc("create_time"));
        List<WebFollow> followerList = followerPage.getRecords();
        long total = followerPage.getTotal();

        Set<String> uids = followerList.stream().map(WebFollow::getUid).collect(Collectors.toSet());
        Map<String, WebUser> userMap = userMapper.selectBatchIds(uids).stream().collect(Collectors.toMap(WebUser::getId, user -> user));

        // 得到当前用户的所有关注
        List<WebFollow> followers = this.list(new QueryWrapper<WebFollow>().eq("uid", userId));
        Set<String> followerSet = followers.stream().map(WebFollow::getFid).collect(Collectors.toSet());

        List<FollowVO> followVOList = new ArrayList<>();
        followerList.forEach(item -> {
            FollowVO followVo = new FollowVO();
            WebUser user = userMap.get(item.getUid());
            followVo.setUid(String.valueOf(user.getId()))
                    .setUsername(user.getUsername())
                    .setAvatar(user.getAvatar())
                    .setTime(item.getCreateTime().getTime())
                    .setIsFollow(followerSet.contains(item.getUid()));
            followVOList.add(followVo);
        });
        result.setRecords(followVOList);
        result.setTotal(total);
        return result;
    }

    private List<String> parseNoteUrls(String urls, String cover) {
        List<String> imgList = new ArrayList<>();
        if (StringUtils.isNotBlank(urls)) {
            try {
                List<String> parsed = JSONUtil.toList(urls.trim(), String.class);
                if (parsed != null) {
                    for (String item : parsed) {
                        if (StringUtils.isNotBlank(item)) {
                            imgList.add(item);
                        }
                    }
                }
            } catch (Exception e) {
                if (urls.contains(",")) {
                    for (String item : urls.split(",")) {
                        if (StringUtils.isNotBlank(item)) {
                            imgList.add(item.trim());
                        }
                    }
                } else {
                    imgList.add(urls.trim());
                }
            }
        }
        if (imgList.isEmpty() && StringUtils.isNotBlank(cover)) {
            imgList.add(cover);
        }
        return imgList;
    }
}
