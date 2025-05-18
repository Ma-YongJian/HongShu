package com.hongshu.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hongshu.web.domain.dto.BrowseRecordDTO;
import com.hongshu.web.domain.entity.WebNote;
import com.hongshu.web.domain.vo.NoteSearchVO;

import java.util.List;

/**
 * 浏览记录
 *
 * @Author hongshu
 */
public interface IWebBrowseRecordService extends IService<WebNote> {

    /**
     * 获取浏览记录
     */
    List<NoteSearchVO> getAllBrowseRecordByUser(long page, long limit, String uid);

    /**
     * 添加浏览记录
     */
    void addBrowseRecord(BrowseRecordDTO browseRecordDTO);

    /**
     * 删除浏览记录
     */
    void delRecord(String uid, List<String> idList);
}
