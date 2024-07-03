package com.hongshu.web.service;

import com.hongshu.web.domain.vo.RecordSearchVo;

import java.util.List;

/**
 * ES
 *
 * @author: hongshu
 */
public interface IWebEsRecordService {

    /**
     * 获取搜索记录
     *
     * @param keyword 关键词
     */
    List<RecordSearchVo> getRecordByKeyWord(String keyword);

    /**
     * 热门关键词
     */
    List<RecordSearchVo> getHotRecord();

    /**
     * 增加搜索记录
     *
     * @param keyword 关键词
     */
    void addRecord(String keyword);
}
