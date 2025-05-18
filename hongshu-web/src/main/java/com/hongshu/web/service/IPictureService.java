package com.hongshu.web.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hongshu.web.domain.entity.WebPicture;
import com.hongshu.web.domain.vo.PictureVO;

import java.util.List;

/**
 * 图片表 服务类
 *
 * @Author hongshu
 */
public interface IPictureService extends SuperService<WebPicture> {

    /**
     * 获取图片列表
     *
     * @param pictureVO
     * @return
     */
    IPage<WebPicture> getPageList(PictureVO pictureVO);

    /**
     * 新增图片
     *
     * @param pictureVOList
     * @return
     */
    String addPicture(List<PictureVO> pictureVOList);

    /**
     * 编辑图片
     *
     * @param pictureVO
     * @return
     */
    String editPicture(PictureVO pictureVO);

    /**
     * 批量删除图片
     *
     * @param pictureVO
     */
    String deleteBatchPicture(PictureVO pictureVO);

    /**
     * 设置图片封面
     *
     * @param pictureVO
     */
    String setPictureCover(PictureVO pictureVO);

    /**
     * 获取最新图片,按时间排序
     *
     * @return
     */
    WebPicture getTopOne();
}
