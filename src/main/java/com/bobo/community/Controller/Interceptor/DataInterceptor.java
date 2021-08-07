package com.bobo.community.Controller.Interceptor;

import com.bobo.community.Service.DataService;
import com.bobo.community.Util.HostHolder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class DataInterceptor implements HandlerInterceptor {

  @Autowired
  DataService dataService;

  @Autowired
  HostHolder hostHolder;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    //计入UV
    String ip = request.getRemoteHost();
    dataService.recordUV(ip);

    //计入DAU
    if(hostHolder.getUser() != null){
      int id = hostHolder.getUser().getId();
      dataService.recordDAU(id);
    }

    return true;
  }
}
