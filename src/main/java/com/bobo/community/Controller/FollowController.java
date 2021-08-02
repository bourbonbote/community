package com.bobo.community.Controller;

import com.bobo.community.Entity.Event;
import com.bobo.community.Entity.Page;
import com.bobo.community.Entity.User;
import com.bobo.community.Event.EventProducer;
import com.bobo.community.Service.FollowService;
import com.bobo.community.Service.UserService;
import com.bobo.community.Util.CommunityConstant;
import com.bobo.community.Util.CommunityUtil;
import com.bobo.community.Util.HostHolder;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/follow")
public class FollowController implements CommunityConstant {

  @Autowired
  FollowService followService;

  @Autowired
  HostHolder hostHolder;

  @Autowired
  UserService userService;

  @Autowired
  EventProducer eventProducer;
  /**
   * 实现关注的功能
   * @param entityType
   * @param entityId
   * @return
   */
  @RequestMapping(path = "/followAction" , method = RequestMethod.POST)
  @ResponseBody
  public String follow(int entityType,int entityId){
    User user = hostHolder.getUser();
    followService.follow(entityType,entityId,user.getId());
    Event event = new Event();
    event.setUserId(hostHolder.getUser().getId())
        .setTopic(TOPIC_FOLLOW)
        .setEntityType(entityType)
        .setEntityId(entityId)
        .setAuthorId(entityId);
    eventProducer.fireEvent(event);
    return CommunityUtil.jsonToString(0,"已关注");
  }

  /**
   * 实现取消关注的功能
   * @param entityType
   * @param entityId
   * @return
   */
  @RequestMapping(path = "/unfollowAction" , method = RequestMethod.POST)
  @ResponseBody
  public String unfollow(int entityType,int entityId){
    User user = hostHolder.getUser();
    followService.unfollow(entityType,entityId,user.getId());
    return CommunityUtil.jsonToString(0,"已取消关注");
  }

  /**
   * 用户的关注列表
   * @param authorId
   * @param page
   * @param model
   * @return
   */
  @RequestMapping(path = "/followees/{authorId}", method = RequestMethod.GET)
  public String followees(@PathVariable("authorId")int authorId, Page page, Model model){
    User author = userService.findUserById(authorId);

    if(author == null){
      throw new IllegalArgumentException("FollowController，用户不存在");
    }
    model.addAttribute("user",author);

    //设置page
    page.setLimit(5);
    page.setPath("/followees/" + authorId);
    page.setRows((int) followService.findFolloweeCount(authorId,ENTITY_TYPE_USER));

    //查询关注列表
    List<Map<String, Object>> followees = followService
        .getFollowees(authorId, page.getOffset(), page.getLimit());
    for(Map<String, Object> map : followees){
      User u = (User) map.get("user");
      //将每个关注状态，一起放入map中，此时map的结构如下
      //user：user
      //time：time
      //status：status
      map.put("hasFollowed",hasFollowed(u.getId()));
    }
    model.addAttribute("followees",followees);

    return "/site/followee";
  }

  /**
   * 用户的粉丝列表
   * @param authorId
   * @param page
   * @param model
   * @return
   */
  @RequestMapping(path = "/followers/{authorId}", method = RequestMethod.GET)
  public String followers(@PathVariable("authorId")int authorId, Page page, Model model){
    User author = userService.findUserById(authorId);

    if(author == null){
      throw new IllegalArgumentException("FollowController，用户不存在");
    }
    model.addAttribute("user",author);
    //设置page
    page.setLimit(5);
    page.setPath("/followers/" + authorId);
    page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER,authorId));

    //查询关注列表
    List<Map<String, Object>> followers = followService
        .getFollowers(authorId, page.getOffset(), page.getLimit());
    for(Map<String, Object> map : followers){
      User u = (User) map.get("user");
      //将每个关注状态，一起放入map中，此时map的结构如下
      //user：user
      //time：time
      //status：status
      map.put("hasFollowed",hasFollowed(u.getId()));
    }
    model.addAttribute("followers",followers);

    return "/site/follower";
  }

  /**
   * 查询用户的关注状态
   * @param authorId
   * @return
   */
  private boolean hasFollowed(int authorId){
    if(hostHolder.getUser() == null){
      return false;
    }
    return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, authorId);
  }
}
