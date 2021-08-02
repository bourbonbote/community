package com.bobo.community.Controller;

import com.alibaba.fastjson.JSONObject;
import com.bobo.community.Entity.Message;
import com.bobo.community.Entity.Page;
import com.bobo.community.Entity.User;
import com.bobo.community.Service.MessageService;
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
import org.springframework.web.util.HtmlUtils;

@Controller
@RequestMapping(path = "/message")
public class MessageController implements CommunityConstant {
  @Autowired
  MessageService messageService;

  @Autowired
  HostHolder hostHolder;

  @Autowired
  UserService userService;

  //私信列表
  @RequestMapping(path = "/list",method = RequestMethod.GET)
  public String findConversation(Model model, Page page){
    //设置分页信息
    User user = hostHolder.getUser();
    page.setLimit(5);
    page.setPath("/message/list");
    page.setRows(messageService.findCoversationsCount(user.getId()));

    //设置查询到的数据
    List<Map<String,Object>> conversations = new ArrayList<>();
    List<Message> conversationList = messageService
        .findConversation(user.getId(), page.getOffset(), page.getLimit());
    if (conversations != null){
      for(Message message : conversationList){
        Map<String, Object> map = new HashMap<>();
        //每个会话中的最后一条消息
        map.put("conversation",message);
        //每个会话中有多少个消息
        map.put("letterCount",messageService.findLettersCount(message.getConversationId()));
        //每个会话中有多少个未读消息
        map.put("unreadCount",messageService.findUnreadCount(user.getId(),message.getConversationId()));
        int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
        map.put("target",userService.findUserById(targetId));
        conversations.add(map);
      }
    }
    model.addAttribute("conversations",conversations);
    //总的未读数
    int letterUnreadCount = messageService.findUnreadCount(user.getId(), null);
    model.addAttribute("letterUnreadCount",letterUnreadCount);

    int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
    model.addAttribute("noticeUnreadCount",noticeUnreadCount);

    return "/site/letter";
  }

  @RequestMapping(path = "/letterDetail/{conversationId}",method = RequestMethod.GET)
  public String getLetters(@PathVariable("conversationId")String conversationId,Page page , Model model){
    page.setLimit(5);
    page.setRows(messageService.findLettersCount(conversationId));
    page.setPath("/message/letterDetail/" + conversationId);

    //获取私信列表
    List<Map<String,Object>> letters = new ArrayList<>();
    List<Message> lettersList = messageService
        .findLetters(conversationId, page.getOffset(), page.getLimit());
    if(lettersList != null){
      for(Message letter : lettersList){
        Map<String,Object> map = new HashMap<>();
        map.put("letter",letter);
        map.put("fromUser",userService.findUserById(letter.getFromId()));
        letters.add(map);
      }
    }
    model.addAttribute("letters",letters);
    model.addAttribute("targetUser",getTargetUser(conversationId));

    //设置未读为已读
    List<Integer> ids = getLettersId(lettersList);
    if(!ids.isEmpty()){
      messageService.updateMesssageStatus(ids);
    }
    return "/site/letter-detail";
  }

  @RequestMapping(path = "/send",method = RequestMethod.POST)
  @ResponseBody
  public String sendMessage(String toName,String content){
    User target = userService.findUserByName(toName);
    if( target == null){
      return CommunityUtil.jsonToString(0,"该用户不存在");
    }

    Message message = new Message();
    message.setContent(content);
    message.setCreateTime(new Date());
    message.setFromId(hostHolder.getUser().getId());
    message.setToId(target.getId());
    String conversationId = null;
    if(message.getFromId() < message.getToId()){
      conversationId = message.getFromId() + "_" + message.getToId();
    } else {
      conversationId =  message.getToId() + "_" + message.getFromId();
    }
    message.setConversationId(conversationId);
    messageService.addMessage(message);
    return CommunityUtil.jsonToString(0);
  }

  //获取通知的列表数据
  @RequestMapping(path = "/notice/list" ,method = RequestMethod.GET)
  public String getNoticeList(Model model){
    //登录的用户
    User user = hostHolder.getUser();


    //评论类通知
    Message message = messageService.findNoticeLatest(user.getId(), TOPIC_COMMENT);
    Map<String,Object> messageVo = new HashMap<>();

    messageVo.put("message",message);
    if(message != null){

      String content = HtmlUtils.htmlUnescape(message.getContent());
      Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

      User u = userService.findUserById((Integer) data.get("userId"));
      messageVo.put("user",u);
      messageVo.put("entityType",data.get("entityType"));
      messageVo.put("entityId",data.get("entityId"));
      messageVo.put("discussPostId",data.get("discussPostId"));

      int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
      messageVo.put("count",noticeCount);

      int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_COMMENT);
      messageVo.put("unreadCount",noticeUnreadCount);

    }
    model.addAttribute("commentNotice",messageVo);

