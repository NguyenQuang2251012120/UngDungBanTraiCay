package com.example.ungdungbantraicay.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.FruitSize;

import java.util.ArrayList;
import java.util.List;

public class FruitSizeDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public FruitSizeDAO(Context context) {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public List<FruitSize> getSizesByFruitId(int fruitId) {
        List<FruitSize> list = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.TABLE_FRUIT_SIZE, null,
                DBHelper.COL_SIZE_FRUIT_ID + " = ?",
                new String[]{String.valueOf(fruitId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                FruitSize s = new FruitSize();
                s.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_SIZE_ID)));
                s.setSize(cursor.getString(cursor.getColumnIndex(DBHelper.COL_SIZE_NAME)));
                s.setPrice(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_SIZE_PRICE)));

                // LẤY TRẠNG THÁI TỪ DATABASE
                int statusIndex = cursor.getColumnIndex("status"); // Hoặc dùng biến hằng số
                if (statusIndex != -1) {
                    s.setStatus(cursor.getInt(statusIndex));
                }
                list.add(s);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}