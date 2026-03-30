package com.example.ungdungbantraicay.Model;

import java.io.Serializable;

public class Cart implements Serializable {

    private int id;
    private int userId;
    private String createdAt;

    public Cart() {}

    public Cart(int id, int userId, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }

    public void setUserId(int userId) { this.userId = userId; }

    public String getCreatedAt() { return createdAt; }

    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
