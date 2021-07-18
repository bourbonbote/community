package com.bobo.community.Controller.Interceptor;

import com.bobo.community.Anonotation.LoginRequired;
import com.bobo.community.Util.HostHolder;
import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

  @Autowired
  HostHolder hostHolder;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    if(handler instanceof HandlerMethod){
      HandlerMethod handlerMethod = (HandlerMethod) handler;
      Method method = handlerMethod.getMethod();
      LoginRequired methodAnnotation = method.getAnnotation(LoginRequired.class);
      if(methodAnnotation != null && hostHolder.getUser() == null){
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
      }
    }
    return true;
  }
}
