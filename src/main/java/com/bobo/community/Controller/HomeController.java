package com.bobo.community.Controller;

import com.bobo.community.Entity.DiscussPost;
import com.bobo.community.Entity.Page;
import com.bobo.community.Entity.User;
import com.bobo.community.Service.DiscussPostService;
import com.bobo.community.Service.UserService;
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
public class HomeController {

  @Autowired
  DiscussPostService discussPostService;

  @Autowired
  UserService userService;

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
        discussPosts.add(map);
      }
    }
    model.addAttribute("discussPosts",discussPosts);
    return "/index";
  }
}
