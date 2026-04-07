package com.example.ungdungbantraicay.Model;

import java.io.Serializable;

public class Order implements Serializable {

    private int id;
    private int userId;
    private int totalPrice;
    private int status;
    private String address;
    private String createdAt;


    public Order() {}

    public Order(int id, int userId, int totalPrice, int status, String address, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.address = address;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }



}