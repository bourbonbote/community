package com.bobo.community.Service;

import com.bobo.community.Entity.DiscussPost;
import com.bobo.community.Mapper.DiscussPostMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscussPostService {
  @Autowired
  DiscussPostMapper discussPostMapper;

  public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
    return discussPostMapper.selectDiscussPosts(0,0,10);
  }

  public int findDiscussPostRows(int userId){
    return discussPostMapper.selectDiscussPostRows(0);
  }
}
