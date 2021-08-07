package com.bobo.community.Config;

import com.bobo.community.Util.CommunityConstant;
import com.bobo.community.Util.CommunityUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;


@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/resources/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // 授权
    http.authorizeRequests()
        //登录者权限
        .antMatchers(
            "/user/setting",
            "/user/upload",
            "/discuss/add",
            "/comment/add/**",
            "/message/**",
            "/like/**",
            "/follow/**"
        )
        .hasAnyAuthority(
            AUTHORITY_USER,
            AUTHORITY_ADMIN,
            AUTHORITY_MODERATOR
        )
        //版主权限
        .antMatchers(
            "/discuss/wonderful",
            "/discuss/top"
        )
        .hasAnyAuthority(
            AUTHORITY_MODERATOR
        )
        //管理员权限
        .antMatchers(
            "/data/**",
            "/discuss/delete"
        )
        .hasAnyAuthority(
            AUTHORITY_ADMIN
        )
        .anyRequest().permitAll()
        .and().csrf().disable();
    //权限不够
    http.exceptionHandling()
        .authenticationEntryPoint(new AuthenticationEntryPoint() {
          //没登录
          @Override
          public void commence(HttpServletRequest request,
              HttpServletResponse response, AuthenticationException e)
              throws IOException, ServletException {
            String xRequestedWith = request.getHeader("x-requested-with");
            if ("XMLHttpRequest".equals(xRequestedWith)) {
              response.setContentType("application/plain;charset=utf-8");
              PrintWriter writer = response.getWriter();
              writer.write(CommunityUtil.jsonToString(403, "你还没有登录哦!"));
            } else {
              response.sendRedirect(request.getContextPath() + "/login");
            }
          }
        })
        .accessDeniedHandler(new AccessDeniedHandler() {
          //权限不足
          @Override
          public void handle(HttpServletRequest request,
              HttpServletResponse response, AccessDeniedException e)
              throws IOException, ServletException {
            String xRequestedWith = request.getHeader("x-requested-with");
            if ("XMLHttpRequest".equals(xRequestedWith)) {
              response.setContentType("application/plain;charset=utf-8");
              PrintWriter writer = response.getWriter();
              writer.write(CommunityUtil.jsonToString(403, "你没有访问此功能的权限!"));
            } else {
              response.sendRedirect(request.getContextPath() + "/denied");
            }
          }
        });
    //覆盖logout方法
    http.logout().logoutUrl("/securitylogout");

  }
}