package com.example.ungdungbantraicay.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public CategoryDAO(Context context) {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public List<Category> getAllCategory() {
        List<Category> list = new ArrayList<>();
        // LỌC: Chỉ lấy danh mục có status = 1
        String query = "SELECT * FROM " + DBHelper.TABLE_CATEGORY + " WHERE " + DBHelper.COL_CAT_STATUS + " = 1";
        Cursor cursor = database.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                // Sử dụng Constructor đầy đủ (ID, Name, Status)
                list.add(new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_CAT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CAT_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_CAT_STATUS))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Category getCategoryById(int id) {
        Category category = null;
        Cursor cursor = database.rawQuery("SELECT * FROM " + DBHelper.TABLE_CATEGORY +
                " WHERE " + DBHelper.COL_CAT_ID + " = ?", new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            category = new Category(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_CAT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_CAT_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_CAT_STATUS))
            );
        }
        cursor.close();
        return category;
    }


    // Bổ sung các hàm này vào CategoryDAO của bạn
    public List<Category> getAllCategoryForAdmin() {
        List<Category> list = new ArrayList<>();
        // Admin lấy tất cả, không lọc status = 1
        String query = "SELECT * FROM " + DBHelper.TABLE_CATEGORY;
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                list.add(new Category(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean insertCategory(String name) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_CAT_NAME, name);
        values.put(DBHelper.COL_CAT_STATUS, 1); // Mặc định hiện
        return database.insert(DBHelper.TABLE_CATEGORY, null, values) > 0;
    }

    public boolean updateCategory(Category category) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_CAT_NAME, category.getName());
        values.put(DBHelper.COL_CAT_STATUS, category.getStatus());
        return database.update(DBHelper.TABLE_CATEGORY, values,
                DBHelper.COL_CAT_ID + "=?", new String[]{String.valueOf(category.getId())}) > 0;
    }
}