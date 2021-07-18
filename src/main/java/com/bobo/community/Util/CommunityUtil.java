package com.bobo.community.Util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Component
public class CommunityUtil {

  public static String generateUUID(){
    return UUID.randomUUID().toString().replaceAll("-","");
  }

  public static String md5(String key){
    if(StringUtils.isBlank(key)) {
      return null;
    }
    return DigestUtils.md5DigestAsHex(key.getBytes());
  }

  public String jsonToString(int code, String msg, Map<String,Object> map){
    JSONObject json = new JSONObject();
    json.put("code",code);
    json.put("msg",msg);
    if(map != null){
      for(String key : map.keySet()){
        json.put(key,map.get(key));
      }
    }
    return json.toJSONString();
  }

  public String jsonToString(int code, String msg){
    return jsonToString(code,msg,null);
  }

  public String jsonToString(int code){
    return jsonToString(code,null,null);
  }
}
