package com.example.ungdungbantraicay.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ungdungbantraicay.Helper.DBHelper;

public class CartDAO {

    DBHelper db;

    public CartDAO(Context context) {
        db = new DBHelper(context);
    }

    public void addToCart(int cartId, int fruitSizeId, int quantity) {
        SQLiteDatabase dbWrite = db.getWritableDatabase();
        // Kiểm tra xem món này đã có trong giỏ chưa
        Cursor cursor = db.getData("SELECT quantity FROM CartItem WHERE cart_id = " + cartId + " AND fruit_size_id = " + fruitSizeId);

        if (cursor.moveToFirst()) {
            int oldQty = cursor.getInt(0);
            dbWrite.execSQL("UPDATE CartItem SET quantity = ? WHERE cart_id = ? AND fruit_size_id = ?",
                    new Object[]{oldQty + quantity, cartId, fruitSizeId});
        } else {
            dbWrite.execSQL("INSERT INTO CartItem(cart_id, fruit_size_id, quantity) VALUES(?,?,?)",
                    new Object[]{cartId, fruitSizeId, quantity});
        }
        cursor.close();
    }

}