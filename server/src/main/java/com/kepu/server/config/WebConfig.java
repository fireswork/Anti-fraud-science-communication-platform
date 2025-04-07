package com.kepu.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
    
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(List.of(
                MediaType.APPLICATION_JSON,
                MediaType.TEXT_PLAIN,
                MediaType.ALL
        ));
        converters.add(0, converter);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 获取应用根目录
        String rootPath = System.getProperty("user.dir");
        File uploadDir = new File(rootPath, "uploads");
        
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
            System.out.println("创建uploads目录: " + uploadDir.getAbsolutePath());
        }
        
        // 添加资源处理器，使uploads目录中的文件可通过/uploads/**访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir.getAbsolutePath() + "/");
                
        System.out.println("已添加静态资源映射: /uploads/** -> " + uploadDir.getAbsolutePath());
    }
} 