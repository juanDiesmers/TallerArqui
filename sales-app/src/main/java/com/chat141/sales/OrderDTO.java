package com.chat141.sales;

import java.util.List;
import java.math.BigDecimal;

public class OrderDTO {
  public Long customerId;
  public List<Item> items;

  public static class Item {
    public Long productId;
    public int qty;
    public BigDecimal unitPrice;
  }
}
