package com.bobo.community.Config;

import com.bobo.community.Controller.Interceptor.AlphaInterceptor;
import com.bobo.community.Controller.Interceptor.LoginRequiredInterceptor;
import com.bobo.community.Controller.Interceptor.LoginTicketInterceptor;
import com.bobo.community.Controller.Interceptor.MessageCountInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Autowired
  private AlphaInterceptor alphaInterceptor;

  @Autowired
  private LoginTicketInterceptor loginTicketInterceptor;

  @Autowired
  private LoginRequiredInterceptor loginRequiredInterceptor;

  @Autowired
  private MessageCountInterceptor messageCountInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {

    registry.addInterceptor(loginTicketInterceptor)
        .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

    registry.addInterceptor(loginRequiredInterceptor)
        .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

    registry.addInterceptor(messageCountInterceptor)
        .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png", "/**/*.jpg", "/**/*.jpeg");

  }
}
