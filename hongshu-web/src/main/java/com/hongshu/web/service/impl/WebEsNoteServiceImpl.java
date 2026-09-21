package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.common.constant.UserConstant;
import com.hongshu.common.utils.ConvertUtils;
import com.hongshu.common.utils.WebUtils;
import com.hongshu.web.auth.AuthContextHolder;
import com.hongshu.web.domain.dto.EsNoteDTO;
import com.hongshu.web.domain.entity.WebLikeOrCollect;
import com.hongshu.web.domain.entity.WebNavbar;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.entity.WebUser;
import com.hongshu.web.domain.vo.NoteSearchVO;
import com.hongshu.web.mapper.WebLikeOrCollectMapper;
import com.hongshu.web.mapper.WebNavbarMapper;
import com.hongshu.web.mapper.WebNoteMapper;
import com.hongshu.web.mapper.WebUserMapper;
import com.hongshu.web.service.IWebEsNoteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 笔记搜索（开源版走 MySQL，接口路径保持兼容）
 *
 * @Author hongshu
 */
@Service
@Slf4j
public class WebEsNoteServiceImpl extends ServiceImpl<WebNoteMapper, WebNote> implements IWebEsNoteService {

    @Autowired
    private WebUserMapper userMapper;
    @Autowired
    private WebNavbarMapper categoryMapper;
    @Autowired
    private WebNoteMapper noteMapper;
    @Autowired
    private WebLikeOrCollectMapper likeOrCollectionMapper;

    @Override
    public Page<NoteSearchVO> getNoteByDTO(long currentPage, long pageSize, EsNoteDTO esNoteDTO) {
        QueryWrapper<WebNote> qw = approvedNote();
        if (esNoteDTO != null) {
            if (StringUtils.isNotBlank(esNoteDTO.getKeyword())) {
                String keyword = esNoteDTO.getKeyword().trim();
                qw.and(w -> w.like("title", keyword).or().like("content", keyword).or().like("author", keyword));
            }
            String cid = esNoteDTO.getCid();
            String cpid = esNoteDTO.getCpid();
            if (StringUtils.isNotBlank(cid) && StringUtils.isNotBlank(cpid)) {
                qw.and(w -> w.eq("cid", cid).or().eq("cpid", cpid).or().eq("cid", cpid).or().eq("cpid", cid));
            } else if (StringUtils.isNotBlank(cid)) {
                qw.and(w -> w.eq("cid", cid).or().eq("cpid", cid));
            } else if (StringUtils.isNotBlank(cpid)) {
                qw.and(w -> w.eq("cpid", cpid).or().eq("cid", cpid));
            }
            Integer type = esNoteDTO.getType();
            if (type != null && type == 1) {
                qw.orderByDesc("like_count");
            } else {
                qw.orderByDesc("time").orderByDesc("update_time");
            }
        } else {
            qw.orderByDesc("time").orderByDesc("update_time");
        }
        return toSearchPage(currentPage, pageSize, qw);
    }

    @Override
    public List<WebNavbar> getCategoryAgg(EsNoteDTO esNoteDTO) {
        QueryWrapper<WebNavbar> qw = new QueryWrapper<>();
        if (esNoteDTO != null && StringUtils.isNotBlank(esNoteDTO.getKeyword())) {
            qw.like("title", esNoteDTO.getKeyword());
        }
        return categoryMapper.selectList(qw);
    }

    @Override
    public Page<NoteSearchVO> getRecommendNote(long currentPage, long pageSize) {
        QueryWrapper<WebNote> qw = approvedNote().orderByDesc("time").orderByDesc("update_time");
        return toSearchPage(currentPage, pageSize, qw);
    }

    @Override
    public Page<WebUser> getRecommendUser(long currentPage, long pageSize) {
        QueryWrapper<WebUser> qw = new QueryWrapper<WebUser>().orderByDesc("update_time");
        String currentUid = AuthContextHolder.getUserId();
        if (StringUtils.isNotBlank(currentUid)) {
            qw.ne("id", currentUid);
        }
        Page<WebUser> page = userMapper.selectPage(new Page<>(currentPage, pageSize), qw);
        if (page.getRecords() != null) {
            page.getRecords().forEach(user -> user.setPassword(null));
        }
        return page;
    }

    @Override
    public Page<NoteSearchVO> getHotNote(long currentPage, long pageSize) {
        QueryWrapper<WebNote> qw = approvedNote().orderByDesc("like_count").orderByDesc("view_count");
        return toSearchPage(currentPage, pageSize, qw);
    }

    @Override
    public void addNote(NoteSearchVO noteSearchVo) {
        // 开源版直接读 MySQL，不再写搜索引擎
    }

    @Override
    public void updateNote(NoteSearchVO noteSearchVo) {
        // 开源版直接读 MySQL，不再写搜索引擎
    }

    @Override
    public void deleteNote(String noteId) {
        // 开源版直接读 MySQL，不再写搜索引擎
    }

    @Override
    public void addNoteBulkData() {
        // 开源版直接读 MySQL，不再写搜索引擎
    }

    @Override
    public void delNoteBulkData() {
        // 开源版直接读 MySQL，不再写搜索引擎
    }

    @Override
    public void refreshNoteData() {
        // 开源版直接读 MySQL，不再写搜索引擎
    }

    /**
     * 开源版只展示已审核通过的图文。
     * 存量数据 note_type=1，新发布为 0；视频为 2，这里排除视频即可。
     */
    private QueryWrapper<WebNote> approvedNote() {
        return new QueryWrapper<WebNote>()
                .eq("audit_status", "1")
                .and(w -> w.ne("note_type", "2").or().isNull("note_type"));
    }

    private Page<NoteSearchVO> toSearchPage(long currentPage, long pageSize, QueryWrapper<WebNote> qw) {
        Page<WebNote> notePage = noteMapper.selectPage(new Page<>(currentPage, pageSize), qw);
        Page<NoteSearchVO> page = new Page<>(currentPage, pageSize);
        page.setTotal(notePage.getTotal());
        page.setRecords(toSearchVo(notePage.getRecords()));
        return page;
    }

    private List<NoteSearchVO> toSearchVo(List<WebNote> notes) {
        if (notes == null || notes.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> uids = notes.stream().map(WebNote::getUid).collect(Collectors.toSet());
        Map<String, WebUser> userMap = userMapper.selectBatchIds(uids).stream()
                .collect(Collectors.toMap(WebUser::getId, u -> u, (a, b) -> a));
        String currentUid = WebUtils.getRequestHeader(UserConstant.USER_ID);
        Set<String> likedIds = Collections.emptySet();
        if (StringUtils.isNotBlank(currentUid)) {
            likedIds = likeOrCollectionMapper.selectList(new QueryWrapper<WebLikeOrCollect>()
                            .eq("uid", currentUid).eq("type", 1))
                    .stream().map(WebLikeOrCollect::getLikeOrCollectionId).collect(Collectors.toSet());
        }
        Set<String> liked = likedIds;
        return notes.stream().map(note -> {
            NoteSearchVO vo = ConvertUtils.sourceToTarget(note, NoteSearchVO.class);
            WebUser user = userMap.get(note.getUid());
            if (user != null) {
                vo.setUsername(user.getUsername());
                vo.setAvatar(user.getAvatar());
            }
            vo.setIsLike(liked.contains(note.getId()));
            vo.setIsLoading(true);
            return vo;
        }).collect(Collectors.toList());
    }
}
