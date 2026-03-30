package com.example.ungdungbantraicay.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.User;

public class UserDAO {
    private DBHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    // Kiểm tra đăng nhập - Đã thêm đóng cursor
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM User WHERE username=? AND password=?",
                new String[]{username, password}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close(); // Quan trọng: Phải đóng để tránh leak memory
        return exists;
    }

    // Kiểm tra username tồn tại chưa
    public boolean checkUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM User WHERE username=?",
                new String[]{username}
        );
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Thêm hàm đăng ký - Chuyển sang dùng insert() để lấy kết quả trả về
    public boolean insertUser(String username, String password, String fullname,
                              String email, String phone, String address) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("fullname", fullname);
        values.put("email", email);
        values.put("phone", phone);
        values.put("address", address);
        values.put("role", "user");

        long result = db.insert("User", null, values);
        return result != -1; // Nếu result = -1 là lỗi, ngược lại là thành công
    }

    // Cập nhật thông tin - Dùng update() để biết có bao nhiêu dòng bị ảnh hưởng
    public boolean updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fullname", user.getFullname());
        values.put("email", user.getEmail());
        values.put("phone", user.getPhone());
        values.put("address", user.getAddress());

        int rows = db.update("User", values, "username=?", new String[]{user.getUsername()});
        return rows > 0; // Trả về true nếu có ít nhất 1 dòng được cập nhật
    }

    // Đổi mật khẩu
    public boolean changePassword(String username, String newPassword) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);

        int rows = db.update("User", values, "username=?", new String[]{username});
        return rows > 0;
    }

    // Lấy object User - Đã thêm xử lý đóng cursor an toàn
    public User getUserInfo(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        Cursor cursor = db.rawQuery(
                "SELECT * FROM User WHERE username=?",
                new String[]{username}
        );

        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(0),      // id
                    cursor.getString(1),   // username
                    cursor.getString(2),   // password
                    cursor.getString(3),   // fullname
                    cursor.getString(4),   // email
                    cursor.getString(5),   // phone
                    cursor.getString(6),   // address
                    cursor.getString(7)    // role
            );
            cursor.close();
        }
        return user;
    }
}