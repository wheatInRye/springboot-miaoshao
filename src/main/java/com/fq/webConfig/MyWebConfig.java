package com.fq.webConfig;

import com.fq.access.AccessInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Auther: 冯庆
 * @Date: 2018/8/8 18:47
 * @Description:
 */
@Configuration
public class MyWebConfig implements WebMvcConfigurer {

    @Autowired
     UserArgumentResolvers userArgumentResolvers;
    @Autowired
    AccessInterceptor accessInterceptor;

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolvers);
    }
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor);
    }
}
