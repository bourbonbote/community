package com.bobo.community.Event;

import com.alibaba.fastjson.JSONObject;
import com.bobo.community.Entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;

@Component
public class EventProducer {

  @Autowired
  private KafkaTemplate kafkaTemplate;

  public void fireEvent(Event event){
    kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
  }
}
