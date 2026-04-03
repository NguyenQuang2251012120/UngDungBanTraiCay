package com.example.ungdungbantraicay.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.User;

public class UserDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public UserDAO(Context context) {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public boolean checkLogin(String username, String password) {
        String query = "SELECT * FROM " + DBHelper.TABLE_USER + " WHERE " + DBHelper.COL_USER_NAME + "=? AND " + DBHelper.COL_USER_PASS + "=?";
        Cursor cursor = database.rawQuery(query, new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean checkUsername(String username) {
        String query = "SELECT * FROM " + DBHelper.TABLE_USER + " WHERE " + DBHelper.COL_USER_NAME + "=?";
        Cursor cursor = database.rawQuery(query, new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_USER_NAME, user.getUsername());
        values.put(DBHelper.COL_USER_PASS, user.getPassword());
        values.put(DBHelper.COL_USER_FULLNAME, user.getFullname());
        values.put(DBHelper.COL_USER_EMAIL, user.getEmail());
        values.put(DBHelper.COL_USER_PHONE, user.getPhone());
        values.put(DBHelper.COL_USER_ADDRESS, user.getAddress());
        values.put(DBHelper.COL_USER_ROLE, "user");

        long result = database.insert(DBHelper.TABLE_USER, null, values);
        return result != -1;
    }

    public boolean updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_USER_FULLNAME, user.getFullname());
        values.put(DBHelper.COL_USER_EMAIL, user.getEmail());
        values.put(DBHelper.COL_USER_PHONE, user.getPhone());
        values.put(DBHelper.COL_USER_ADDRESS, user.getAddress());

        int rows = database.update(DBHelper.TABLE_USER, values, DBHelper.COL_USER_NAME + "=?", new String[]{user.getUsername()});
        return rows > 0;
    }

    public boolean changePassword(String username, String newPassword) {
        ContentValues values = new ContentValues();
        // Gán mật khẩu mới vào cột Password
        values.put(DBHelper.COL_USER_PASS, newPassword);

        // Cập nhật dòng có username tương ứng
        int rows = database.update(DBHelper.TABLE_USER, values,
                DBHelper.COL_USER_NAME + "=?", new String[]{username});

        // Nếu có ít nhất 1 dòng được cập nhật, trả về true
        return rows > 0;
    }

    public User getUserInfo(String username) {
        User user = null;
        String query = "SELECT * FROM " + DBHelper.TABLE_USER + " WHERE " + DBHelper.COL_USER_NAME + "=?";
        Cursor cursor = database.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndex(DBHelper.COL_USER_NAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(DBHelper.COL_USER_PASS)));
            user.setFullname(cursor.getString(cursor.getColumnIndex(DBHelper.COL_USER_FULLNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(DBHelper.COL_USER_EMAIL)));
            user.setPhone(cursor.getString(cursor.getColumnIndex(DBHelper.COL_USER_PHONE)));
            user.setAddress(cursor.getString(cursor.getColumnIndex(DBHelper.COL_USER_ADDRESS)));
            user.setRole(cursor.getString(cursor.getColumnIndex(DBHelper.COL_USER_ROLE)));
        }
        cursor.close();
        return user;
    }

    public int getUserIdByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM User WHERE username = ?", new String[]{username});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }
}