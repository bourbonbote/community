package com.bobo.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class LoggerTest {
  private static final Logger logger = LoggerFactory.getLogger(LoggerTest.class);

  @Test
  public void testLogger() {
    System.out.println(logger.getName());

    logger.debug("debug log");
    logger.info("info log");
    logger.warn("warn log");
    logger.error("error log");
  }

  @Test
  public void logTest(){
    //异常捕获到日志测试
    try{
      int i = 1 / 0;
    }catch(Exception exception){
      logger.error("算术运算失败  error log");
    }
  }
}
