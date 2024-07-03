package com.hongshu.web.service.sys;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hongshu.web.domain.entity.WebPictureSort;
import com.hongshu.web.domain.vo.PictureSortVO;

/**
 * 图片分类表 服务类
 *
 * @author: hongshu
 * @date 2018-09-04
 */
public interface IPictureSortService extends SuperService<WebPictureSort> {

    /**
     * 获取图片分类列表
     *
     * @param pictureSortVO
     * @return
     */
    public IPage<WebPictureSort> getPageList(PictureSortVO pictureSortVO);

    /**
     * 新增图片分类
     *
     * @param pictureSortVO
     */
    public String addPictureSort(PictureSortVO pictureSortVO);

    /**
     * 编辑图片分类
     *
     * @param pictureSortVO
     */
    public String editPictureSort(PictureSortVO pictureSortVO);

    /**
     * 删除图片分类
     *
     * @param pictureSortVO
     */
    public String deletePictureSort(PictureSortVO pictureSortVO);

    /**
     * 置顶图片分类
     *
     * @param pictureSortVO
     */
    public String stickPictureSort(PictureSortVO pictureSortVO);
}
