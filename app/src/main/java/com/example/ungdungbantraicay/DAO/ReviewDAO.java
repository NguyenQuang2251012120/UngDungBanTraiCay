package com.example.ungdungbantraicay.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.Review;

import java.util.ArrayList;

public class ReviewDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public ReviewDAO(Context context) {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public boolean insertReview(Review review) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_REV_USER_ID, review.getUserId());
        values.put(DBHelper.COL_REV_FRUIT_ID, review.getFruitId());
        values.put(DBHelper.COL_REV_RATING, review.getRating());
        values.put(DBHelper.COL_REV_COMMENT, review.getComment());

        long result = database.insert(DBHelper.TABLE_REVIEW, null, values);
        return result != -1;
    }

    public float getAvgRating(int fruitId) {
        String sql = "SELECT AVG(" + DBHelper.COL_REV_RATING + ") FROM " + DBHelper.TABLE_REVIEW +
                " WHERE " + DBHelper.COL_REV_FRUIT_ID + " = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(fruitId)});
        float avg = 0;
        if (cursor.moveToFirst()) avg = cursor.getFloat(0);
        cursor.close();
        return avg;
    }

    // Cập nhật hàm getReviewsByFruitId trong ReviewDAO.java
    public ArrayList<Review> getReviewsByFruitId(int fruitId) {
        ArrayList<Review> list = new ArrayList<>();
        // Gọi hàm từ DBHelper
        Cursor cursor = dbHelper.getReviewsWithUserName(fruitId);

        if (cursor.moveToFirst()) {
            do {
                Review r = new Review();
                r.setId(cursor.getInt(0));
                r.setUserId(cursor.getInt(1));
                r.setFruitId(cursor.getInt(2));
                r.setRating(cursor.getInt(3));
                r.setComment(cursor.getString(4));
                r.setCreatedAt(cursor.getString(5));

                // ĐÂY RỒI: Lấy fullname từ cột cuối cùng (do lệnh JOIN tạo ra)
                r.setUserName(cursor.getString(6));

                list.add(r);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}