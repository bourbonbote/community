package com.bobo.community;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class TmpTest {
  @Test
  public void hashMapTest(){
    Map<String,String> map = new HashMap<>();

    map.put("comment","a");
    map.put("comment","b");

    for(String tmp : map.keySet()){
      System.out.println(map.get(tmp));
    }
  }

}
