package com.bobo.community.Util;

public interface CommunityConstant {
  /**
   * 激活成功状态
   */
  int ACTIVATION_SUCCESS = 0;

  /**
   * 重复激活状态
   */
  int ACTIVATION_REPEATE = 1;

  /**
   * 激活失败状态
   */
  int ACTIVATION_FAILURE = 2;

  /**
   * 登录时使用默认时间保存
   */
  int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

  /**
   * 登录时勾选“记住我”保存时间
   */
  int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 10;


  /**
   * 设置实体类的类型为帖子
   */
  int ENTITY_TYPE_POST = 1;

  /**
   * 设置实体类的类型为评论
   */
  int ENTITY_TYPE_COMMENT = 2;

  /**
   * 设置实体类的类型为用户
   */
  int ENTITY_TYPE_USER = 3;

  /**
   * 设置消息队列的类型为评论
   */
  String TOPIC_COMMENT = "comment";


  /**
   * 设置消息队列的类型为点赞
   */
  String TOPIC_LIKE = "like";


  /**
   * 设置消息队列的类型为关注
   */
  String  TOPIC_FOLLOW = "follow";

  /**
   * 设置默认发消息的用户userId为  1
   */
  int SYSTEM_ID = 1;
}
