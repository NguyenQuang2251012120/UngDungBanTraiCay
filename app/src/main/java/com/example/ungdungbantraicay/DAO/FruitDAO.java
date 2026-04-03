package com.example.ungdungbantraicay.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.Fruit;
import java.util.ArrayList;
import java.util.List;

public class FruitDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public FruitDAO(Context context) {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    // Lấy toàn bộ trái cây
    public List<Fruit> getAllFruits() {
        List<Fruit> list = new ArrayList<>();
        // Câu lệnh SQL lấy tất cả cột của Fruit và cộng thêm 1 cột ảo là avg_rating từ bảng Review
        String query = "SELECT f.*, " +
                "(SELECT AVG(rating) FROM Review WHERE fruit_id = f.id) as avg_rating " +
                "FROM " + DBHelper.TABLE_FRUIT + " f";

        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToFruit(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // --- HÀM MỚI: Lấy trái cây theo danh mục ---
    public List<Fruit> getFruitsByCategory(int categoryId) {
        List<Fruit> list = new ArrayList<>();
        String query = "SELECT f.*, " +
                "(SELECT AVG(rating) FROM Review WHERE fruit_id = f.id) as avg_rating " +
                "FROM " + DBHelper.TABLE_FRUIT + " f " +
                "WHERE f." + DBHelper.COL_FRUIT_CAT_ID + " = ?";

        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(categoryId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToFruit(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // Hàm phụ để tránh lặp code (Helper method)
    private Fruit cursorToFruit(Cursor cursor) {
        Fruit f = new Fruit();
        f.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_FRUIT_ID)));
        f.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COL_FRUIT_NAME)));
        f.setDescription(cursor.getString(cursor.getColumnIndex(DBHelper.COL_FRUIT_DESC)));
        f.setImage(cursor.getString(cursor.getColumnIndex(DBHelper.COL_FRUIT_IMG)));
        f.setCategoryId(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_FRUIT_CAT_ID)));
        f.setStatus(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_FRUIT_STATUS)));

        // LẤY ĐIỂM SAO: Cột avg_rating nằm ở vị trí cuối cùng trong câu SELECT trên
        int ratingIndex = cursor.getColumnIndex("avg_rating");
        if (ratingIndex != -1) {
            f.setAverageRating(cursor.getFloat(ratingIndex));
        }

        return f;
    }


    public void updateFruit(Fruit fruit) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_FRUIT_NAME, fruit.getName());
        values.put(DBHelper.COL_FRUIT_DESC, fruit.getDescription());
        database.update(DBHelper.TABLE_FRUIT, values, DBHelper.COL_FRUIT_ID + " = ?", new String[]{String.valueOf(fruit.getId())});
    }
}