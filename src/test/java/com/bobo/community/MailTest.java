package com.bobo.community;

import com.bobo.community.Util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailTest {

  private static final Logger logger = LoggerFactory.getLogger(MailTest.class);

  @Autowired
  MailClient mailSender;

  @Autowired
  TemplateEngine templateEngine;

  @Test
  public void sendTest(){
    mailSender.sendMail("1048281260@qq.com","Test","Hello");
  }

  @Test
  public void sendHtmlTest(){
    Context context = new Context();
    context.setVariable("username","bobo");
    String content = templateEngine.process("/mail/demo", context);

    mailSender.sendMail("1048281260@qq.com","Test",content);
  }
}
