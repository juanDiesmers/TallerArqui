package com.chat141.sales;

import java.io.Serializable;

public class OrderEvent implements Serializable {
  private String type;
  private Long orderId;
  public OrderEvent() {}
  public OrderEvent(String type, Long orderId) { this.type = type; this.orderId = orderId; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public Long getOrderId() { return orderId; }
  public void setOrderId(Long orderId) { this.orderId = orderId; }
}
