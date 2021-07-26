package com.bobo.community.Service;

import com.bobo.community.Entity.User;
import com.bobo.community.Util.CommunityConstant;
import com.bobo.community.Util.RedisKeyUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class FollowService implements CommunityConstant {

  @Autowired
  RedisTemplate redisTemplate;

  @Autowired
  UserService userService;

  /**
   * 新增一个关注
   * @param entityType
   * @param entityId
   * @param userId
   */
  public void follow(int entityType, int entityId, int userId){
    String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
    String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

    redisTemplate.execute(new SessionCallback() {
      @Override
      public Object execute(RedisOperations operations) throws DataAccessException {
        operations.multi();

        operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
        operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

        return operations.exec();
      }
    });
  }

  /**
   * 取消关注
   * @param entityType
   * @param entityId
   * @param userId
   */
  public void unfollow(int entityType, int entityId, int userId){
    String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
    String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

    redisTemplate.execute(new SessionCallback() {
      @Override
      public Object execute(RedisOperations operations) throws DataAccessException {
        operations.multi();

        operations.opsForZSet().remove(followeeKey,entityId,System.currentTimeMillis());
        operations.opsForZSet().remove(followerKey,userId,System.currentTimeMillis());

        return operations.exec();
      }
    });
  }

  /**
   * 查询用户的关注数
   * @param userId
   * @param entityType
   * @return
   */
  public long findFolloweeCount(int userId,int entityType){
    String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
    return redisTemplate.opsForZSet().zCard(followeeKey);
  }

  /**
   * 查询用户的粉丝数
   * @param entityType
   * @param entityId
   * @return
   */
  public long findFollowerCount(int entityType,int entityId){
    String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
    return redisTemplate.opsForZSet().zCard(followerKey);
  }

  /**
   * 返回是否已关注
   * @param userId
   * @param entityType
   * @param entityId
   * @return
   */
  public boolean hasFollowed(int userId,int entityType,int entityId){
    String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
    return redisTemplate.opsForZSet().score(followeeKey,entityId) != null;
  }

  /**
   * 查询某用户的关注
   * @param userId
   * @param offset
   * @param limit
   * @return
   */
  public List<Map<String,Object>> getFollowees(int userId, int offset, int limit){
    String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
    //返回的用户都是userId
    Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

    if(targetIds == null){
      return null;
    }

    List<Map<String,Object>> list = new ArrayList<>();
    for(Integer id : targetIds){
      Map<String,Object> map = new HashMap<>();
      User user = userService.findUserById(id);
      map.put("user",user);
      Double time = redisTemplate.opsForZSet().score(followeeKey, id);
      map.put("time",new Date(time.longValue()));
      list.add(map);
    }
    return list;
  }

  /**
   * 查询某用户的粉丝
   * @param entityId
   * @param offset
   * @param limit
   * @return
   */
  public List<Map<String,Object>> getFollowers(int entityId, int offset, int limit){
    String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,entityId );
    //返回的用户都是userId
    Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

    if(targetIds == null){
      return null;
    }

    List<Map<String,Object>> list = new ArrayList<>();
    for(Integer id : targetIds){
      Map<String,Object> map = new HashMap<>();
      User user = userService.findUserById(id);
      map.put("user",user);
      Double time = redisTemplate.opsForZSet().score(followerKey, id);
      map.put("time", new Date(time.longValue()));
      list.add(map);
    }
    return list;
  }
}
