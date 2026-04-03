package com.example.ungdungbantraicay.DAO;

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
        Cursor cursor = database.rawQuery("SELECT * FROM " + DBHelper.TABLE_CATEGORY, null);

        if (cursor.moveToFirst()) {
            do {
                Category c = new Category();
                c.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_CAT_ID)));
                c.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COL_CAT_NAME)));
                list.add(c);
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
            category = new Category();
            category.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_CAT_ID)));
            category.setName(cursor.getString(cursor.getColumnIndex(DBHelper.COL_CAT_NAME)));
        }
        cursor.close();
        return category;
    }
}