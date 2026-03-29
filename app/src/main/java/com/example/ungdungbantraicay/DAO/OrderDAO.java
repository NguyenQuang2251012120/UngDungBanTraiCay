package com.example.ungdungbantraicay.DAO;

import android.content.Context;

import com.example.ungdungbantraicay.Helper.DBHelper;

public class OrderDAO {

    DBHelper db;

    public OrderDAO(Context context) {
        db = new DBHelper(context);
    }

    public void createOrder(int userId, int totalPrice, String date) {

        String sql = "INSERT INTO Orders(user_id,total_price,status,created_at) VALUES(" +
                userId + "," + totalPrice + ",'Pending','" + date + "')";

        db.executeSQL(sql);
    }

}