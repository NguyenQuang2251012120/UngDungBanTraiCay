package com.example.ungdungbantraicay.Model;

import java.io.Serializable;

public class FruitSize implements Serializable {

    private int id;
    private int fruitId;
    private String size;
    private int price;
    private int quantity = 1;
    private int status;

    public FruitSize() {}

    // Constructor đầy đủ để dùng trong DAO
    public FruitSize(int id, int fruitId, String size, int price, int status) {
        this.id = id;
        this.fruitId = fruitId;
        this.size = size;
        this.price = price;
        this.status = status;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getFruitId() { return fruitId; }

    public void setFruitId(int fruitId) { this.fruitId = fruitId; }

    public String getSize() { return size; }

    public void setSize(String size) { this.size = size; }

    public int getPrice() { return price; }

    public void setPrice(int price) { this.price = price; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }
}