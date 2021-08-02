package com.bobo.community.Event;

import com.alibaba.fastjson.JSONObject;
import com.bobo.community.Entity.Event;
import com.bobo.community.Entity.Message;
import com.bobo.community.Service.MessageService;
import com.bobo.community.Util.CommunityConstant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer implements CommunityConstant {

  public static Logger logger = LoggerFactory.getLogger(EventConsumer.class);

  @Autowired
  private MessageService messageService;

  @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
  public void handleEvent(ConsumerRecord record){
    if(record == null || record.value() == null){
      logger.error("消费者队列拿到为null");
       return;
    }

    Event event = JSONObject.parseObject(record.value().toString(),Event.class);

    if (event == null){
      logger.error("消费者队列拿到为null");
      return;
    }

    //发送站内通知
    Message message = new Message();
    message.setFromId(SYSTEM_ID);
    message.setToId(event.getAuthorId());
    message.setConversationId(event.getTopic());
    message.setCreateTime(new Date());

    //设置message的content属性
    Map<String,Object> content = new HashMap<>();
    content.put("userId",event.getUserId());
    content.put("entityType",event.getEntityType());
    content.put("entityId",event.getEntityId());
    if(!event.getData().isEmpty()){
      for(Map.Entry<String,Object> entry : event.getData().entrySet()){
        content.put(entry.getKey(),entry.getValue());
      }
    }
    message.setContent(JSONObject.toJSONString(content));

    //插入一条新的message
    messageService.addMessage(message);
  }
}
