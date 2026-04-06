package com.example.ungdungbantraicay.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.CartItem;

import java.util.ArrayList;
import java.util.List;

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

    public List<CartItem> getItemsByUserId(int userId) {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT ci.*, f.name, f.image, fs.size, fs.price " +
                "FROM CartItem ci " +
                "JOIN Cart c ON ci.cart_id = c.id " +
                "JOIN FruitSize fs ON ci.fruit_size_id = fs.id " +
                "JOIN Fruit f ON fs.fruit_id = f.id " +
                "WHERE c.user_id = ?";

        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem();
                item.setId(cursor.getInt(0));
                item.setCartId(cursor.getInt(1));
                item.setFruitSizeId(cursor.getInt(2));
                item.setQuantity(cursor.getInt(3));

                // Lấy dữ liệu từ các bảng JOIN
                item.setFruitName(cursor.getString(4));
                item.setFruitImage(cursor.getString(5));
                item.setSizeName(cursor.getString(6));
                item.setPrice(cursor.getInt(7));

                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void updateQuantity(int itemId, int newQty) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_CI_QUANTITY, newQty);
        database.update(DBHelper.TABLE_CART_ITEM, values, "id = ?", new String[]{String.valueOf(itemId)});
    }

    public void deleteItem(int itemId) {
        database.delete(DBHelper.TABLE_CART_ITEM, "id = ?", new String[]{String.valueOf(itemId)});
    }
}