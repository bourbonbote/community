package com.bobo.community.Controller;


import com.bobo.community.Entity.Comment;
import com.bobo.community.Entity.DiscussPost;
import com.bobo.community.Entity.Event;
import com.bobo.community.Entity.User;
import com.bobo.community.Event.EventProducer;
import com.bobo.community.Service.CommentService;
import com.bobo.community.Service.DiscussPostService;
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

  @Autowired
  EventProducer eventProducer;

  @Autowired
  DiscussPostService discussPostService;

  @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
  public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment){
    comment.setCreateTime(new Date());
    comment.setUserId(hostHolder.getUser().getId());
    comment.setStatus(0);
    commentService.addComment(comment);

    //触发评论事件
    Event event = new Event();
    event.setTopic(TOPIC_COMMENT)
        .setUserId(hostHolder.getUser().getId())
        .setEntityType(ENTITY_TYPE_COMMENT)
        .setEntityId(comment.getEntityId())
        .setData("postId",discussPostId);
    //如果评论的是帖子
    if(comment.getEntityType() == ENTITY_TYPE_POST){
      DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
      event.setAuthorId(discussPost.getUserId());
    } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT){
      Comment target = commentService.findCommentById(comment.getEntityId());
      event.setAuthorId(target.getUserId());
    }
    eventProducer.fireEvent(event);
    return "redirect:/discuss/detail/"+discussPostId;
  }

}
