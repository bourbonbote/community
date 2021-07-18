package com.bobo.community.Util;

import com.bobo.community.Entity.User;
import org.springframework.stereotype.Component;

/**
 * 持有用户信息，用于充当容器，替代session
 */
@Component
public class HostHolder {
  private ThreadLocal<User> users = new ThreadLocal<>();

  public void setUser(User user){
    users.set(user);
  }
  public User getUser(){
    return users.get();
  }
  public void clear(){
    users.remove();
  }

}
