package com.bobo.community.quartz;

import com.bobo.community.Entity.DiscussPost;
import com.bobo.community.Service.DiscussPostService;
import com.bobo.community.Service.LikeService;
import com.bobo.community.Util.CommunityConstant;
import com.bobo.community.Util.RedisKeyUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

public class PostScoreRefreshJob implements CommunityConstant , Job {

  public static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

  @Autowired
  RedisTemplate redisTemplate;

  @Autowired
  DiscussPostService discussPostService;

  @Autowired
  LikeService likeService;

  public static final Date era;

  static {
    try {
      era = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss").parse("2014-08-01 00:00:00");
    } catch (ParseException e) {
      throw new RuntimeException("PostScoreRefreshJob初始化失败");
    }
  }

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    String postScoreKey = RedisKeyUtil.getPostScoreKey();
    BoundSetOperations boundSetOperations = redisTemplate.boundSetOps(postScoreKey);
    if( boundSetOperations.size() == 0 ){
      logger.info("[任务取消] 没有需要刷新的帖子" );
      return;
    }

    logger.info("[任务开始] 正在刷新帖子分数" + boundSetOperations.size());
    while(boundSetOperations.size() > 0){
      this.refresh((Integer) boundSetOperations.pop());
    }
    logger.info("[任务结束] 帖子刷新完毕");
  }

  private void refresh(int postId){
    //根据postId获取discussPost对象
    DiscussPost discussPost = discussPostService.findDiscussPostById(postId);
    if (discussPost == null) {
      logger.error("该帖子不存在: id = " + postId);
      return;
    }
    //为了进行公式计算
    boolean wonderful = discussPost.getStatus() == 1;
    int commentCount = discussPost.getCommentCount();
    long likeCont = likeService.findLikeCount(ENTITY_TYPE_POST,postId);

    //计算权重
    double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCont *2 ;
    //分数 = 帖子权重+ 帖子日期
    double score  = Math.log10(Math.max(w,1))
        + (discussPost.getCreateTime().getTime() - era.getTime() ) / (1000* 3600 * 24);
    //更新帖子
    discussPostService.updateScore(postId,score);

  }
}
