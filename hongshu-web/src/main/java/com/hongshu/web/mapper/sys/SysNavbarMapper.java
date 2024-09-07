package com.hongshu.web.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hongshu.web.domain.entity.WebCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 导航栏表 数据层
 *
 * @author: hongshu
 */
@Mapper
public interface SysNavbarMapper extends BaseMapper<WebCategory> {


    @Select("select count(1) from web_category where pid = #{id}")
    int hasChildById(Long id);

    @Select("select * from web_category where id = #{id}")
    WebCategory selectByCpid(String cpid);

    @Select("select * from web_category where id = #{id}")
    WebCategory selectByPid(String cid);
}
