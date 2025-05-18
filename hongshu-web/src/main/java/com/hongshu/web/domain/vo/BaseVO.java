package com.hongshu.web.domain.vo;

import com.hongshu.common.annotation.IdValid;
import com.hongshu.common.validator.group.Delete;
import com.hongshu.common.validator.group.Update;
import com.hongshu.web.domain.PageInfo;
import lombok.Data;

/**
 * BaseVO   view object 表现层 基类对象
 *
 * @Author hongshu
 * @create: 2019-12-03-22:38
 */
@Data
public class BaseVO<T> extends PageInfo<T> {

    /**
     * 唯一UID
     */
    @IdValid(groups = {Update.class, Delete.class})
    private String uid;

    private Integer status;
}
