package com.example.ungdungbantraicay.Model;

import java.io.Serializable;

public class CartItem implements Serializable {

    private int id;
    private int cartId;
    private int fruitSizeId;
    private int quantity;

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
}
