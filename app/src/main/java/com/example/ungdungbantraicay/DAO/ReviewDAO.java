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

    // --- MỚI: Kiểm tra xem User đã mua và nhận hàng thành công trái cây này chưa ---
    public boolean canUserReview(int userId, int fruitId) {
        // Logic: Kết nối bảng Orders -> OrderItem -> FruitSize để tìm fruit_id
        // Điều kiện: status của đơn hàng phải là STATUS_SUCCESS (3)
        String sql = "SELECT COUNT(*) FROM " + DBHelper.TABLE_ORDER + " o " +
                " JOIN " + DBHelper.TABLE_ORDER_ITEM + " oi ON o." + DBHelper.COL_ORDER_ID + " = oi." + DBHelper.COL_OI_ORDER_ID +
                " JOIN " + DBHelper.TABLE_FRUIT_SIZE + " fs ON oi." + DBHelper.COL_OI_SIZE_ID + " = fs." + DBHelper.COL_SIZE_ID +
                " WHERE o." + DBHelper.COL_ORDER_USER_ID + " = ?" +
                " AND fs." + DBHelper.COL_SIZE_FRUIT_ID + " = ?" +
                " AND o." + DBHelper.COL_ORDER_STATUS + " = " + DBHelper.STATUS_SUCCESS;

        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(userId), String.valueOf(fruitId)});
        boolean canReview = false;
        if (cursor.moveToFirst()) {
            // Nếu đếm được > 0 tức là đã có ít nhất 1 đơn hàng thành công chứa món này
            canReview = cursor.getInt(0) > 0;
        }
        cursor.close();
        return canReview;
    }

    public boolean insertReview(Review review) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_REV_USER_ID, review.getUserId());
        values.put(DBHelper.COL_REV_FRUIT_ID, review.getFruitId());
        values.put(DBHelper.COL_REV_RATING, review.getRating());
        values.put(DBHelper.COL_REV_COMMENT, review.getComment());
        // created_at sẽ tự động lấy CURRENT_TIMESTAMP từ DB nếu bạn không truyền vào

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

    public ArrayList<Review> getReviewsByFruitId(int fruitId) {
        ArrayList<Review> list = new ArrayList<>();
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
                r.setUserName(cursor.getString(6)); // Fullname từ phép JOIN trong DBHelper

                list.add(r);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    // 1. Kiểm tra xem User đã từng đánh giá trái cây này chưa
    public boolean isAlreadyReviewed(int userId, int fruitId) {
        String sql = "SELECT COUNT(*) FROM " + DBHelper.TABLE_REVIEW +
                " WHERE " + DBHelper.COL_REV_USER_ID + " = ? AND " + DBHelper.COL_REV_FRUIT_ID + " = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(userId), String.valueOf(fruitId)});
        boolean reviewed = false;
        if (cursor.moveToFirst()) {
            reviewed = cursor.getInt(0) > 0;
        }
        cursor.close();
        return reviewed;
    }

    // 2. Hàm xóa đánh giá
    public boolean deleteReview(int reviewId) {
        int rows = database.delete(DBHelper.TABLE_REVIEW,
                DBHelper.COL_REV_ID + " = ?", new String[]{String.valueOf(reviewId)});
        return rows > 0;
    }

    // Xóa đánh giá khi chỉ biết User và Trái cây (dùng trong màn hình chi tiết)
    public boolean deleteReviewByUserAndFruit(int userId, int fruitId) {
        int rows = database.delete(DBHelper.TABLE_REVIEW,
                DBHelper.COL_REV_USER_ID + " = ? AND " + DBHelper.COL_REV_FRUIT_ID + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(fruitId)});
        return rows > 0;
    }

    // Trong ReviewDAO.java
    public boolean updateReview(int reviewId, int rating, String comment) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_REV_RATING, rating);
        values.put(DBHelper.COL_REV_COMMENT, comment);
        // Lưu ý: Thường không cập nhật thời gian created_at để giữ ngày đánh giá gốc
        // hoặc bạn có thể thêm cột updated_at nếu muốn.

        int rows = database.update(DBHelper.TABLE_REVIEW, values,
                DBHelper.COL_REV_ID + " = ?", new String[]{String.valueOf(reviewId)});
        return rows > 0;
    }
}