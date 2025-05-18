package com.hongshu.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hongshu.web.domain.entity.WebNote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 笔记信息 数据层
 *
 * @Author hongshu
 */
@Mapper
public interface SysNoteMapper extends BaseMapper<WebNote> {

    /**
     * 获取一年内的笔记贡献数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    @Select("SELECT DISTINCT DATE_FORMAT(create_time, '%Y-%m-%d') DATE, COUNT(uid) COUNT FROM web_note WHERE 1=1 && status = 1 && create_time >= #{startTime} && create_time < #{endTime} GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d')")
    List<Map<String, Object>> getNoteContributeCount(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 通过标签获取博客数量
     *
     * @return
     */
    @Select("SELECT cpid, COUNT(cpid) as count FROM  web_note where status = 1 GROUP BY cpid")
    List<Map<String, Object>> getNoteCountByCategory();
}
