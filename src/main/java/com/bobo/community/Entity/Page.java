package com.bobo.community.Entity;

public class Page {
  //显示当前页码
  private int current = 1;
  //显示每页行数
  private int limit = 10;
  //显示总行数
  private int rows;
  //用于复用分页链接
  private String path;

  public int getCurrent() {
    return current;
  }

  public void setCurrent(int current) {
    if (current >= 1){
      this.current = current;
    }
  }

  public int getRows() {
    return rows;
  }

  public void setRows(int rows) {
    if (rows > 0 ) {
      this.rows = rows;
    }
  }

  public int getLimit() {
    return limit;
  }

  public void setLimit(int limit) {
    if (limit > 1 && limit < 100) {
      this.limit = limit;
    }
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public int getTotalPages() {
    return rows % limit == 0 ? rows / limit : rows / limit +1;
  }

  /**
   * 获取当前页的起始行
   * @return
   */
  public int getOffset(){
    //current * limit - limit =currentPage
    return (current - 1) * limit;
  }

  /**
   * 显示当前页数的前两页
   *
   * @return
   */
  public int getFrom(){
    int from = current - 2;
    return from < 1 ? 1 : from;
  }

  /**
   * 显示当前页数的后两页
   *
   * @return
   */
  public int getTo(){
    int to = current + 2;
    int totalPages = getTotalPages();
    return to > totalPages ? totalPages : to;
  }
}
