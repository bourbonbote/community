package com.bobo.community.Service;

import com.bobo.community.Entity.Comment;
import com.bobo.community.Entity.DiscussPost;
import com.bobo.community.Mapper.CommentMapper;
import com.bobo.community.Util.CommunityConstant;
import com.bobo.community.Util.SensitiveFilterUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

@Service
public class CommentService implements CommunityConstant {
  @Autowired
  CommentMapper commentMapper;
  @Autowired
  DiscussPostService discussPostService;
  @Autowired
  SensitiveFilterUtil sensitiveFilterUtil;

  public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit){
    return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
  }

  public int findCountByEntity(int entityType, int entityId){
    return commentMapper.selectCountByEntity(entityType,entityId);
  }

  @Transactional(isolation = Isolation.READ_UNCOMMITTED,propagation = Propagation.REQUIRED)
  public int addComment(Comment comment){
    if(comment == null){
      throw new IllegalArgumentException("comment 参数不能为null");
    }
    //添加评论
    comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
    comment.setContent(sensitiveFilterUtil.filter(comment.getContent()));
    int rows = commentMapper.insertComment(comment);

    //更新评论数量
    if( comment.getEntityType() == ENTITY_TYPE_POST){
      int commentCount = commentMapper.selectCountByEntity(comment.getEntityType(),comment.getEntityId());
      discussPostService.updateCommentCount(comment.getId(),commentCount);
    }
    return rows;
  }
}
