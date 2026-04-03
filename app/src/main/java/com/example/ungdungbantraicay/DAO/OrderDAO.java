package com.example.ungdungbantraicay.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.Order;

public class OrderDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public OrderDAO(Context context) {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public long createOrder(Order order) {
        ContentValues values = new ContentValues();
        values.put("user_id", order.getUserId());
        values.put("total_price", order.getTotalPrice());
        values.put("status", order.getStatus()); // Ví dụ: "Pending"
        values.put("created_at", order.getCreatedAt());

        // Trả về ID của đơn hàng vừa tạo (rất quan trọng để lưu OrderItem sau đó)
        return database.insert("Orders", null, values);
    }
}