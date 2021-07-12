package com.bobo.community.Mapper;

import com.bobo.community.Entity.DiscussPost;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //@Para注解用于给参数取别名，如果只有一个参数，且要在<if>中使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);
}
