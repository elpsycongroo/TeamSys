package com.chiba.config;


import com.chiba.service.ErrorPageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*****************************************
 *  @author Yuudachi(HanZhumeng)
 *  @date 2018/6/5              
 *  Description: 
 *****************************************/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ErrorPageInterceptor errorPageInterceptor;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:login");
        registry.addViewController("/login").setViewName("login");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(errorPageInterceptor);
    }
}
