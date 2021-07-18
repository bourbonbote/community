package com.bobo.community.Controller.Interceptor;

import com.bobo.community.Entity.LoginTicket;
import com.bobo.community.Entity.User;
import com.bobo.community.Service.UserService;
import com.bobo.community.Util.CookieUtil;
import com.bobo.community.Util.HostHolder;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

  @Autowired
  CookieUtil cookies;

  @Autowired
  UserService userService;

  @Autowired
  HostHolder hostHolder;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    String ticket = cookies.getValue(request, "ticket");
    if(ticket != null){
      LoginTicket loginTicket = userService.selectLoginTicket(ticket);
      if( loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())){
        User user = userService.findUserById(loginTicket.getUserId());
        hostHolder.setUser(user);
      }
    }
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    User user = hostHolder.getUser();
    if( user != null && modelAndView != null) {
      modelAndView.addObject("loginUser",user);
    }
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
    hostHolder.clear();
  }
}
