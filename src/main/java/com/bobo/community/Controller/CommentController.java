package com.bobo.community.Controller;


import com.bobo.community.Entity.Comment;
import com.bobo.community.Entity.User;
import com.bobo.community.Service.CommentService;
import com.bobo.community.Util.CommunityConstant;
import com.bobo.community.Util.HostHolder;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(path = "/comment")
public class CommentController implements CommunityConstant {
  @Autowired
  CommentService commentService;

  @Autowired
  HostHolder hostHolder;

  @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
  public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment){
    comment.setCreateTime(new Date());
    comment.setUserId(hostHolder.getUser().getId());
    comment.setStatus(0);
    commentService.addComment(comment);
    return "redirect:/discuss/detail/"+discussPostId;
  }

}
