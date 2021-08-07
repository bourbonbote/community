package com.bobo.community.Service;

import com.bobo.community.Util.RedisKeyUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.BitOperation;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class DataService {
  @Autowired
  RedisTemplate redisTemplate;

  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

  //记录UV的值
  public void recordUV(String ip){
    String uvKey = RedisKeyUtil.getUVKey(df.format(new Date()));
    redisTemplate.opsForHyperLogLog().add(uvKey,ip);
  }

  //统计指定区间内的UV的数据
  public long getUVData(Date startDate,Date endDate){
    //对传入区间进行判断
    if(startDate == null || endDate == null){
      throw new IllegalArgumentException("DataService传入参数有误");
    }
    //获取区间内的每一天的数据，并将每一天存入list集合中
    List<String> list = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    while (!calendar.getTime().after(endDate)){
      String uvKey = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
      list.add(uvKey);
      calendar.add(Calendar.DATE,1);
    }

    //合并区间范围内数据
    String uvKey = RedisKeyUtil.getUVKey(df.format(startDate), df.format(endDate));
    redisTemplate.opsForHyperLogLog().union(uvKey,list.toArray());

    //统计数据
    return redisTemplate.opsForHyperLogLog().size(uvKey);
  }

  //记录DAU的值
  public void recordDAU(int userId){
    String dauKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
    redisTemplate.opsForValue().setBit(dauKey,userId,true);
  }

  ////统计指定区间内的DAU的数据
  public  long getDAUData(Date startDate,Date endDate){
    //对传入区间进行判断
    if(startDate == null || endDate == null){
      throw new IllegalArgumentException("DataService传入参数有误");
    }
    //获取区间内的每一天的数据，并将每一天存入list集合中
    List<byte[]> list = new ArrayList<>();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(startDate);
    while(!calendar.getTime().after(endDate)){
      String dauKey = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
      list.add(dauKey.getBytes());
      calendar.add(Calendar.DATE,1);
    }

    //合并统计区间范围内数据
    return (long) redisTemplate.execute(new RedisCallback() {
      @Override
      public Object doInRedis(RedisConnection connection) throws DataAccessException {
        String uvKey = RedisKeyUtil.getUVKey(df.format(startDate), df.format(endDate));

        //list.toArray(new byte[0][0])——》将list转换为new byte[0][0]的数组
        //将二维数组进行or预算，存入uvKey的byte数组中
        connection.bitOp(BitOperation.OR,uvKey.getBytes(),list.toArray(new byte[0][0]));
        return connection.bitCount(uvKey.getBytes());
      }
    });
  }
}
