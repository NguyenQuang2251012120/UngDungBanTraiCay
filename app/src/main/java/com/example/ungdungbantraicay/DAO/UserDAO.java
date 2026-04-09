package com.example.ungdungbantraicay.DAO;

import static com.example.ungdungbantraicay.Helper.DBHelper.TABLE_USER;

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

    // CẬP NHẬT: Kiểm tra thêm status = 1 khi đăng nhập
    public User login(String username, String password) {
        User user = null;
        // Không check status ở câu SQL này để biết tài khoản có tồn tại hay không đã
        String query = "SELECT * FROM " + TABLE_USER +
                " WHERE " + DBHelper.COL_USER_NAME + "=? AND " + DBHelper.COL_USER_PASS + "=?";
        Cursor cursor = database.rawQuery(query, new String[]{username, password});

        if (cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_PASS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_FULLNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_ROLE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_STATUS))
            );
        }
        cursor.close();
        return user;
    }

    public int checkLoginStatus(String username, String password) {
        String query = "SELECT " + DBHelper.COL_USER_STATUS + " FROM " + TABLE_USER +
                " WHERE " + DBHelper.COL_USER_NAME + "=? AND " + DBHelper.COL_USER_PASS + "=?";
        Cursor cursor = database.rawQuery(query, new String[]{username, password});

        if (cursor.moveToFirst()) {
            int status = cursor.getInt(0);
            cursor.close();
            if (status == 1) return 1; // Đăng nhập thành công
            else return 0;            // Tài khoản đã bị xóa hoặc khóa
        }

        cursor.close();
        return -1; // Sai tài khoản hoặc mật khẩu (không tìm thấy)
    }


    public boolean checkUsername(String username) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_USER +
                " WHERE " + DBHelper.COL_USER_NAME + "=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists; // Tìm thấy thì trả về true -> Activity báo lỗi "Đã tồn tại"
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
        values.put(DBHelper.COL_USER_STATUS, 1); // Mặc định khi tạo mới là Active (1)

        long result = database.insert(TABLE_USER, null, values);
        return result != -1;
    }
    public boolean updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_USER_FULLNAME, user.getFullname());
        values.put(DBHelper.COL_USER_EMAIL, user.getEmail());
        values.put(DBHelper.COL_USER_PHONE, user.getPhone());
        values.put(DBHelper.COL_USER_ADDRESS, user.getAddress());

        int rows = database.update(TABLE_USER, values, DBHelper.COL_USER_NAME + "=?", new String[]{user.getUsername()});
        return rows > 0;
    }

    public boolean changePassword(String username, String newPassword) {
        ContentValues values = new ContentValues();
        // Gán mật khẩu mới vào cột Password
        values.put(DBHelper.COL_USER_PASS, newPassword);

        // Cập nhật dòng có username tương ứng
        int rows = database.update(TABLE_USER, values,
                DBHelper.COL_USER_NAME + "=?", new String[]{username});

        // Nếu có ít nhất 1 dòng được cập nhật, trả về true
        return rows > 0;
    }

    public User getUserInfo(String username) {
        User user = null;
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + DBHelper.COL_USER_NAME + "=?";
        Cursor cursor = database.rawQuery(query, new String[]{username});

        if (cursor.moveToFirst()) {
            // Sử dụng Constructor đầy đủ (9 tham số)
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_PASS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_FULLNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_ADDRESS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_ROLE)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_USER_STATUS))
            );
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

    public String getAddressByUserId(int userId) {
        String address = "";
        Cursor cursor = database.rawQuery("SELECT address FROM User WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            address = cursor.getString(0);
        }
        cursor.close();
        return address;
    }

    public void createAdminIfNotExists() { // ham tao tai khoan admin tam thoi
        Cursor cursor = database.rawQuery(
                "SELECT * FROM " + TABLE_USER + " WHERE " + DBHelper.COL_USER_NAME + "=?",
                new String[]{"admin"}
        );
        if (!cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(DBHelper.COL_USER_NAME, "admin");
            values.put(DBHelper.COL_USER_PASS, "123456");
            values.put(DBHelper.COL_USER_FULLNAME, "Administrator");
            values.put(DBHelper.COL_USER_EMAIL, "admin@gmail.com");
            values.put(DBHelper.COL_USER_PHONE, "0000000000");
            values.put(DBHelper.COL_USER_ROLE, "admin"); //
            values.put(DBHelper.COL_USER_STATUS, 1);

            database.insert(TABLE_USER, null, values);
        }

        cursor.close();
    }

    public Cursor getAllUsers() {
        return database.rawQuery("SELECT * FROM " + TABLE_USER, null);
    }

    public boolean deleteUser(int id) {
        int rows = database.delete(TABLE_USER, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean updateRole(int id, String role) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_USER_ROLE, role);
        int rows = database.update(TABLE_USER, values, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public Cursor searchUser(String keyword){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM users WHERE username LIKE ?";
        return db.rawQuery(query, new String[]{"%" + keyword + "%"});
    }
}