package com.example.ungdungbantraicay.DAO;

import android.content.ContentValues;
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
    }

    // 1. Lấy tất cả size của 1 trái cây (Admin xem cả size ẩn)
    public List<FruitSize> getSizesByFruitIdAdmin(int fruitId) {
        List<FruitSize> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + DBHelper.TABLE_FRUIT_SIZE +
                " WHERE " + DBHelper.COL_SIZE_FRUIT_ID + " = ?", new String[]{String.valueOf(fruitId)});

        if (cursor.moveToFirst()) {
            do {
                list.add(new FruitSize(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getInt(4)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 2. Thêm size mới
    public long insertSize(FruitSize fs) {
        ContentValues v = new ContentValues();
        v.put(DBHelper.COL_SIZE_FRUIT_ID, fs.getFruitId());
        v.put(DBHelper.COL_SIZE_NAME, fs.getSize());
        v.put(DBHelper.COL_SIZE_PRICE, fs.getPrice());
        v.put(DBHelper.COL_SIZE_STATUS, fs.getStatus());
        return database.insert(DBHelper.TABLE_FRUIT_SIZE, null, v);
    }

    // 3. Cập nhật size
    public boolean updateSize(FruitSize fs) {
        ContentValues v = new ContentValues();
        v.put(DBHelper.COL_SIZE_NAME, fs.getSize());
        v.put(DBHelper.COL_SIZE_PRICE, fs.getPrice());
        v.put(DBHelper.COL_SIZE_STATUS, fs.getStatus());
        return database.update(DBHelper.TABLE_FRUIT_SIZE, v, "id=?", new String[]{String.valueOf(fs.getId())}) > 0;
    }

    // 4. Xóa size
    public boolean deleteSize(int id) {
        return database.delete(DBHelper.TABLE_FRUIT_SIZE, "id=?", new String[]{String.valueOf(id)}) > 0;
    }
}


