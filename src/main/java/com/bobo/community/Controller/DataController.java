package com.bobo.community.Controller;

import com.bobo.community.Service.DataService;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DataController {

  @Autowired
  DataService dataService;

  //返回数据统计页面
  @RequestMapping(path = "/data",method = {RequestMethod.POST,RequestMethod.GET})
  public String getDataPage(){
    return "/site/admin/data";
  }

  //统计网站独立用户
  @RequestMapping(path = "/data/uv",method = RequestMethod.POST)
  public String getUV(
      @DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
      @DateTimeFormat(pattern = "yyyy-MM-dd")Date end,
      Model model){
    long uvData = dataService.getUVData(start, end);
    model.addAttribute("resultUV",uvData);
    model.addAttribute("UVstart",start);
    model.addAttribute("UVend",end);
    return "forward:/data";
  }

  //统计网站日活跃用户
  @RequestMapping(path = "/data/dau",method = RequestMethod.POST)
  public String getDAU(
      @DateTimeFormat(pattern = "yyyy-MM-dd")Date start,
      @DateTimeFormat(pattern = "yyyy-MM-dd")Date end,
      Model model){
    long dauData = dataService.getDAUData(start, end);
    model.addAttribute("resultDAU",dauData);
    model.addAttribute("DAUstart",start);
    model.addAttribute("DAUend",end);
    return "forward:/data";
  }
}
