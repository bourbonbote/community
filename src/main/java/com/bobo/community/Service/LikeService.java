package com.bobo.community.Service;

import com.bobo.community.Util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
  @Autowired
  RedisTemplate redisTemplate;

  /**
   * 实现单击点赞，双击取消的功能
   * @param entityType
   * @param entityId
   * @param userId        点赞用户
   * @param authorId      帖子或者评论的作者
   */
  public void like(int entityType,int entityId,int userId,int authorId){
    redisTemplate.execute(new SessionCallback() {
      @Override
      public Object execute(RedisOperations redisOperations) throws DataAccessException {
        String likeEntityKey = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
        String likeUserKey = RedisKeyUtil.getLikeUserKey(authorId);

        //获取当前是否已经点赞过
        Boolean isMember = redisOperations.opsForSet().isMember(likeEntityKey, userId);

        //开启事务
        //单击——>用户获得点赞&&该评论点赞列表存入点赞者
        //即谁点了赞，以及谁被点赞
        redisOperations.multi();
        if(isMember){
          redisOperations.opsForSet().remove(likeEntityKey,userId);
          redisOperations.opsForValue().decrement(likeUserKey);
        } else{
          redisOperations.opsForSet().add(likeEntityKey,userId);
          redisOperations.opsForValue().increment(likeUserKey);
        }
        //结束事务
        return redisOperations.exec();
      }
    });
  }

  /**
   * 查询某实体点赞的数量
   */
  public long findLikeCount(int entityType,int entityId){
    String likeEntityKey = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
    return redisTemplate.opsForSet().size(likeEntityKey);
  }

  /**
   * 实现"已赞"和"赞"
   * 判断当前是否赞过
   */
  public int findLikeStatus(int entityType,int entityId,int userId){
    String likeEntityKey = RedisKeyUtil.getLikeEntityKey(entityType, entityId);
    return redisTemplate.opsForSet().isMember(likeEntityKey,userId)  ? 1: 0;
  }

  /**
   * 获取某用户的所有点赞数
   * @param authorId
   * @return
   */
  public int findAuthorLikeCount(int authorId){
    String likeUserKey = RedisKeyUtil.getLikeUserKey(authorId);
    Integer count = (Integer) redisTemplate.opsForValue().get(likeUserKey);
    return count == null ? 0 : count.intValue();
  }


}
