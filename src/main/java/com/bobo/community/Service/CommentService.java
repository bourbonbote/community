package com.bobo.community.Service;

import com.bobo.community.Entity.Comment;
import com.bobo.community.Mapper.CommentMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
  @Autowired
  CommentMapper commentMapper;

  public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit){
    return commentMapper.selectCommentsByEntity(entityType,entityId,offset,limit);
  }

  public int findCountByEntity(int entityType, int entityId){
    return commentMapper.selectCountByEntity(entityType,entityId);
  }
}
