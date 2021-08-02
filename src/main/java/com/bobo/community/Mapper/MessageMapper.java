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

  //显示最近的系统通知
  Message selectNoticeLatest(int userId,String conversationId);

  //显示某notice下的数量
  int selectNoticeCount(int userId,String conversationId);

  //显示未读的数量（总的、某会话的）
  int selectNoticeUnreadCount(int userId,String conversationId);

  //获取分页的会话详细列表，封装为list集合
  List<Message> selectNoticeDetail(int userId,String conversationId,int offset,int limit);
}
