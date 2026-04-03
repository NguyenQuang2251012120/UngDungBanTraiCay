package com.example.ungdungbantraicay.Model;

import java.io.Serializable;

public class Review implements Serializable {
    private int id;
    private int userId;
    private int fruitId;
    private int rating; // Số sao (1-5)
    private String comment;
    private String createdAt;
    private String userName;

    public Review() {}

    public Review(int id, int userId, int fruitId, int rating, String comment, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.fruitId = fruitId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getFruitId() { return fruitId; }
    public void setFruitId(int fruitId) { this.fruitId = fruitId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
