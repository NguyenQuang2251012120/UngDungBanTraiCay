package com.example.ungdungbantraicay.Model;

import java.io.Serializable;

public class OrderItem implements Serializable {

    private int id;
    private int orderId;
    private int fruitSizeId;
    private int quantity;
    private int price;
    private int fruitId;   // Thêm biến này
    private String fruitName; // Thêm biến này để hiện tiêu đề Dialog
    private String fruitImage;
    private String sizeName;


    public OrderItem() {}

    public OrderItem(int id, int orderId, int fruitSizeId, int quantity, int price) {
        this.id = id;
        this.orderId = orderId;
        this.fruitSizeId = fruitSizeId;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }

    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getFruitSizeId() { return fruitSizeId; }

    public void setFruitSizeId(int fruitSizeId) { this.fruitSizeId = fruitSizeId; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getPrice() { return price; }

    public void setPrice(int price) { this.price = price; }

    public int getFruitId() { return fruitId; }
    public void setFruitId(int fruitId) { this.fruitId = fruitId; }
    public String getFruitName() { return fruitName; }

    public void setFruitName(String fruitName) { this.fruitName = fruitName; }
    public String getSizeName() { return sizeName; }
    public void setSizeName(String sizeName) { this.sizeName = sizeName; }
    public String getFruitImage() { return fruitImage; }
    public void setFruitImage(String fruitImage) { this.fruitImage = fruitImage; }
}