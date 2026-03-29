package com.example.ungdungbantraicay.DAO;

import android.content.Context;

import com.example.ungdungbantraicay.Helper.DBHelper;

public class CartDAO {

    DBHelper db;

    public CartDAO(Context context) {
        db = new DBHelper(context);
    }

    public void addToCart(int cartId, int fruitSizeId, int quantity) {

        String sql = "INSERT INTO CartItem(cart_id,fruit_size_id,quantity) VALUES(" +
                cartId + "," + fruitSizeId + "," + quantity + ")";

        db.executeSQL(sql);
    }

}