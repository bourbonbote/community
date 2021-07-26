package com.bobo.community.Util;

public class RedisKeyUtil {
  private static final String SPLIT = ":";
  private static final String PREFIX_LIKE_ENTITY = "like:entity";
  private static final String PREFIX_LIKE_User = "like:user";
  private static final String PREFIX_FOLLOWER = "follower";
  private static final String PREFIX_FOLLOWEE = "followee";

  //获取某个实体（帖子、评论）的赞的key值
  //key    :like:entity:entityType:entityId -> set(userId)
  //value  :userId
  public static String getLikeEntityKey(int entityType, int entityId){
    return PREFIX_LIKE_ENTITY + SPLIT + entityType + SPLIT +entityId;
  }

  // 我收到的赞
  // key    :like:user:userId -> int
  // value  :int
  public static String getLikeUserKey(int userId){
    return PREFIX_LIKE_User + SPLIT + userId;
  }

  //我的关注
  //key   :followee:userId:entityType
  //value :entityId
  //zet(entityId，now)
  public static String getFolloweeKey(int userId,int entityType){
    return PREFIX_FOLLOWEE + SPLIT + userId +SPLIT + entityType ;
  }
  //我的粉丝
  //key   :follower:entityType:entityId
  //value :userId
  //zet(userId，now)
  public static String getFollowerKey(int entityType,int entityId){
    return PREFIX_FOLLOWER + SPLIT + entityType +SPLIT + entityId ;
  }
}
