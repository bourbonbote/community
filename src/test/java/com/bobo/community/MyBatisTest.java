package com.bobo.community;

import com.bobo.community.Entity.DiscussPost;
import com.bobo.community.Entity.LoginTicket;
import com.bobo.community.Entity.Message;
import com.bobo.community.Entity.User;
import com.bobo.community.Mapper.DiscussPostMapper;
import com.bobo.community.Mapper.LoginTicketMapper;
import com.bobo.community.Mapper.MessageMapper;
import com.bobo.community.Mapper.UserMapper;
import java.util.ArrayList;
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

  @Autowired
  LoginTicketMapper loginTicketMapper;

  @Autowired
  MessageMapper messageMapper;

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
    DiscussPost discussPost = new DiscussPost();
    discussPost.setStatus(0);
    discussPost.setCreateTime(new Date());
    discussPost.setContent("Text");
    discussPost.setUserId(166);
    discussPost.setCommentCount(0);
    discussPost.setTitle("Test");
    discussPostMapper.insertDiscussPost(discussPost);
    List<DiscussPost> list = discussPostMapper.selectDiscussPosts(149,0,10);
    for(DiscussPost discussPost1 : list){
      System.out.println(discussPost1);
    }
    //gittest
    int i = discussPostMapper.selectDiscussPostRows(149);
    System.out.println(i);
  }

  @Test
  public void LoginTicketTest(){
    LoginTicket loginTicket = new LoginTicket();
    loginTicket.setId(1);
    loginTicket.setStatus(0);
    loginTicket.setUserId(2);
    loginTicket.setTicket("123");
    loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10) );
    loginTicketMapper.insertLoginTicket(loginTicket);
    LoginTicket lt = loginTicketMapper.selectLoginTicket("123");
    System.out.println(lt);
    loginTicketMapper.updateLoginTicketStatus(lt.getTicket(),1);
  }

  @Test
  public void HomeTest(){
    List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 50, 10);
    for(DiscussPost discussPost :list){
      System.out.println(list);
    }
  }

  @Test
  public void messageTest(){
    List<Message> messages = messageMapper.selectConversation(111, 0, 20);
    for(Message message :messages){
      System.out.println(message);
    }
    int i = messageMapper.selectCoversationsCount(111);
    System.out.println(i);

    messages = messageMapper.selectLetters("111_112", 0, 10);
    for(Message message :messages){
      System.out.println(message);
    }
    i = messageMapper.selectLettersCount("111_112");
    System.out.println(i);
    i = messageMapper.selectUnreadCount(111,null);
    System.out.println(i);
  }
  @Test
  public void MessageTest(){
    List<Integer> ids = new ArrayList<>();
    ids.add(1);
    messageMapper.updateStatus(ids,0);
  }
}
