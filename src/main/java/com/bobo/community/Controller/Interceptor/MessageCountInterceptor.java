package com.bobo.community.Controller.Interceptor;

import com.bobo.community.Entity.User;
import com.bobo.community.Service.MessageService;
import com.bobo.community.Util.HostHolder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class MessageCountInterceptor implements HandlerInterceptor {
  @Autowired
  MessageService messageService;

  @Autowired
  HostHolder hostHolder;

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
    User user = hostHolder.getUser();
    if( user != null && modelAndView != null){
      int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
      int letterUnreadCount = messageService.findUnreadCount(user.getId(), null);
      modelAndView.addObject("allUnreadCount",noticeUnreadCount+ letterUnreadCount);
    }
  }
}
