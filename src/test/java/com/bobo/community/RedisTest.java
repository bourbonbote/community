package com.bobo.community;


import com.bobo.community.Config.RedisConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
  @Autowired
  private RedisTemplate redisTemplate;

  @Test
  public void RedisStringTest(){
    String key = "test:count";
    redisTemplate.opsForValue().set(key,"hello");
    System.out.println(redisTemplate.opsForValue().get(key));
  }

  @Test
  public void RedisHashTest(){
    String key = "test:user";
    redisTemplate.opsForHash().put(key,"username","bobo");
    redisTemplate.opsForHash().put(key,"age",0);
    System.out.println(redisTemplate.opsForHash().get(key,"username"));
    System.out.println(redisTemplate.opsForHash().get(key,"age"));
  }
}
