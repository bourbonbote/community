package com.bobo.community;

import com.bobo.community.Entity.Message;
import com.bobo.community.Service.MessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaTmpTest {
  @Autowired
  MessageService messageService;
  @Test
  public void testMessageMapper() {
    Message comment = messageService.findNoticeLatest(166, "comment");
    System.out.println(comment);
  }
}
