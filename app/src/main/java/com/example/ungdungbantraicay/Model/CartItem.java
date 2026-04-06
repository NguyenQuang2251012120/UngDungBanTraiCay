package com.example.ungdungbantraicay.Model;

import java.io.Serializable;

public class CartItem implements Serializable {

    private int id;
    private int cartId;
    private int fruitSizeId;
    private int quantity;
    private String fruitName;
    private String fruitImage;
    private String sizeName;
    private int price;

    public CartItem() {}

    public CartItem(int id, int cartId, int fruitSizeId, int quantity) {
        this.id = id;
        this.cartId = cartId;
        this.fruitSizeId = fruitSizeId;
        this.quantity = quantity;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getCartId() { return cartId; }

    public void setCartId(int cartId) { this.cartId = cartId; }

    public int getFruitSizeId() { return fruitSizeId; }

    public void setFruitSizeId(int fruitSizeId) { this.fruitSizeId = fruitSizeId; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getFruitName() { return fruitName; }
    public void setFruitName(String fruitName) { this.fruitName = fruitName; }
    public String getFruitImage() { return fruitImage; }
    public void setFruitImage(String fruitImage) { this.fruitImage = fruitImage; }
    public String getSizeName() { return sizeName; }
    public void setSizeName(String sizeName) { this.sizeName = sizeName; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}
