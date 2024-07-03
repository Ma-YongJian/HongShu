package com.hongshu.web.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.web.auth.AuthContextHolder;
import com.hongshu.web.domain.entity.WebFollower;
import com.hongshu.web.domain.entity.WebLikeOrCollection;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.FollowerVo;
import com.hongshu.web.domain.vo.TrendVo;
import com.hongshu.web.mapper.WebFollowerMapper;
import com.hongshu.web.mapper.WebLikeOrCollectionMapper;
import com.hongshu.web.mapper.WebNoteMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.IWebFollowerService;
import com.hongshu.web.websocket.im.ChatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: hongshu
 */
@Service
public class WebFollowerServiceImpl extends ServiceImpl<WebFollowerMapper, WebFollower> implements IWebFollowerService {

    @Autowired
    WebNoteMapper noteMapper;
    @Autowired
    WebUserMapper userMapper;
    @Autowired
    WebLikeOrCollectionMapper likeOrCollectionMapper;
    @Autowired
    ChatUtils chatUtils;


    /**
     * 获取关注用户的所有动态
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @Override
    public Page<TrendVo> getFollowTrend(long currentPage, long pageSize) {
        Page<TrendVo> page = new Page<>();
        // 得到当前用户所有关注的用户
        String currentUid = AuthContextHolder.getUserId();
        List<WebFollower> followers = this.list(new QueryWrapper<WebFollower>().eq("uid", currentUid));
        List<String> fids = followers.stream().map(WebFollower::getFid).collect(Collectors.toList());
        fids.add(currentUid);
        Page<WebNote> notePage = noteMapper.selectPage(new Page<>((int) currentPage, (int) pageSize),
                new QueryWrapper<WebNote>()
                        .like("audit_status", 1)
                        .in("uid", fids)
                        .orderByDesc("update_time"));
        List<WebNote> notes = notePage.getRecords();
        List<TrendVo> trendVos = new ArrayList<>();
        if (!notes.isEmpty()) {
            // 得到所有用户的图片
            List<String> ids = notes.stream().map(WebNote::getUid).collect(Collectors.toList());
            List<WebUser> users = userMapper.selectBatchIds(ids);
            HashMap<String, WebUser> userMap = new HashMap<>();
            users.forEach(item -> userMap.put(String.valueOf(item.getId()), item));
            // 是否点赞
            List<WebLikeOrCollection> likeOrCollections = likeOrCollectionMapper.selectList(new QueryWrapper<WebLikeOrCollection>().eq("uid", currentUid).eq("type", 1));
            List<String> likeOrCollectionIds = likeOrCollections.stream().map(WebLikeOrCollection::getLikeOrCollectionId).collect(Collectors.toList());
            for (WebNote note : notes) {
                TrendVo trendVo = new TrendVo();
                WebUser user = userMap.get(note.getUid());
                trendVo.setUid(String.valueOf(user.getId()))
                        .setUsername(user.getUsername())
                        .setAvatar(user.getAvatar())
                        .setNid(String.valueOf(note.getId()))
                        .setTime(note.getUpdateTime().getTime())
                        .setContent(note.getContent())
                        .setCommentCount(note.getCommentCount())
                        .setLikeCount(note.getLikeCount())
                        .setIsLike(likeOrCollectionIds.contains(note.getId()))
                        .setIsLoading(false);
                String urls = note.getUrls();
                List<String> imgList = JSONUtil.toList(urls, String.class);
                if (imgList.size() > 4) {
                    List<String> subList = imgList.subList(0, 4);
                    trendVo.setImgUrls(subList);
                } else {
                    trendVo.setImgUrls(imgList);
                }
                trendVos.add(trendVo);
            }
        }
        long total = notePage.getTotal();
        page.setTotal(total);
        page.setRecords(trendVos);
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
    public Page<FollowerVo> getFriend(long currentPage, long pageSize, Integer type) {
        return null;
    }

    /**
     * 关注用户
     *
     * @param followerId 关注用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void followById(String followerId) {
        WebFollower follower = new WebFollower();
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
            this.remove(new QueryWrapper<WebFollower>().eq("uid", userId).eq("fid", followerId));
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
        long count = this.count(new QueryWrapper<WebFollower>().eq("uid", userId).eq("fid", followerId));
        return count > 0;
    }

    /**
     * 获取当前用户的最新关注信息
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    @Override
    public Page<FollowerVo> getNoticeFollower(long currentPage, long pageSize) {
        Page<FollowerVo> result = new Page<>();
        String userId = AuthContextHolder.getUserId();

        Page<WebFollower> followerPage = this.page(new Page<>((int) currentPage, (int) pageSize), new QueryWrapper<WebFollower>().eq("fid", userId).ne("uid", userId).orderByDesc("create_time"));
        List<WebFollower> followerList = followerPage.getRecords();
        long total = followerPage.getTotal();

        Set<String> uids = followerList.stream().map(WebFollower::getUid).collect(Collectors.toSet());
        Map<String, WebUser> userMap = userMapper.selectBatchIds(uids).stream().collect(Collectors.toMap(WebUser::getId, user -> user));

        // 得到当前用户的所有关注
        List<WebFollower> followers = this.list(new QueryWrapper<WebFollower>().eq("uid", userId));
        Set<String> followerSet = followers.stream().map(WebFollower::getFid).collect(Collectors.toSet());

        List<FollowerVo> followerVoList = new ArrayList<>();
        followerList.forEach(item -> {
            FollowerVo followerVo = new FollowerVo();
            WebUser user = userMap.get(item.getUid());
            followerVo.setUid(String.valueOf(user.getId()))
                    .setUsername(user.getUsername())
                    .setAvatar(user.getAvatar())
                    .setTime(item.getCreateTime().getTime())
                    .setIsFollow(followerSet.contains(item.getUid()));
            followerVoList.add(followerVo);
        });
        result.setRecords(followerVoList);
        result.setTotal(total);
        return result;
    }
}
