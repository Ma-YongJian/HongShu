package com.hongshu.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hongshu.web.domain.entity.WebNavbar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 导航栏表 数据层
 *
 * @Author hongshu
 */
@Mapper
public interface SysNavbarMapper extends BaseMapper<WebNavbar> {

    @Select("select count(1) from web_navbar where pid = #{id}")
    int hasChildById(Long id);

    @Select("select * from web_navbar where id = #{id}")
    WebNavbar selectByCpid(String cpid);

    @Select("select * from web_navbar where id = #{id}")
    WebNavbar selectByPid(String cid);
}
