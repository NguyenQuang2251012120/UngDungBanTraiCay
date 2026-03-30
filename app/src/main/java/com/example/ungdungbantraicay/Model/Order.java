package com.example.ungdungbantraicay.Model;

import java.io.Serializable;

public class Order implements Serializable {

    private int id;
    private int userId;
    private int totalPrice;
    private String status;
    private String createdAt;

    public Order() {}

    public Order(int id, int userId, int totalPrice, String status, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public int getTotalPrice() { return totalPrice; }

    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}