package com.bobo.community.Util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 前缀树思路
 * 	1、在内存中生成过滤用的前缀树
 * 		获取待过滤文件
 * 		获取文件中的敏感词
 * 		生成过滤树
 * 	2、将传入字符串进行过滤
 * 		定义三个指针
 * 		定义结果
 * 		考虑是否是符号
 * 		判断是否是敏感词
 * 			是替换					    到了叶子结点，sb直加
 * 			不是，发现疑似			begin不动，position动
 * 			不是，发现没一个匹配 	stringbuilder直加
 * 		将疑似而到达最后的存入StringBuilder并返回
 * 		将疑似而到达最后的存入StringBuilder并返回
 * 	3、前缀树数据结构
 * 		能有子节点
 * 		最后一个节点需要有标记
 */
@Component
public class SensitiveFilterUtil {


  private static final Logger logger = LoggerFactory.getLogger(SensitiveFilterUtil.class);

  //定义转换后的敏感词为 ***
  private String REPLACEMENT = "***";

  //定义根节点
  private TrieNode rootNode = new TrieNode();

  //获取敏感词文件，读出敏感词，构建前缀树
  @PostConstruct
  public void init(){
    try (
        //获取文件
        InputStream is = this.getClass().getClassLoader()
            .getResourceAsStream("sensitive-words.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ){
      String keyword;
      //获取文件中的内容
      while((keyword = reader.readLine()) != null){
        //添加到前缀树中
        this.addKeyWords(keyword);
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  //定义添加到前缀树的方法，在内存中生成前缀树
  private void addKeyWords( String keyword){
    TrieNode tmpTrieNode = rootNode;
    for(int i = 0; i< keyword.length(); i++){
      Character c = keyword.charAt(i);
      TrieNode subTrieNode = tmpTrieNode.getSubTredNode(c);
      if( subTrieNode == null){
        subTrieNode = new TrieNode();
        tmpTrieNode.setSubTredNode(c,subTrieNode);
      }
      tmpTrieNode = subTrieNode;
      if(i == keyword.length()-1){
        tmpTrieNode.setKeywordEnd(true);
      }
    }
  }

  /**
   * 将敏感词进行转换
   * @param text 待过滤的文字
   * @return     过滤完的文字
   */
  public String filter(String text){
    if(text == null){
      return null;
    }
    //定义整理后的数据容器
    StringBuilder sb = new StringBuilder();
    //定义指向root的指针
    TrieNode tmpNode = rootNode;
    //定义指向begin的指针
    int begin = 0;
    //定义指向begin下一个的指针，该指针作为循环遍历时的原点
    int position = 0;
    while(position < text.length()){
      Character c = text.charAt(position);
      if(isSymbol(c)){
        if(tmpNode == rootNode){
          sb.append(c);
          begin ++;
        }
        position++;
        continue;
      }
      tmpNode = tmpNode.getSubTredNode(c);
      if(tmpNode == null){
        sb.append(c);
        position = ++begin;
        tmpNode = rootNode;
      } else if(tmpNode.isKeywordEnd()){
        sb.append(REPLACEMENT);
        begin = ++position;
        tmpNode = rootNode;
      } else {
        position ++;
      }
    }
    sb.append(text.substring(begin));
    return sb.toString();
  }

  // 判断是否为符号
  private boolean isSymbol(Character c) {
    // 0x2E80~0x9FFF 是东亚文字范围
    return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
  }

  //定义前缀树
  private class TrieNode {
    //定义是否为叶子节点
    private boolean isKeywordEnd=false;
    //定义子节点
    private Map<Character, TrieNode> subTrieNode = new HashMap<>();

    //定义获取子节点的方法
    private TrieNode getSubTredNode(Character c){
      return subTrieNode.get(c);
    }
    //定义设置子节点的方法
    private void setSubTredNode(Character c, TrieNode t){
      subTrieNode.put(c,t);
    }

    public boolean isKeywordEnd() {
      return isKeywordEnd;
    }

    public void setKeywordEnd(boolean keywordEnd) {
      isKeywordEnd = keywordEnd;
    }
  }

}
