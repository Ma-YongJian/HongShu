package com.hongshu.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hongshu.web.domain.entity.WebComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: hongshu
 */
@Mapper
public interface WebCommentMapper extends BaseMapper<WebComment> {
}