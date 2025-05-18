package com.hongshu.web.websocket.factory;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author hongshu
 */
public interface OssFactory {

    String save(MultipartFile file);

    Boolean delete(String path);
}
