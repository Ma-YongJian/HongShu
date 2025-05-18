package com.hongshu.web.service;

import com.hongshu.common.core.domain.Query;
import com.hongshu.web.domain.entity.WebNote;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 笔记信息 服务层
 *
 * @Author hongshu
 */
public interface ISysNoteService {

    /**
     * 查询笔记信息集合
     */
    List<WebNote> getAllNoteList();

    /**
     * 查询笔记信息集合
     *
     * @param query 笔记信息
     */
    List<WebNote> selectNoteList(Query query);

    /**
     * 获取未审核笔记列表
     */
    List<WebNote> selectUnAuditNoteList(Query query);

    /**
     * 通过笔记ID查询笔记信息
     *
     * @param id 笔记ID
     */
    WebNote selectNoteById(Long id);

    /**
     * 新增保存笔记信息
     *
     * @param note 笔记信息
     */
    int insertNote(WebNote note, MultipartFile file);

    /**
     * 修改保存笔记信息
     *
     * @param note 笔记信息
     */
    int updateNote(WebNote note, MultipartFile file);

    /**
     * 批量删除笔记信息
     *
     * @param ids 需要删除的笔记ID
     */
    int deleteNoteByIds(Long[] ids);

    Object getNoteCount(int status);

    Map<String, Object> getNoteContributeCount();

    List<Map<String, Object>> getNoteCountByCategory();

    /**
     * 审核管理
     *
     * @param noteId    笔记ID
     * @param auditType 审核状态
     */
    boolean auditNote(String noteId, String auditType);
}
