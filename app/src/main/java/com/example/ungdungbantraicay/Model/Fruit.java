package com.example.ungdungbantraicay.Model;

import java.io.Serializable;

public class Fruit implements Serializable {

    private int id;
    private String name;
    private String description;
    private String image;
    private int categoryId;

    public Fruit() {
    }

    public Fruit(int id, String name, String description, String image, int categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.categoryId = categoryId;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}