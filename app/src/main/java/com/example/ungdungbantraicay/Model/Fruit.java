package com.example.ungdungbantraicay.Model;

import java.io.Serializable;

public class Fruit implements Serializable {

    private int id;
    private String name;
    private String description;
    private String image;
    private int categoryId;
    private int status; // 1: Còn hàng, 0: Hết hàng
    private float averageRating;
    private int minPrice; // Thêm thuộc tính này

    public Fruit() {
    }

    public Fruit(int id, String name, String description, String image, int categoryId, int status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.categoryId = categoryId;
        this.status = status;
    }

    // Getter và Setter cho status
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    // Các Getter và Setter cũ giữ nguyên
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    // Hàm bổ trợ kiểm tra nhanh
    public boolean isAvailable() {
        return this.status == 1;
    }

    public float getAverageRating() { return averageRating; }
    public void setAverageRating(float averageRating) { this.averageRating = averageRating; }
    public int getMinPrice() { return minPrice; }
    public void setMinPrice(int minPrice) { this.minPrice = minPrice; }
}