package com.example.ungdungbantraicay.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.CartItem;

public class CartDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public CartDAO(Context context) {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    // --- HÀM MỚI: Lấy ID giỏ hàng của User (Nếu chưa có thì tạo mới) ---
    public int getOrCreateCartId(int userId) {
        int cartId = -1;
        String query = "SELECT id FROM Cart WHERE user_id = ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            cartId = cursor.getInt(0);
        } else {
            // Nếu User chưa có giỏ hàng, tạo một dòng mới trong bảng Cart
            ContentValues values = new ContentValues();
            values.put("user_id", userId);
            cartId = (int) database.insert("Cart", null, values);
        }
        cursor.close();
        return cartId;
    }

    // --- HÀM CỦA BẠN: Có thêm một chút cải tiến về hiệu suất ---
    public void addToCart(CartItem item) {
        // Sử dụng hằng số từ DBHelper thay vì viết cứng tên cột để tránh gõ sai
        String query = "SELECT " + DBHelper.COL_CI_ID + ", " + DBHelper.COL_CI_QUANTITY +
                " FROM " + DBHelper.TABLE_CART_ITEM +
                " WHERE " + DBHelper.COL_CI_CART_ID + " = ? AND " + DBHelper.COL_CI_SIZE_ID + " = ?";

        String[] selectionArgs = {String.valueOf(item.getCartId()), String.valueOf(item.getFruitSizeId())};
        Cursor cursor = database.rawQuery(query, selectionArgs);

        if (cursor != null && cursor.moveToFirst()) {
            // Đã có: Cập nhật số lượng
            int oldQty = cursor.getInt(1);
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_CI_QUANTITY, oldQty + item.getQuantity());

            database.update(DBHelper.TABLE_CART_ITEM, values,
                    DBHelper.COL_CI_CART_ID + " = ? AND " + DBHelper.COL_CI_SIZE_ID + " = ?", selectionArgs);
        } else {
            // Chưa có: Chèn mới
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_CI_CART_ID, item.getCartId());
            values.put(DBHelper.COL_CI_SIZE_ID, item.getFruitSizeId());
            values.put(DBHelper.COL_CI_QUANTITY, item.getQuantity());

            database.insert(DBHelper.TABLE_CART_ITEM, null, values);
        }

        if (cursor != null) cursor.close();
    }
}