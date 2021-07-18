package com.bobo.community.Mapper;

import com.bobo.community.Entity.LoginTicket;
import java.util.Date;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LoginTicketMapper {
  @Update({
      "update login_ticket set status = #{status} where ticket=#{ticket}"
  })
  int updateLoginTicketStatus(String ticket,int status);

  @Insert({
      "insert into login_ticket (id, user_id, ticket, status, expired) ",
      "values (#{id}, #{userId}, #{ticket}, #{status}, #{expired})"
  })
  @Options(useGeneratedKeys=true,keyProperty = "id")
  int insertLoginTicket(LoginTicket loginTicket);


  @Select({
      "select id, user_id, ticket, status, expired",
      "from login_ticket",
      "where ticket=#{ticket}"
  })
  LoginTicket selectLoginTicket(String ticket);

  @Select({
      "select id, user_id, ticket, status, expired",
      "from login_ticket",
      "where user_id=#{userId}"
  })
  LoginTicket selectLoginTicketByUserId(int userId);
}
