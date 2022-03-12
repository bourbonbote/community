package com.bobo.community.Controller.ControllerAdvice;

import com.bobo.community.Util.CommunityUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

  public static Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

  @ExceptionHandler
  public void handleException(Exception e, HttpServletResponse response, HttpServletRequest request)
      throws IOException {
    logger.error(e.getMessage());
    for(StackTraceElement element : e.getStackTrace()){
      logger.error("服务器发生异常："+element.toString());
    }
    String xRequestedWith = request.getHeader("x-requested-with");
    //异步请求
    if ("XMLHttpRequest".equals(xRequestedWith)) {
      response.setContentType("application/plain;charset=utf-8");
      PrintWriter writer = response.getWriter();
      writer.write(CommunityUtil.jsonToString(1, "服务器异常!"));
      //非异步
    } else {
      response.sendRedirect(request.getContextPath() + "/error");
    }
  }
}
