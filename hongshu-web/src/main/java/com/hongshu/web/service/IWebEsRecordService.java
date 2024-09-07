package com.hongshu.web.service;

import com.hongshu.web.domain.dto.EsRecordDTO;
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
     */
    List<RecordSearchVo> getRecordByKeyWord(EsRecordDTO esRecordDTO);

    /**
     * 热门关键词
     */
    List<RecordSearchVo> getHotRecord();

    /**
     * 增加搜索记录
     */
    void addRecord(EsRecordDTO esRecordDTO);

    /**
     * 删除搜索记录
     */
    void clearRecordByUser(EsRecordDTO esRecordDTO);

    /**
     * 清空搜索记录
     */
    void clearAllRecord();
}
