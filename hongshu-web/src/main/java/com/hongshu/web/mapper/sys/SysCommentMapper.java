package com.hongshu.web.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hongshu.web.domain.entity.WebComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论 数据层
 *
 * @author hongshu
 */
@Mapper
public interface SysCommentMapper extends BaseMapper<WebComment> {

}
