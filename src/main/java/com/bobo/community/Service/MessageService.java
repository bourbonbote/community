package com.bobo.community.Service;

import com.bobo.community.Entity.Message;
import com.bobo.community.Mapper.MessageMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
  @Autowired
  MessageMapper messageMapper;

  public List<Message> findConversation(int userId,int offset,int limit){
    return messageMapper.selectConversation(userId,offset,limit);
  }

  public int findCoversationsCount(int userId){
    return messageMapper.selectCoversationsCount(userId);
  }

  public List<Message> findLetters(String conversationId,int offset, int limit){
    return messageMapper.selectLetters(conversationId,offset,limit);
  }

  public int findLettersCount(String conversationId){
    return messageMapper.selectLettersCount(conversationId);
  }

  public int findUnreadCount(int userId,String conversationId){
    return messageMapper.selectUnreadCount(userId,conversationId);
  }

  public int updateMesssageStatus(List<Integer> ids){
    return messageMapper.updateStatus(ids,1);
  }

  public int addMessage(Message message){
    return messageMapper.insertMessage(message);
  }
}
