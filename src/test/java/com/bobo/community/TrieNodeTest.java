package com.bobo.community;

import com.bobo.community.Util.SensitiveFilterUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TrieNodeTest {

  @Autowired
  SensitiveFilterUtil sensitiveFilterUtil;

  @Test
  public void sensitiveFilterTest(){
    String text = "这里可以赌博，嫖娼，喝酒，吸毒，开票，哈哈哈哈";
    String filter = sensitiveFilterUtil.filter(text);
    System.out.println(filter);


    String text2 = "这里可以☆▷▃▒赌☆▷▃▒博，嫖☆▷▃▒娼，喝☆▷▃▒酒，吸毒☆▷▃▒，开☆▷▃▒票，哈哈哈哈";
    String filter2 = sensitiveFilterUtil.filter(text2);
    System.out.println(filter2);
  }
}
