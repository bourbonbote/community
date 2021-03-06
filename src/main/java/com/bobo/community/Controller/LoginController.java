package com.bobo.community.Controller;

import com.bobo.community.Entity.User;
import com.bobo.community.Service.UserService;
import com.bobo.community.Util.CommunityConstant;
import com.bobo.community.Util.CommunityUtil;
import com.bobo.community.Util.RedisKeyUtil;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.google.code.kaptcha.Producer;
@Controller
public class LoginController implements CommunityConstant {

  public static Logger logger = LoggerFactory.getLogger(LoginController.class);

  @Autowired
  private UserService userService;

  @Autowired
  private Producer kaptchaProducer;

  @Autowired
  private RedisTemplate redisTemplate;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  @RequestMapping(path = "/register", method = RequestMethod.GET)
  public String getRegisterPage(){
    return "/site/register";
  }

  @RequestMapping(path = "/login", method = RequestMethod.GET)
  public String getLoginPage(){
    return "/site/login";
  }

  @RequestMapping(path = "/register",method = RequestMethod.POST)
  public String register(Model model, User user){
    Map<String, Object> map = userService.register(user);
    //map??????null??????????????????
    //map?????????null??????????????????
    if(map == null | map.isEmpty()){
      model.addAttribute("Msg","???????????????????????????????????????????????????????????????????????????");
      model.addAttribute("target","/index");
      return "/site/operate-result";
    } else {
      model.addAttribute("usernameMsg",map.get("usernameMsg"));
      model.addAttribute("passwordMsg",map.get("passwordMsg"));
      model.addAttribute("emailMsg",map.get("emailMsg"));
      return "/site/register";
    }
  }

  //http://localhost:8080/community/activation/id/code
  @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
  public String activation(Model model,@PathVariable("userId") int userId,@PathVariable("code") String code){
    int result = userService.activation(userId, code);
    if(result == ACTIVATION_SUCCESS){
      model.addAttribute("Msg","????????????????????????????????????");
      model.addAttribute("target","/login");
    } else if(result == ACTIVATION_REPEATE) {
      model.addAttribute("Msg","??????????????????????????????????????????");
      model.addAttribute("target","/index");
    } else {
      model.addAttribute("Msg","?????????????????????????????????????????????");
      model.addAttribute("target","/index");
    }
    return "/site/operate-result";
  }


  @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
  public void kaptcha(HttpServletResponse response/*, HttpSession session*/){
    //???????????????
    String text = kaptchaProducer.createText();
    BufferedImage image = kaptchaProducer.createImage(text);
    //??????session
//    session.setAttribute("kaptcha",text);

    //??????????????????
    String kaptchaOwner = CommunityUtil.generateUUID();
    Cookie cookie = new Cookie("kaptchaOwner",kaptchaOwner);
    cookie.setPath(contextPath);
    cookie.setMaxAge(60);
    response.addCookie(cookie);
    //??????redis
    String kaptchaKey = RedisKeyUtil.getKaptcha(kaptchaOwner);
    redisTemplate.opsForValue().set(kaptchaKey,text, 60,TimeUnit.SECONDS);

    response.setContentType("image/png");
    try {
      OutputStream os = response.getOutputStream();
      ImageIO.write(image,"png",os);
    } catch (IOException e) {
      logger.error("?????????????????????" + e.getMessage());
    }
  }

  @RequestMapping(path = "/login",method = RequestMethod.POST)
  public String login(String username, String password, boolean rememberMe, String code,
      Model model,@CookieValue("kaptchaOwner")String kaptchaOwner,
      /*HttpSession session,*/ HttpServletResponse response){

    //???????????????
 //   String kaptcha = (String) session.getAttribute("kaptcha");
    String kaptcha = null;
    if(StringUtils.isNotBlank(kaptchaOwner)){
    String kaptchaKey = RedisKeyUtil.getKaptcha(kaptchaOwner);
    kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
    }

    //???????????????
    if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)) {
      model.addAttribute("codeMsg","???????????????");
      return "/site/login";
    }

    int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
    Map<String, Object> map = userService.loginTicket(username, password, expiredSeconds);
    if( map.containsKey("ticket")) {
      String ticket = (String) map.get("ticket");
      Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
      cookie.setPath(contextPath);
      cookie.setMaxAge(expiredSeconds);
      response.addCookie(cookie);
      return "redirect:/index";
    } else {
      model.addAttribute("usernameMsg",map.get("usernameMsg"));
      model.addAttribute("passwordMsg",map.get("passwordMsg"));
      return "/site/login";
    }
  }

  @RequestMapping(path = "/logout",method = RequestMethod.GET)
  public String logout(@CookieValue("ticket")String ticket ){
    userService.logout(ticket);
    SecurityContextHolder.clearContext();
    return "redirect:/index";
  }
}
