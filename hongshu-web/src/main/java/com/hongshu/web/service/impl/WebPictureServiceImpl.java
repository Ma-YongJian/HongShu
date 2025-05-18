package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.enums.EStatus;
import com.hongshu.common.global.MessageConf;
import com.hongshu.common.global.SQLConf;
import com.hongshu.common.global.SysConf;
import com.hongshu.common.utils.ResultUtil;
import com.hongshu.common.utils.StringUtilss;
import com.hongshu.common.utils.WebUtil;
import com.hongshu.web.domain.entity.WebPicture;
import com.hongshu.web.domain.entity.WebPictureSort;
import com.hongshu.web.domain.vo.PictureVO;
import com.hongshu.web.mapper.WebPictureMapper;
import com.hongshu.web.service.IPictureService;
import com.hongshu.web.service.IPictureSortService;
import com.hongshu.web.service.IWebFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 图片表 服务实现类
 *
 * @Author hongshu
 * @since 2018-09-04
 */
@Service
public class WebPictureServiceImpl extends SuperServiceImpl<WebPictureMapper, WebPicture> implements IPictureService {

    @Autowired
    private WebUtil webUtil;

    @Autowired
    private IPictureService pictureService;

//    @Autowired
//    private BlogService blogService;

    @Autowired
    private IPictureSortService pictureSortService;

    @Resource
    private IWebFileService fileService;

//    @Autowired
//    private RabbitTemplate rabbitTemplate;

    @Override
    public IPage<WebPicture> getPageList(PictureVO pictureVO) {
        QueryWrapper<WebPicture> queryWrapper = new QueryWrapper<>();
        if (StringUtilss.isNotEmpty(pictureVO.getKeyword()) && !StringUtilss.isEmpty(pictureVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.PIC_NAME, pictureVO.getKeyword().trim());
        }

        Page<WebPicture> page = new Page<>();
        page.setCurrent(pictureVO.getCurrentPage());
        page.setSize(pictureVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.PICTURE_SORT_UID, pictureVO.getPictureSortUid());
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        IPage<WebPicture> pageList = pictureService.page(page, queryWrapper);
        List<WebPicture> pictureList = pageList.getRecords();

        final StringBuffer fileUids = new StringBuffer();
        pictureList.forEach(item -> {
            if (StringUtilss.isNotEmpty(item.getFileUid())) {
                fileUids.append(item.getFileUid() + SysConf.FILE_SEGMENTATION);
            }
        });

        String pictureResult = null;
        Map<String, String> pictureMap = new HashMap<>();

        if (fileUids != null) {
            pictureResult = this.fileService.getPicture(fileUids.toString(), SysConf.FILE_SEGMENTATION);
        }
        List<Map<String, Object>> picList = webUtil.getPictureMap(pictureResult);

        picList.forEach(item -> {
            pictureMap.put(item.get(SysConf.UID).toString(), item.get(SysConf.URL).toString());
        });

        for (WebPicture item : pictureList) {
            if (StringUtilss.isNotEmpty(item.getFileUid())) {
                item.setPictureUrl(pictureMap.get(item.getFileUid()));
            }
        }
        pageList.setRecords(pictureList);
        return pageList;
    }

    @Override
    public String addPicture(List<PictureVO> pictureVOList) {
        List<WebPicture> pictureList = new ArrayList<>();
        if (pictureVOList.size() > 0) {
            for (PictureVO pictureVO : pictureVOList) {
                WebPicture picture = new WebPicture();
                picture.setFileUid(pictureVO.getFileUid());
                picture.setPictureSortUid(pictureVO.getPictureSortUid());
                picture.setPicName(pictureVO.getPicName());
                picture.setStatus(EStatus.ENABLE);
                pictureList.add(picture);
            }
            pictureService.saveBatch(pictureList);
        } else {
            return ResultUtil.errorWithMessage(MessageConf.INSERT_FAIL);
        }
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editPicture(PictureVO pictureVO) {
        WebPicture picture = pictureService.getById(pictureVO.getUid());
//        // 这里需要更新所有的博客，将图片替换成 裁剪的图片
//        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
//        queryWrapper.eq(SQLConf.FILE_UID, picture.getFileUid());
//        List<Blog> blogList = blogService.list(queryWrapper);
//        if (blogList.size() > 0) {
//            blogList.forEach(item -> {
//                item.setFileUid(pictureVO.getFileUid());
//            });
//            blogService.updateBatchById(blogList);
//
//            Map<String, Object> map = new HashMap<>();
//            map.put(SysConf.COMMAND, SysConf.EDIT_BATCH);
//
//            //发送到RabbitMq
//            rabbitTemplate.convertAndSend(SysConf.EXCHANGE_DIRECT, SysConf.MOGU_BLOG, map);
//        }
        picture.setFileUid(pictureVO.getFileUid());
        picture.setPicName(pictureVO.getPicName());
        picture.setPictureSortUid(pictureVO.getPictureSortUid());
        picture.setUpdateTime(new Date());
        picture.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchPicture(PictureVO pictureVO) {
        // 参数校验
        // 图片删除的时候，是携带多个id拼接而成的
        String uidStr = pictureVO.getUid();
        if (StringUtilss.isEmpty(uidStr)) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uids = StringUtilss.changeStringToString(pictureVO.getUid(), SysConf.FILE_SEGMENTATION);
        for (String item : uids) {
            WebPicture picture = pictureService.getById(item);
            picture.setStatus(EStatus.DISABLED);
            picture.setUpdateTime(new Date());
            picture.updateById();
        }
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String setPictureCover(PictureVO pictureVO) {
        WebPictureSort pictureSort = pictureSortService.getById(pictureVO.getPictureSortUid());
        if (pictureSort != null) {
            WebPicture picture = pictureService.getById(pictureVO.getUid());
            if (picture != null) {
                pictureSort.setFileUid(picture.getFileUid());
                picture.setUpdateTime(new Date());
                pictureSort.updateById();
            } else {
                return ResultUtil.errorWithMessage(MessageConf.THE_PICTURE_NOT_EXIST);
            }
        } else {
            return ResultUtil.errorWithMessage(MessageConf.THE_PICTURE_SORT_NOT_EXIST);
        }
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public WebPicture getTopOne() {
        QueryWrapper<WebPicture> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByAsc(SQLConf.CREATE_TIME);
        queryWrapper.last(SysConf.LIMIT_ONE);
        WebPicture picture = pictureService.getOne(queryWrapper);
        return picture;
    }
}
