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
  int COMMENT_ENTITY_TYPE_POST = 1;

  /**
   * 设置实体类的类型为评论
   */
  int COMMENT_ENTITY_TYPE_COMMENT = 2;
}
