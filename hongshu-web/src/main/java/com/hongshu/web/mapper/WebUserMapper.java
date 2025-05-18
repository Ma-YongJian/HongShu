package com.hongshu.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hongshu.web.domain.entity.WebUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author hongshu
 */
@Mapper
public interface WebUserMapper extends BaseMapper<WebUser> {

    /**
     * 根据条件分页查询角色数据
     *
     * @param user 会员
     * @return 角色数据集合信息
     */
    List<WebUser> getUserList(WebUser user);
}
