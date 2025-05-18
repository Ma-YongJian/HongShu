package com.hongshu.web.auth;

import com.hongshu.common.config.HongshuConfig;
import com.hongshu.common.constant.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author: hongshu
 */
@Configuration
public class LoginMvcConfigureAdapter extends WebMvcConfigurationSupport {

    /**
     * 拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLoginInterceptor())
                .addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    /**
     * 静态资源访问
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler(UploadFileConstant.OSS + "/**") //虚拟url路径
//                .addResourceLocations("file:" + UploadFileConstant.ADDRESS); //真实本地路径
//        super.addResourceHandlers(registry);

        /** 本地文件上传路径 */
        registry.addResourceHandler(Constants.RESOURCE_PREFIX + "/**")
                .addResourceLocations("file:" + HongshuConfig.getProfile() + "/");
    }
}
