package com.bobo.community.Entity;

import java.util.HashMap;
import java.util.Map;

public class Event {

  private String topic;
  private int userId;
  private int entityType;
  private int entityId;
  private int authorId;
  private Map<String,Object> data = new HashMap<>();

  public String getTopic() {
    return topic;
  }

  public Event setTopic(String topic) {
    this.topic = topic;
    return this;
  }

  public int getUserId() {
    return userId;
  }

  public Event setUserId(int userId) {
    this.userId = userId;
    return this;
  }

  public int getEntityType() {
    return entityType;
  }

  public Event setEntityType(int entityType) {
    this.entityType = entityType;
    return this;
  }

  public int getEntityId() {
    return entityId;
  }

  public Event setEntityId(int entityId) {
    this.entityId = entityId;
    return this;
  }

  public int getAuthorId() {
    return authorId;
  }

  public Event setAuthorId(int authorId) {
    this.authorId = authorId;
    return this;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public Event setData(String key,Object value) {
    data.put(key,value);
    return this;
  }

  @Override
  public String toString() {
    return "Event{" +
        "topic='" + topic + '\'' +
        ", userId=" + userId +
        ", entityType=" + entityType +
        ", entityId=" + entityId +
        ", authorId=" + authorId +
        ", map=" + data +
        '}';
  }
}
