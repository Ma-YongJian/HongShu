package com.hongshu.web.websocket.factory;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author: hongshu
 */
public interface OssFactory {

    public String save(MultipartFile file);

    public Boolean delete(String path);
}
