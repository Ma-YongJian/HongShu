package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hongshu.common.constant.UserConstant;
import com.hongshu.common.utils.WebUtils;
import com.hongshu.web.auth.AuthContextHolder;
import com.hongshu.web.domain.dto.EsRecordDTO;
import com.hongshu.web.domain.entity.WebSearchRecord;
import com.hongshu.web.domain.vo.RecordSearchVO;
import com.hongshu.web.mapper.WebSearchRecordMapper;
import com.hongshu.web.service.IWebEsRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 搜索记录（开源版走 MySQL，接口路径保持兼容）
 *
 * @Author hongshu
 */
@Service
public class WebEsRecordServiceImpl implements IWebEsRecordService {

    @Autowired
    private WebSearchRecordMapper searchRecordMapper;

    @Override
    public List<RecordSearchVO> getRecordByKeyWord(EsRecordDTO esRecordDTO) {
        String uid = resolveUid(esRecordDTO);
        QueryWrapper<WebSearchRecord> qw = new QueryWrapper<WebSearchRecord>().orderByDesc("update_time").last("limit 20");
        if (StringUtils.isNotBlank(uid)) {
            qw.eq("uid", uid);
        }
        if (esRecordDTO != null && StringUtils.isNotBlank(esRecordDTO.getKeyword())) {
            qw.like("content", esRecordDTO.getKeyword().trim());
        }
        return searchRecordMapper.selectList(qw).stream().map(this::toVo).collect(Collectors.toList());
    }

    @Override
    public List<RecordSearchVO> getHotRecord() {
        List<WebSearchRecord> records = searchRecordMapper.selectList(
                new QueryWrapper<WebSearchRecord>().select("content", "sum(search_count) as search_count")
                        .groupBy("content")
                        .orderByDesc("search_count")
                        .last("limit 10"));
        return records.stream().map(this::toVo).collect(Collectors.toList());
    }

    @Override
    public void addRecord(EsRecordDTO esRecordDTO) {
        if (esRecordDTO == null || StringUtils.isBlank(esRecordDTO.getKeyword())) {
            return;
        }
        String keyword = esRecordDTO.getKeyword().trim();
        String uid = resolveUid(esRecordDTO);
        QueryWrapper<WebSearchRecord> qw = new QueryWrapper<WebSearchRecord>().eq("content", keyword);
        if (StringUtils.isNotBlank(uid)) {
            qw.eq("uid", uid);
        } else {
            qw.isNull("uid");
        }
        WebSearchRecord exist = searchRecordMapper.selectOne(qw.last("limit 1"));
        Date now = new Date();
        if (exist == null) {
            WebSearchRecord record = new WebSearchRecord();
            record.setId(UUID.randomUUID().toString().replace("-", ""));
            record.setUid(uid);
            record.setContent(keyword);
            record.setSearchCount(1L);
            record.setCreateTime(now);
            record.setUpdateTime(now);
            searchRecordMapper.insert(record);
            return;
        }
        exist.setSearchCount((exist.getSearchCount() == null ? 0L : exist.getSearchCount()) + 1);
        exist.setUpdateTime(now);
        searchRecordMapper.updateById(exist);
    }

    @Override
    public void clearRecordByUser(EsRecordDTO esRecordDTO) {
        String uid = resolveUid(esRecordDTO);
        if (StringUtils.isBlank(uid)) {
            return;
        }
        QueryWrapper<WebSearchRecord> qw = new QueryWrapper<WebSearchRecord>().eq("uid", uid);
        if (esRecordDTO != null && StringUtils.isNotBlank(esRecordDTO.getKeyword())) {
            qw.eq("content", esRecordDTO.getKeyword().trim());
        }
        searchRecordMapper.delete(qw);
    }

    @Override
    public void clearAllRecord() {
        searchRecordMapper.delete(new QueryWrapper<>());
    }

    private String resolveUid(EsRecordDTO esRecordDTO) {
        String uid = AuthContextHolder.getUserId();
        if (StringUtils.isNotBlank(uid)) {
            return uid;
        }
        return WebUtils.getRequestHeader(UserConstant.USER_ID);
    }

    private RecordSearchVO toVo(WebSearchRecord record) {
        RecordSearchVO vo = new RecordSearchVO();
        vo.setUid(record.getUid());
        vo.setContent(record.getContent());
        vo.setHighlightContent(record.getContent());
        vo.setSearchCount(record.getSearchCount());
        vo.setTime(record.getUpdateTime() == null ? null : record.getUpdateTime().getTime());
        return vo;
    }
}
