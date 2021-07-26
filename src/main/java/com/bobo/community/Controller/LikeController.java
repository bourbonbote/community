package com.bobo.community.Controller;

import com.bobo.community.Entity.User;
import com.bobo.community.Service.LikeService;
import com.bobo.community.Util.CommunityUtil;
import com.bobo.community.Util.HostHolder;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping(path = "/like")
@Controller
public class LikeController {
  @Autowired
  LikeService likeService;

  @Autowired
  HostHolder hostHolder;

  @RequestMapping(path = "/likeAction",method = RequestMethod.POST)
  @ResponseBody
  public String like(int entityType,int entityId,int authorId){
    User user = hostHolder.getUser();

    //点赞
    likeService.like(entityType,entityId,user.getId(),authorId);
    //查询状态
    int likeStatus = likeService.findLikeStatus(entityType, entityId, hostHolder.getUser().getId());
    //查询点赞数
    long likeCount = likeService.findLikeCount(entityType, entityId);
    //存入容器
    Map<String,Object> map = new HashMap<>();
    map.put("likeStatus",likeStatus);
    map.put("likeCount",likeCount);
    //返回json
    return CommunityUtil.jsonToString(0,null,map);
  }
}
