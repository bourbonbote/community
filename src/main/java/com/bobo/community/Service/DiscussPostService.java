package com.bobo.community.Service;

import com.bobo.community.Entity.DiscussPost;
import com.bobo.community.Mapper.DiscussPostMapper;
import com.bobo.community.Util.CommunityUtil;
import com.bobo.community.Util.SensitiveFilterUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class DiscussPostService {
  @Autowired
  SensitiveFilterUtil sensitiveFilterUtil;

  @Autowired
  DiscussPostMapper discussPostMapper;

  public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit,int mode){
    return discussPostMapper.selectDiscussPosts(userId,offset,limit,mode);
  }

  public int findDiscussPostRows(int userId){
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
