package com.wsk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author sk
 * @date 2022/5/1
 * @description 描述
 */
@Configuration
public class MyWebAppConfigurer implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/image/**", "/images/**", "/css/**", "/js/**", "/img/**", "/toImage/**","/new/**","/plugins/**","/fonts/**")
                .addResourceLocations("classpath:/mystatic/image/", "classpath:/mystatic/images/", "classpath:/mystatic/css/", "classpath:/mystatic/js/", "classpath:/mystatic/plugins/", "classpath:/mystatic/fonts/","classpath:/mystatic/img/",
                        "file:D:/toImage/", "classpath:/templates/new/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