    //点赞类通知
    message = messageService.findNoticeLatest(user.getId(), TOPIC_LIKE);
    messageVo = new HashMap<>();
    messageVo.put("message",message);
    if(message != null){

      String content = HtmlUtils.htmlUnescape(message.getContent());
      Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

      User u = userService.findUserById((Integer) data.get("userId"));
      messageVo.put("user",u);
      messageVo.put("entityType",data.get("entityType"));
      messageVo.put("entityId",data.get("entityId"));
      messageVo.put("discussPostId",data.get("discussPostId"));

      int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
      messageVo.put("count",noticeCount);

      int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_LIKE);
      messageVo.put("unreadCount",noticeUnreadCount);

    }
    model.addAttribute("likeNotice",messageVo);

    //关注类通知
    message = messageService.findNoticeLatest(user.getId(), TOPIC_FOLLOW);
    messageVo = new HashMap<>();
    messageVo.put("message",message);
    if(message != null){

      String content = HtmlUtils.htmlUnescape(message.getContent());
      Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

      User u = userService.findUserById((Integer) data.get("userId"));
      messageVo.put("user",u);
      messageVo.put("entityType",data.get("entityType"));
      messageVo.put("entityId",data.get("entityId"));

      int noticeCount = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
      messageVo.put("count",noticeCount);

      int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), TOPIC_FOLLOW);
      messageVo.put("unreadCount",noticeUnreadCount);

    }
    model.addAttribute("followNotice",messageVo);

    //将所有会话的未读消息的总数存入model
    int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
    model.addAttribute("noticeUnreadCount",noticeUnreadCount);

    int lettersUnreadCount = messageService.findUnreadCount(user.getId(), null);
    model.addAttribute("lettersUnreadCount",lettersUnreadCount);

    return "/site/notice";
  }

  //获取通知列表中的详细数据
  @RequestMapping(path = "/notice/detail/{topic}" ,method = RequestMethod.GET)
  public String getNoticeDetail(@PathVariable("topic")String topic, Model model,Page page){
    //获取当前登录的用户
    User user = hostHolder.getUser();

    //设置page的值
    page.setPath("/message/notice/detail/"+topic);
    page.setLimit(5);
    page.setRows(messageService.findNoticeCount(user.getId(),topic));

    //获取list中的数据
    List<Message> noticeList = messageService
        .findNoticeDetail(user.getId(), topic, page.getOffset(), page.getLimit());
    //创建list集合，用于存放数据
    List<Map<String,Object>> noticeVoList = new ArrayList<>();
    for(Message notice : noticeList){
      Map<String,Object> map = new HashMap<>();
      map.put("notice",notice);

      String content = HtmlUtils.htmlUnescape(notice.getContent());
      HashMap data = JSONObject.parseObject(content, HashMap.class);

      map.put("user",userService.findUserById((Integer) data.get("userId")));
      map.put("entityType",data.get("entityType"));
      map.put("entityId",data.get("entityId"));
      map.put("postId",data.get("postId"));
      map.put("fromUser",userService.findUserById(notice.getFromId()));
      noticeVoList.add(map);
    }
    model.addAttribute("noticeVoList",noticeVoList);

    //设置未读为已读
    List<Integer> ids = getLettersId(noticeList);
    if(!ids.isEmpty()){
      messageService.updateMesssageStatus(ids);
    }
    return "/site/notice-detail";
  }






  //转换conversationId
  private User getTargetUser(String conversationId){
    String[] ids = conversationId.split("_");
    int id0 = Integer.parseInt(ids[0]);
    int id1 = Integer.parseInt(ids[1]);
    return hostHolder.getUser().getId() == id0 ? userService.findUserById(id1) : userService.findUserById(id0);
  }
  //获取未读Message的Id
  private List<Integer> getLettersId(List<Message> messageList){
    List<Integer> ids = new ArrayList<>();
    if(messageList != null){
      for(Message letter : messageList){
        if(letter.getStatus() == 0 && hostHolder.getUser().getId() == letter.getToId()){
          ids.add(letter.getId());
        }
      }
    }
    return ids;
  }


}
