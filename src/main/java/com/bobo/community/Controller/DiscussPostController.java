package com.bobo.community.Controller;

import com.bobo.community.Entity.Comment;
import com.bobo.community.Entity.DiscussPost;
import com.bobo.community.Entity.Page;
import com.bobo.community.Entity.User;
import com.bobo.community.Service.CommentService;
import com.bobo.community.Service.DiscussPostService;
import com.bobo.community.Service.LikeService;
import com.bobo.community.Service.UserService;
import com.bobo.community.Util.CommunityConstant;
import com.bobo.community.Util.CommunityUtil;
import com.bobo.community.Util.HostHolder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {
  @Autowired
  DiscussPostService discussPostService;

  @Autowired
  UserService userService;

  @Autowired
  HostHolder hostHolder;

  @Autowired
  CommunityUtil communityUtil;

  @Autowired
  CommentService commentService;

  @Autowired
  LikeService likeService;

  @RequestMapping(path = "/add",method = RequestMethod.POST)
  @ResponseBody
  public String addDiscussPost(String title, String content){
    User user = hostHolder.getUser();
    if( user == null){
      return communityUtil.jsonToString(403,"请登录之后访问");
    }
    DiscussPost discussPost = new DiscussPost();
    discussPost.setTitle(title);
    discussPost.setContent(content);
    discussPost.setCommentCount(0);
    discussPost.setUserId(user.getId());
    discussPost.setStatus(0);
    discussPost.setCreateTime(new Date());
    discussPost.setType(0);
    discussPost.setCommentCount(0);
    discussPostService.addDiscussPost(discussPost);

    return communityUtil.jsonToString(0,"发布成功");
  }

  @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
  public String discussPostDetail(@PathVariable int discussPostId, Model model, Page page){
    //帖子
    DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
    model.addAttribute("post",post);

    //作者
    User user = userService.findUserById(post.getUserId());
    model.addAttribute("user",user);

    //点赞数量
    long likeCount = likeService.findLikeCount(ENTITY_TYPE_POST, discussPostId);
    model.addAttribute("likeCount",likeCount);

    //点赞状态
    int likeStatus=hostHolder.getUser()==null?0:
        likeService.findLikeStatus(ENTITY_TYPE_POST,discussPostId,hostHolder.getUser().getId());
    model.addAttribute("likeStatus",likeStatus);

    //设置评论分页信息
    page.setRows(post.getCommentCount());
    page.setPath("/discuss/detail/" + discussPostId);
    page.setLimit(5);
    //评论：回复帖子
    //回复：回复评论的评论
    //评论列表
    List<Comment> commentList = commentService
        .findCommentsByEntity(ENTITY_TYPE_POST, discussPostId, page.getOffset(),
            page.getLimit());
    //待加入Model的容器commentVoList
    List<Map<String,Object>> commentVoList = new ArrayList<>();
    if (commentList != null) {
      for (Comment comment : commentList){
        Map<String,Object> commentVo = new HashMap<>();
        //将帖子的评论放入容器commentVo中
        commentVo.put("comment",comment);
        //将帖子评论的作者放入容器comementVo中
        commentVo.put("user",userService.findUserById(comment.getUserId()));
        //点赞数量
        likeCount = likeService.findLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
        commentVo.put("likeCount",likeCount);
        //点赞状态
        likeStatus=hostHolder.getUser()==null?0:
            likeService.findLikeStatus(ENTITY_TYPE_COMMENT,comment.getId(),hostHolder.getUser().getId());
        commentVo.put("likeStatus",likeStatus);
        //获取回复列表
        List<Comment> replyList = commentService
            .findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0,
                Integer.MAX_VALUE);
        //待加入commentVoList的容器commentVoList
        List<Map<String,Object>> replyVoList = new ArrayList<>();
        if(replyList != null){
          for(Comment reply : replyList){
            Map<String,Object> replyVo = new HashMap<>();
            replyVo.put("user",userService.findUserById(reply.getUserId()));
            replyVo.put("reply",reply);
            User target =
                reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
            replyVo.put("target",target);
            //点赞数量
            likeCount = likeService.findLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
            replyVo.put("likeCount",likeCount);
            //点赞状态
            likeStatus=hostHolder.getUser()==null?0:
                likeService.findLikeStatus(ENTITY_TYPE_COMMENT,reply.getId(),hostHolder.getUser().getId());
            replyVo.put("likeStatus",likeStatus);
            replyVoList.add(replyVo);
          }

        }
        commentVo.put("replys",replyVoList);
        int replyCount = commentService.findCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
        commentVo.put("replyCount",replyCount);
        commentVoList.add(commentVo);
      }
    }
    model.addAttribute("comments",commentVoList);
    return "/site/discuss-detail";
  }
}
