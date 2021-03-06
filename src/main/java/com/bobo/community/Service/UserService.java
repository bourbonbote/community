package com.bobo.community.Service;

import com.bobo.community.Entity.LoginTicket;
import com.bobo.community.Entity.User;
import com.bobo.community.Mapper.LoginTicketMapper;
import com.bobo.community.Mapper.UserMapper;
import com.bobo.community.Util.CommunityConstant;
import com.bobo.community.Util.CommunityUtil;
import com.bobo.community.Util.MailClient;
import com.bobo.community.Util.RedisKeyUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class UserService implements CommunityConstant {
  @Autowired
  UserMapper userMapper;

  @Autowired
  MailClient mailClient;

  @Autowired
  CommunityUtil communityUtil;

  @Autowired
  TemplateEngine templateEngine;

//  @Autowired
//  LoginTicketMapper loginTicketMapper;
  @Autowired
  RedisTemplate redisTemplate;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  @Value("${community.path.domain}")
  private String domain;

  public User findUserById(int userId){
    User user = getCache(userId);
    if(user == null){
      user = initCache(userId);
    }
    return user;
  }

  public Map<String,Object> register(User user){
    Map<String,Object> map = new HashMap<String,Object>();

    //判断用户是否为空
    if(user == null){
      throw new IllegalArgumentException("用户输入不能为空");
    }
    //判断用户名是否为空
    if(StringUtils.isBlank(user.getUsername())){
      map.put("usernameMsg","用户名不能为空");
      return map;
    }
    //判断密码是否为空
    if(StringUtils.isBlank(user.getPassword())){
      map.put("passwordMsg","密码不能为空");
      return map;
    }
    //判断邮箱是否为空
    if(StringUtils.isBlank(user.getEmail())){
      map.put("emailMsg","邮箱不能为空");
      return map;
    }

    User u = userMapper.selectByName(user.getUsername());
    //判断用户名是否已经被使用过
    if(u != null){
      map.put("usernameMsg","用户名已存在");
      return map;
    }
    u = userMapper.selectByEmail(user.getEmail());
    //判断用户名是否已经被使用过
    if(u != null){
      map.put("emailMsg","该邮箱已经被使用");
      return map;
    }
    //将用户新增到数据库中
    user.setType(0);
    user.setStatus(0);
    user.setSalt(CommunityUtil.generateUUID().substring(0,5));
    user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
    user.setActivationCode(CommunityUtil.generateUUID());
    user.setCreateTime(new Date());
    user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
    userMapper.insertUser(user);
    //发送激活邮件
    Context context = new Context();
    context.setVariable("email",user.getEmail());
    //http://localhost:8080/community/activation/id/code
    String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
    context.setVariable("url",url);
    String content = templateEngine.process("/mail/activation", context);
    mailClient.sendMail(user.getEmail(),"激活账号",content);
    return map;
  }

  public int activation(int userId, String code){
    User user = userMapper.selectById(userId);

    if(user.getStatus() == 1){
      return ACTIVATION_REPEATE;
    } else if(user.getActivationCode().equals(code)){
      userMapper.updateStatus(userId,1);
      clearCache(userId);
      return ACTIVATION_SUCCESS;
    } else{
      return ACTIVATION_FAILURE;
    }
  }

  public Map<String,Object> loginTicket(String username, String password,int expiredSeconds){
    Map<String, Object> map = new HashMap<String,Object>();
    //空值检查
    if(StringUtils.isBlank(username)){
      map.put("usernameMsg","账号不能为空！");
      return map;
    }
    if(StringUtils.isBlank(password)){
      map.put("passwordMsg","密码不能为空！");
      return map;
    }
    //账号检测
    User user = userMapper.selectByName(username);
    if(user == null){
      map.put("usernameMsg","账号不存在");
      return map;
    }
    //状态检测
    if(user.getStatus() == 0){
      map.put("usernameMsg","账号未激活");
      return map;
    }
    //密码检测
    password = CommunityUtil.md5(password + user.getSalt());
    if(!password.equals(user.getPassword())){
      map.put("passwordMsg","密码不正确");
      return map;
    }

    //设置LoginTicket
    LoginTicket loginTicket = new LoginTicket();
    loginTicket.setUserId(user.getId());
    loginTicket.setStatus(0);
    loginTicket.setTicket(CommunityUtil.generateUUID());
    loginTicket.setExpired(new Date(System.currentTimeMillis()+ expiredSeconds * 1000) );
    //将loginTicket添加到数据库中
//    loginTicketMapper.insertLoginTicket(loginTicket);
    String ticketKey = RedisKeyUtil.getTicket(loginTicket.getTicket());
    redisTemplate.opsForValue().set(ticketKey,loginTicket);
    map.put("ticket",loginTicket.getTicket());
    return map;
  }

  public void logout(String ticket){
    String ticketKey = RedisKeyUtil.getTicket(ticket);
    LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    loginTicket.setStatus(1);
    redisTemplate.opsForValue().set(ticketKey,loginTicket);
//    loginTicketMapper.updateLoginTicketStatus(ticket,1);

  }

  public LoginTicket selectLoginTicket(String ticket){
    String ticketKey = RedisKeyUtil.getTicket(ticket);
    LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
//    LoginTicket loginTicket = loginTicketMapper.selectLoginTicket(ticket);
    return loginTicket;
  }

  public void updateHeaderUrl(String headerUrl,int id){
    userMapper.updateHeader(id,headerUrl);
    clearCache(id);
  }

  public User findUserByName(String username){
    return userMapper.selectByName(username);
  }
  //从内存中获取User
  private User getCache(int userId){
    String userKey = RedisKeyUtil.getUser(userId);
    User user = (User) redisTemplate.opsForValue().get(userKey);
    return user;
  }
  //从内存中获取不到User，从MySQL中查询后存入redis
  private User initCache(int userId){
    User user = userMapper.selectById(userId);
    String userKey = RedisKeyUtil.getUser(userId);
    redisTemplate.opsForValue().set(userKey,user,3600, TimeUnit.SECONDS);
    return user;
  }
  //数据发生改变，删除Redis中的user
  private void clearCache(int userId){
    String userKey = RedisKeyUtil.getUser(userId);
    redisTemplate.delete(userKey);
  }


  //用于获取用户权限
  public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
    User user = this.findUserById(userId);

    List<GrantedAuthority> list = new ArrayList<>();
    list.add(new GrantedAuthority() {

      @Override
      public String getAuthority() {
        switch (user.getType()) {
          case 1:
            return AUTHORITY_ADMIN;
          case 2:
            return AUTHORITY_MODERATOR;
          default:
            return AUTHORITY_USER;
        }
      }
    });
    return list;
  }
}
