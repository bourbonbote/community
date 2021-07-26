package com.bobo.community.Mapper;

import com.bobo.community.Entity.Message;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper {
  //返回当前会话的每一条最新消息
  List<Message> selectConversation(int userId,int offset,int limit);

  //查询当前用户的会话总数
  int selectCoversationsCount(int userId);

  //查询具体会话
  List<Message> selectLetters(String conversationId,int offset, int limit);

  //查询具体会话数目
  int selectLettersCount(String conversationId);

  //未读数量
  int selectUnreadCount(int userId,String conversationId);

  //更新一整个会话为已读状态
  int updateStatus(List<Integer> ids,int status);

  //添加一个message消息
  int insertMessage(Message message);

}
