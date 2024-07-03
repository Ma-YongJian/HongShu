package com.hongshu.web.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hongshu.web.domain.entity.WebTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签信息 数据层
 *
 * @author hongshu
 */
@Mapper
public interface SysTagMapper extends BaseMapper<WebTag> {

}
