package com.bobo.community.Controller;

import com.bobo.community.Anonotation.LoginRequired;
import com.bobo.community.Entity.User;
import com.bobo.community.Service.FollowService;
import com.bobo.community.Service.LikeService;
import com.bobo.community.Service.UserService;
import com.bobo.community.Util.CommunityConstant;
import com.bobo.community.Util.CommunityUtil;
import com.bobo.community.Util.HostHolder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(path = "/user")
public class UserController implements CommunityConstant {

  @Value("${community.path.upload}")
  private String upload;

  @Value("${community.path.domain}")
  private String domain;

  @Value("${server.servlet.context-path}")
  private String contextPath;

  @Autowired
  UserService userService;

  @Autowired
  HostHolder hostHolder;

  @Autowired
  LikeService likeService;

  @Autowired
  FollowService followService;

  public static Logger logger = LoggerFactory.getLogger(UserController.class);

  @LoginRequired
  @RequestMapping(path = "/setting",method = RequestMethod.GET)
  public String setting(){
    return "/site/setting";
  }

  /**
   * 思路：
   *    获取文件，判断是否为null
   *    获取文件后缀名，判断是否有后缀
   *    从model中获取userid
   *    创建文件（随机码+后缀）
   *    将文件转存到配置路径下
   *    将文件响应路径响应到数据库中
   * @param model
   * @param headerImage
   * @return
   */
  @LoginRequired
  @RequestMapping(path = "/upload",method = RequestMethod.POST)
  public String uploadHeader(Model model, MultipartFile headerImage){
    if(headerImage == null){
      model.addAttribute("erro","未上传图片!");
      return "/site/setting";
    }
    String imageName = headerImage.getOriginalFilename();
    String suffix = imageName.substring(imageName.lastIndexOf("."));
    if(suffix == null){
      model.addAttribute("erro","文件格式不正确");
      return "/site/setting";
    }
    String fileName = CommunityUtil.generateUUID() + suffix;
    File dest = new File(upload + "/" +fileName);
    try {
      headerImage.transferTo(dest);
    } catch (IOException e) {
      logger.error("文件创建失败",e);
      throw new RuntimeException("文件上传失败",e);
    }
    User user = hostHolder.getUser();
    String url = domain + contextPath + "/user/header/" + fileName;
    userService.updateHeaderUrl(url,user.getId());
    return "redirect:/index";
  }

  /**
   * 文件获取思路
   * 		获取文件名
   * 		设置返回文件类型
   * 		定位到具体全限定类名下的文件
   * 		使用inputStream获取
   * 		使用outputStream输出
   * @param response
   * @param fileName
   * @return
   */
  @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
  public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
    String filename = upload + "/" + fileName;
    String suffix = filename.substring(filename.lastIndexOf("."));
    response.setContentType("image/"+suffix);
    try(
        FileInputStream is = new FileInputStream(filename );
        OutputStream os = response.getOutputStream();
        ){
      byte[] buffer= new byte[1024];
      int b = 0;
      while((b = is.read(buffer)) != -1){
        os.write(buffer,0,b);
      }
    } catch (IOException e) {
      logger.error("头像读取失败"+ e.getMessage());
    }
  }

  /**
   * 个人主页
   * @param authorId
   * @param model
   * @return
   */
  @RequestMapping(path = "/profile/{authorId}",method = RequestMethod.GET)
  public String getProfile(@PathVariable("authorId")int authorId,Model model){
    User user = userService.findUserById(authorId);
    if( user == null){
      throw new IllegalArgumentException("user/profile的user不能为空");
    }
    model.addAttribute("user",user);
    //点赞数
    int likeCount = likeService.findAuthorLikeCount(authorId);
    model.addAttribute("likeCount",likeCount);
    //关注数
    long followeeCount = followService.findFolloweeCount(user.getId(), ENTITY_TYPE_USER);
    model.addAttribute("followeeCount",followeeCount);
    //粉丝数
    long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, authorId);
    model.addAttribute("followerCount",followerCount);
    //是否已关注
    boolean hasFollowed = false;
    if(hostHolder.getUser() != null){
      hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, authorId);
    }
    model.addAttribute("hasFollowed",hasFollowed);
    return "/site/profile";
  }
}
