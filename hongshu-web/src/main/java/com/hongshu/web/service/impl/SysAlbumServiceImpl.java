package com.hongshu.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hongshu.common.core.domain.Query;
import com.hongshu.common.validator.ValidatorUtil;
import com.hongshu.web.domain.entity.WebAlbum;
import com.hongshu.web.mapper.SysAlbumMapper;
import com.hongshu.web.service.ISysAlbumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 会员信息 服务层处理
 *
 * @Author hongshu
 */
@Slf4j
@Service
public class SysAlbumServiceImpl implements ISysAlbumService {

    @Autowired
    private SysAlbumMapper albumMapper;


    /**
     * 查询会员信息集合
     *
     * @param query 会员信息
     */
    @Override
    public List<WebAlbum> getAlbumList(Query query) {
        QueryWrapper<WebAlbum> qw = new QueryWrapper<>();
        qw.lambda().like(ValidatorUtil.isNotNull(query.getTitle()), WebAlbum::getTitle, query.getTitle());
        qw.lambda().ge(ValidatorUtil.isNotNull(query.get("params[beginTime]")), WebAlbum::getCreateTime, query.get("params[beginTime]"));
        qw.lambda().le(ValidatorUtil.isNotNull(query.get("params[endTime]")), WebAlbum::getCreateTime, query.get("params[endTime]"));
        qw.orderByDesc("create_time");
        return albumMapper.selectList(qw);
    }

    /**
     * 查询所有会员
     */
    @Override
    public List<WebAlbum> selectAlbumAll() {
        return albumMapper.selectList(new QueryWrapper<>());
    }

    /**
     * 通过会员ID查询会员信息
     *
     * @param id 会员ID
     */
    @Override
    public WebAlbum getAlbumById(Long id) {
        return albumMapper.selectById(id);
    }

    /**
     * 新增会员信息
     *
     * @param album 会员信息
     */
    @Override
    public int insertAlbum(WebAlbum album) {
        album.setTitle(album.getTitle());
        album.setSort(album.getSort());
        album.setCreateTime(new Date());
        album.setUpdateTime(new Date());
        return albumMapper.insert(album);
    }

    /**
     * 修改保存会员信息
     *
     * @param album 会员信息
     */
    @Override
    public int updateAlbum(WebAlbum album) {
        WebAlbum webAlbum = albumMapper.selectById(album.getId());
        webAlbum.setTitle(album.getTitle());
        webAlbum.setSort(album.getSort());
        webAlbum.setUpdater("System");
        webAlbum.setUpdateTime(new Date());
        return albumMapper.updateById(webAlbum);
    }

    /**
     * 删除会员信息
     *
     * @param id 会员ID
     */
    @Override
    public int deleteAlbumById(Long id) {
        return albumMapper.deleteById(id);
    }

    /**
     * 批量删除会员信息
     *
     * @param ids 需要删除的会员ID
     */
    @Override
    public int deleteAlbumByIds(Long[] ids) {
        List<Long> longs = Arrays.asList(ids);
        for (Long id : ids) {
            WebAlbum album = getAlbumById(id);
            if (ValidatorUtil.isNull(album)) {
                log.info("用户不存在:{}", id);
                longs.remove(id);
            }
        }
        return albumMapper.deleteBatchIds(longs);
    }
}
