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

    // --- 1. Lấy toàn bộ trái cây ---
    public List<Fruit> getAllFruits() {
        List<Fruit> list = new ArrayList<>();
        // TỐI ƯU: Chỉ lấy giá MIN của những size còn hàng (status = 1)
        String query = "SELECT f.*, " +
                "(SELECT AVG(rating) FROM Review WHERE fruit_id = f.id) as avg_rating, " +
                "(SELECT MIN(price) FROM FruitSize WHERE fruit_id = f.id AND status = 1) as min_price " +
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

    // --- 2. Lấy trái cây theo Category ---
    public List<Fruit> getFruitsByCategory(int categoryId) {
        List<Fruit> list = new ArrayList<>();
        // TỐI ƯU: Chỉ lấy giá MIN của những size còn hàng (status = 1)
        String query = "SELECT f.*, " +
                "(SELECT AVG(rating) FROM Review WHERE fruit_id = f.id) as avg_rating, " +
                "(SELECT MIN(price) FROM FruitSize WHERE fruit_id = f.id AND status = 1) as min_price " +
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

    // --- 3. Hàm tìm kiếm và lọc nâng cao ---
    public List<Fruit> searchFruits(String query, int categoryId, String sortType) {
        List<Fruit> list = new ArrayList<>();

        // TỐI ƯU: Subquery giá MIN chỉ xét các size có status = 1
        String sql = "SELECT f.*, " +
                "(SELECT AVG(rating) FROM Review WHERE fruit_id = f.id) as avg_rating, " +
                "(SELECT MIN(price) FROM FruitSize WHERE fruit_id = f.id AND status = 1) as min_price " +
                "FROM Fruit f " +
                "WHERE f.name LIKE ?";

        List<String> args = new ArrayList<>();
        args.add("%" + query + "%");

        if (categoryId > 0) {
            sql += " AND f.category_id = ?";
            args.add(String.valueOf(categoryId));
        }

        // Sắp xếp dựa trên min_price đã lọc status = 1
        if (sortType.equals("PRICE_ASC")) {
            sql += " ORDER BY min_price ASC";
        } else if (sortType.equals("PRICE_DESC")) {
            sql += " ORDER BY min_price DESC";
        } else if (sortType.equals("RATING")) {
            sql += " ORDER BY avg_rating DESC";
        }

        Cursor cursor = database.rawQuery(sql, args.toArray(new String[0]));
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToFruit(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private Fruit cursorToFruit(Cursor cursor) {
        // 1. Lấy dữ liệu cơ bản từ các cột tương ứng trong DBHelper
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_FRUIT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_FRUIT_NAME));
        String desc = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_FRUIT_DESC));
        String img = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_FRUIT_IMG));
        int catId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_FRUIT_CAT_ID));
        int status = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_FRUIT_STATUS));

        // 2. Lấy Average Rating (từ Subquery trong SQL)
        float rating = 0;
        int ratingIndex = cursor.getColumnIndex("avg_rating");
        if (ratingIndex != -1) {
            rating = cursor.getFloat(ratingIndex);
        }

        // 3. Khởi tạo đối tượng bằng Constructor mới (có Rating, không có MinPrice)
        Fruit f = new Fruit(id, name, desc, img, catId, status, rating);

        // 4. Set MinPrice riêng (nếu câu query có trả về cột min_price)
        int priceIndex = cursor.getColumnIndex("min_price");
        if (priceIndex != -1) {
            f.setMinPrice(cursor.getInt(priceIndex));
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