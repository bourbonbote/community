package com.bobo.community.Mapper;

import com.bobo.community.Entity.Comment;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {
  List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

  int selectCountByEntity(int entityType, int entityId);

  int insertComment(Comment comment);
}
