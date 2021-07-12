package com.bobo.community;

import com.bobo.community.Entity.DiscussPost;
import com.bobo.community.Entity.User;
import com.bobo.community.Mapper.DiscussPostMapper;
import com.bobo.community.Mapper.UserMapper;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyBatisTest {
  @Autowired
  UserMapper userMapper;

  @Autowired
  DiscussPostMapper discussPostMapper;

  @Test
  public void selectTests(){
    User user1 = userMapper.selectById(101);
    System.out.println(user1);

    User user2 = userMapper.selectByName("liubei");
    System.out.println(user2);

    User user3 = userMapper.selectByEmail("nowcoder101@sina.com");
    System.out.println(user3);
  }

  @Test
  public void insertTests(){
    User user = new User();
    user.setId(150);
    user.setUsername("bobo");
    user.setEmail("bobo.com");
    user.setActivationCode("1");
    user.setType(0);

    user.setSalt("22");
    user.setCreateTime(new Date());
    user.setHeaderUrl("1111");
    user.setStatus(0);
    user.setPassword("bobo123");
    userMapper.insertUser(user);
  }

  @Test
  public void updateTest(){
    userMapper.updateStatus(150,1);
    userMapper.updatePassword(150,"bobo123");
    userMapper.updateHeader(150,"1234");
  }

  @Test
  public void DiscussPostTest(){
    List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);
    for(DiscussPost discussPost : list){
      System.out.println(discussPost);
    }
    //gittest
    int i = discussPostMapper.selectDiscussPostRows(149);
    System.out.println(i);
  }
}
