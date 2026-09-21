package com.hongshu.framework.config;

import com.hongshu.common.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 将 C 端 JWT 密钥从配置注入 JwtUtils
 */
@Configuration
public class JwtAppSecretConfig {

    @Value("${hongshu.jwt.app-secret:ukc8BDbRigUDaY6pZFfWus2jZWLPHO}")
    private String appSecret;

    @PostConstruct
    public void init() {
        JwtUtils.setAppSecret(appSecret);
    }
}
