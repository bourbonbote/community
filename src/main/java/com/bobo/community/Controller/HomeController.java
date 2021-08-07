package com.bobo.community.Controller;

import com.bobo.community.Entity.DiscussPost;
import com.bobo.community.Entity.Page;
import com.bobo.community.Entity.User;
import com.bobo.community.Service.DiscussPostService;
import com.bobo.community.Service.LikeService;
import com.bobo.community.Service.UserService;
import com.bobo.community.Util.CommunityConstant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController implements CommunityConstant {

  @Autowired
  DiscussPostService discussPostService;

  @Autowired
  UserService userService;

  @Autowired
  LikeService likeService;

  @RequestMapping(path = "/index",method = RequestMethod.GET)
  public String findAllDiscussPost(Model model, Page page){
    page.setPath("/index");
    page.setRows(discussPostService.findDiscussPostRows(0));
    List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
    List<Map<String,Object>> discussPosts = new ArrayList<>();
    if( list != null) {
      for(DiscussPost discussPost : list) {
        Map<String,Object> map = new HashMap<>();
        map.put("post",discussPost);
        User user = userService.findUserById(discussPost.getUserId());
        map.put("user",user);
        long likeCount = likeService.findLikeCount(ENTITY_TYPE_POST, discussPost.getId());
        map.put("likeCount",likeCount);
        discussPosts.add(map);
      }
    }
    model.addAttribute("discussPosts",discussPosts);
    return "/index";
  }
  @RequestMapping(path = "/error", method = RequestMethod.GET)
  public String getErrorPage(){
    return "/error/500";
  }

  @RequestMapping(path = "/denied",method = RequestMethod.GET)
  public String getNoAuthority(){
    return "/error/404";
  }
}
