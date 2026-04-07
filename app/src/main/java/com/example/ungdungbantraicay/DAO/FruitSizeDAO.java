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
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM FruitSize WHERE fruit_id = ? AND status = 1",
                new String[]{String.valueOf(fruitId)});

        if (cursor.moveToFirst()) {
            do {
                list.add(new FruitSize(
                        cursor.getInt(0), // id
                        cursor.getInt(1), // fruitId
                        cursor.getString(2), // size
                        cursor.getInt(3), // price
                        cursor.getInt(4)  // status -> Thêm dòng này vào constructor
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }}