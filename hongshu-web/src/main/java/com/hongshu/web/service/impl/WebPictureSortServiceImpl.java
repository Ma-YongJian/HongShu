package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hongshu.common.enums.EStatus;
import com.hongshu.common.global.MessageConf;
import com.hongshu.common.global.SQLConf;
import com.hongshu.common.global.SysConf;
import com.hongshu.common.utils.ResultUtil;
import com.hongshu.common.utils.StringUtils;
import com.hongshu.common.utils.StringUtilss;
import com.hongshu.common.utils.WebUtil;
import com.hongshu.web.domain.entity.WebPicture;
import com.hongshu.web.domain.entity.WebPictureSort;
import com.hongshu.web.domain.vo.PictureSortVO;
import com.hongshu.web.mapper.WebPictureSortMapper;
import com.hongshu.web.service.IPictureService;
import com.hongshu.web.service.IPictureSortService;
import com.hongshu.web.service.IWebFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 图片分类表 服务实现类
 *
 * @Author hongshu
 */
@Service
public class WebPictureSortServiceImpl extends SuperServiceImpl<WebPictureSortMapper, WebPictureSort> implements IPictureSortService {

    @Autowired
    private WebUtil webUtil;
    @Autowired
    private IPictureSortService pictureSortService;
    @Autowired
    private IPictureService pictureService;
    @Resource
    private IWebFileService fileService;

    @Override
    public IPage<WebPictureSort> getPageList(PictureSortVO pictureSortVO) {
        QueryWrapper<WebPictureSort> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(pictureSortVO.getKeyword()) && !StringUtils.isEmpty(pictureSortVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.NAME, pictureSortVO.getKeyword().trim());
        }

        if (pictureSortVO.getIsShow() != null) {
            queryWrapper.eq(SQLConf.IS_SHOW, SysConf.ONE);
        }
        Page<WebPictureSort> page = new Page<>();
        page.setCurrent(pictureSortVO.getCurrentPage());
        page.setSize(pictureSortVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.SORT);
        IPage<WebPictureSort> pageList = pictureSortService.page(page, queryWrapper);
        List<WebPictureSort> list = pageList.getRecords();

        final StringBuffer fileUids = new StringBuffer();
        list.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getFileUid())) {
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

        for (WebPictureSort item : list) {
            //获取图片
            if (StringUtils.isNotEmpty(item.getFileUid())) {
                List<String> pictureUidsTemp = StringUtilss.changeStringToString(item.getFileUid(), SysConf.FILE_SEGMENTATION);
                List<String> pictureListTemp = new ArrayList<>();
                pictureUidsTemp.forEach(picture -> {
                    pictureListTemp.add(pictureMap.get(picture));
                });
                item.setPhotoList(pictureListTemp);
            }
        }
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String addPictureSort(PictureSortVO pictureSortVO) {
        WebPictureSort pictureSort = new WebPictureSort();
        pictureSort.setName(pictureSortVO.getName());
        pictureSort.setParentUid(pictureSortVO.getParentUid());
        pictureSort.setSort(pictureSortVO.getSort());
        pictureSort.setFileUid(pictureSortVO.getFileUid());
        pictureSort.setStatus(EStatus.ENABLE);
        pictureSort.setIsShow(pictureSortVO.getIsShow());
        pictureSort.setUpdateTime(new Date());
        pictureSort.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editPictureSort(PictureSortVO pictureSortVO) {
        WebPictureSort pictureSort = pictureSortService.getById(pictureSortVO.getUid());
        pictureSort.setName(pictureSortVO.getName());
        pictureSort.setParentUid(pictureSortVO.getParentUid());
        pictureSort.setSort(pictureSortVO.getSort());
        pictureSort.setFileUid(pictureSortVO.getFileUid());
        pictureSort.setIsShow(pictureSortVO.getIsShow());
        pictureSort.setUpdateTime(new Date());
        pictureSort.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deletePictureSort(PictureSortVO pictureSortVO) {
        // 判断要删除的分类，是否有图片
        QueryWrapper<WebPicture> pictureQueryWrapper = new QueryWrapper<>();
        pictureQueryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        pictureQueryWrapper.eq(SQLConf.PICTURE_SORT_UID, pictureSortVO.getUid());
        Integer pictureCount = Math.toIntExact(pictureService.count(pictureQueryWrapper));
        if (pictureCount > 0) {
            return ResultUtil.errorWithMessage(MessageConf.PICTURE_UNDER_THIS_SORT);
        }

        WebPictureSort pictureSort = pictureSortService.getById(pictureSortVO.getUid());
        pictureSort.setStatus(EStatus.DISABLED);
        pictureSort.setUpdateTime(new Date());
        pictureSort.updateById();
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    @Override
    public String stickPictureSort(PictureSortVO pictureSortVO) {
        WebPictureSort pictureSort = pictureSortService.getById(pictureSortVO.getUid());
        //查找出最大的那一个
        QueryWrapper<WebPictureSort> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(SQLConf.SORT);
        Page<WebPictureSort> page = new Page<>();
        page.setCurrent(0);
        page.setSize(1);
        IPage<WebPictureSort> pageList = pictureSortService.page(page, queryWrapper);
        List<WebPictureSort> list = pageList.getRecords();
        WebPictureSort maxSort = list.get(0);
        if (StringUtils.isEmpty(maxSort.getUid())) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        if (maxSort.getUid().equals(pictureSort.getUid())) {
            return ResultUtil.errorWithMessage(MessageConf.THIS_SORT_IS_TOP);
        }
        Integer sortCount = maxSort.getSort() + 1;
        pictureSort.setSort(sortCount);
        pictureSort.setUpdateTime(new Date());
        pictureSort.updateById();
        return ResultUtil.successWithMessage(MessageConf.OPERATION_SUCCESS);
    }
}
