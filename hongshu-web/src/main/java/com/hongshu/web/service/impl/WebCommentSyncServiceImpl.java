package com.hongshu.web.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hongshu.web.domain.entity.WebCommentSync;
import com.hongshu.web.mapper.WebCommentSyncMapper;
import com.hongshu.web.service.IWebCommentSyncService;
import org.springframework.stereotype.Service;

/**
 * @Author hongshu
 */
@Service
public class WebCommentSyncServiceImpl extends ServiceImpl<WebCommentSyncMapper, WebCommentSync> implements IWebCommentSyncService {
}
