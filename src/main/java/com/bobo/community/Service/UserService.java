package com.bobo.community.Service;

import com.bobo.community.Entity.User;
import com.bobo.community.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  @Autowired
  UserMapper userMapper;

  public User findUserById(int userId){
    return userMapper.selectById(userId);
  }
}
