package com.kubsu.project.config;

import com.kubsu.project.utils.RedirectInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${errors.path}")
    private String errorsPath;

    @Value("${warnings.path}")
    private String warningsPath;

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/excel/**")
                .addResourceLocations("file://" + uploadPath + "/");
        registry.addResourceHandler("/file_with_errors_info/**")
                .addResourceLocations("file://" + errorsPath + "/");
        registry.addResourceHandler("/files_with_warnings_info/**")
                .addResourceLocations("file://" + warningsPath + "/");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RedirectInterceptor());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.configureMessageConverters(converters);
        converters.add(new FormHttpMessageConverter());
    }
}
