package com.example.ungdungbantraicay.Model;

import java.io.Serializable;

public class OrderItem implements Serializable {

    private int id;
    private int orderId;
    private int fruitSizeId;
    private int quantity;
    private int price;

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
}