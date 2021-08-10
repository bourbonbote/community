package com.bobo.community.Service;

import com.bobo.community.Entity.DiscussPost;
import com.bobo.community.Mapper.DiscussPostMapper;
import com.bobo.community.Util.SensitiveFilterUtil;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class DiscussPostService {

  public static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

  @Value("${caffeine.posts.max-size}")
  private int maxSize;

  @Value("${caffeine.posts.expire-seconds}")
  private int expireSeconds;

  @Autowired
  SensitiveFilterUtil sensitiveFilterUtil;

  @Autowired
  DiscussPostMapper discussPostMapper;

  // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

  // 帖子列表缓存
  private com.github.benmanes.caffeine.cache.@NonNull LoadingCache<String, List<DiscussPost>> postListCache;

  // 帖子总数缓存
  private @NonNull LoadingCache<Integer, Integer> postRowsCache;

  @PostConstruct
  public void init(){
    //使用caffeine查询帖子列表
    postListCache = Caffeine.newBuilder()
        .maximumSize(maxSize)
        .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
        .build(new CacheLoader<String, List<DiscussPost>>() {
          @Nullable
          @Override
          public List<DiscussPost> load(@NonNull String key) throws Exception {
            if (key == null || key.length() == 0) {
              throw new IllegalArgumentException("参数错误!");
            }

            String[] params = key.split(":");
            if (params == null || params.length != 2) {
              throw new IllegalArgumentException("参数错误!");
            }

            int offset = Integer.valueOf(params[0]);
            int limit = Integer.valueOf(params[1]);

            // 二级缓存: Redis -> mysql

            logger.debug("load post list from DB.");
            return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
          }
        });
    //使用caffeine查询帖子总数
    postRowsCache = Caffeine.newBuilder()
        .maximumSize(maxSize)
        .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
        .build(new CacheLoader<Integer, Integer>() {
          @Nullable
          @Override
          public Integer load(@NonNull Integer key) throws Exception {
            logger.debug("load post rows from DB.");
            return discussPostMapper.selectDiscussPostRows(key);
          }
        });
  }

  public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int mode){
    //如果是使用查看热帖功能时，数据走caffeine缓存获取数据
    if(userId == 0 && mode == 1){
      return postListCache.get(offset + ":" + limit);
    }
    logger.debug("load post list from DB.");
    return discussPostMapper.selectDiscussPosts(userId,offset,limit,mode);
  }

  public int findDiscussPostRows(int userId){
    //如果是使用查看热帖功能时，数据走caffeine缓存获取数据
    if(userId == 0){
      return postRowsCache.get(userId);
    }
    logger.debug("load post list from DB.");
    return discussPostMapper.selectDiscussPostRows(0);
  }

  public int addDiscussPost(DiscussPost discussPost){
    if(discussPost == null){
      throw new IllegalArgumentException("discussPost内容不能为空");
    }
    //转换html格式页面
    discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
    discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
    //转换敏感词
    discussPost.setTitle(sensitiveFilterUtil.filter(discussPost.getTitle()));
    discussPost.setContent(sensitiveFilterUtil.filter(discussPost.getContent()));

    return discussPostMapper.insertDiscussPost(discussPost);
  }

  public DiscussPost findDiscussPostById(int id){
    return discussPostMapper.selectDiscussPostById(id);
  }

  public int updateCommentCount(int id, int commentCount){
    return discussPostMapper.updateCommentCount(id,commentCount);
  }

  public void updateType(int id, int type){
    discussPostMapper.updateType(id,type);
  }

  public void updateStatus(int id, int status){
    discussPostMapper.updateStatus(id,status);
  }


  public void updateScore(int postId, double score) {
    discussPostMapper.updateScore(postId,score);
  }
}
