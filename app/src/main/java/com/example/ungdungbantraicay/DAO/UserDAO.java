package com.example.ungdungbantraicay.DAO;

import static com.example.ungdungbantraicay.Helper.DBHelper.TABLE_USER;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.User;

import java.util.ArrayList;
import java.util.List;

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

//    public int getUserIdByUsername(String username) {
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT id FROM User WHERE username = ?", new String[]{username});
//        int id = -1;
//        if (cursor.moveToFirst()) {
//            id = cursor.getInt(0);
//        }
//        cursor.close();
//        return id;
//    }

    public String getAddressByUserId(int userId) {
        String address = "";
        Cursor cursor = database.rawQuery("SELECT address FROM User WHERE id = ?", new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) address = cursor.getString(0);
        cursor.close();
        return address;
    }
    // 1. Lấy tất cả người dùng trừ chính mình (để không tự xóa mình)
    public List<User> getAllUsersForAdmin(String currentAdminUsername) {
        List<User> list = new ArrayList<>();
        String query = "SELECT * FROM " + DBHelper.TABLE_USER + " WHERE username != ?";
        Cursor cursor = database.rawQuery(query, new String[]{currentAdminUsername});
        if (cursor.moveToFirst()) {
            do {
                list.add(cursorToUser(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    // 2. Thêm mới (Admin tạo hộ)
    public boolean insertUserAdmin(User user) {
        // RÀO CHẮN 1: Ngăn chặn dữ liệu rỗng/null
        if (user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return false;
        }

        // RÀO CHẮN 2: Kiểm tra trùng Username
        if (checkUsernameExists(user.getUsername())) return false;

        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_USER_NAME, user.getUsername().trim());
        values.put(DBHelper.COL_USER_PASS, user.getPassword().trim());
        values.put(DBHelper.COL_USER_FULLNAME, user.getFullname());
        values.put(DBHelper.COL_USER_EMAIL, user.getEmail());
        values.put(DBHelper.COL_USER_PHONE, user.getPhone());
        values.put(DBHelper.COL_USER_ADDRESS, user.getAddress());
        values.put(DBHelper.COL_USER_ROLE, user.getRole());
        values.put(DBHelper.COL_USER_STATUS, 1); // Mặc định tạo mới là Active

        return database.insert(DBHelper.TABLE_USER, null, values) > 0;
    }
    // 3. Cập nhật thông tin (Sửa, Đổi role, Khóa/Mở)
    public boolean updateUserAdmin(User user) {
        // RÀO CHẮN 3: Bảo vệ Admin tối cao (ID = 1)
        // Không cho phép đổi Role của Admin gốc thành 'user' hoặc Status thành 0 (khóa)
        if (user.getId() == 1) {
            user.setRole("admin");
            user.setStatus(1);
        }

        ContentValues v = new ContentValues();
        v.put(DBHelper.COL_USER_PASS, user.getPassword());
        v.put(DBHelper.COL_USER_FULLNAME, user.getFullname());
        v.put(DBHelper.COL_USER_EMAIL, user.getEmail());
        v.put(DBHelper.COL_USER_PHONE, user.getPhone());
        v.put(DBHelper.COL_USER_ADDRESS, user.getAddress());
        v.put(DBHelper.COL_USER_ROLE, user.getRole());
        v.put(DBHelper.COL_USER_STATUS, user.getStatus());

        return database.update(DBHelper.TABLE_USER, v, "id=?", new String[]{String.valueOf(user.getId())}) > 0;
    }
    // 4. Xóa tài khoản
    public boolean deleteUser(int id) {
        // RÀO CHẮN 4: Không bao giờ được xóa Admin gốc
        if (id == 1) return false;

        try {
            // RÀO CHẮN 5: Kiểm tra trạng thái và vai trò trước khi xóa
            String query = "SELECT " + DBHelper.COL_USER_ROLE + ", " + DBHelper.COL_USER_STATUS +
                    " FROM " + TABLE_USER + " WHERE id = ?";
            Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(id)});

            if (cursor.moveToFirst()) {
                String role = cursor.getString(0);
                int status = cursor.getInt(1);

                // Nếu là admin khác -> Không cho xóa (để tránh admin này xóa admin kia)
                if ("admin".equals(role)) {
                    cursor.close();
                    return false;
                }

                // Nếu tài khoản đang hoạt động (status = 1) -> Không cho xóa
                if (status == 1) {
                    cursor.close();
                    return false;
                }
            }
            cursor.close();

            // Thực hiện xóa nếu thỏa mãn: là 'user' VÀ status = 0 (đã bị khóa)
            return database.delete(DBHelper.TABLE_USER, "id=?", new String[]{String.valueOf(id)}) > 0;
        } catch (Exception e) {
            Log.e("UserDAO", "Error deleting user: " + e.getMessage());
            return false;
        }
    }
    // 1. Kiểm tra xem username mới đã tồn tại chưa
    public boolean checkUsernameExists(String username) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_USER +
                " WHERE " + DBHelper.COL_USER_NAME + "=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    // 2. Cập nhật thông tin (Dùng ID để làm điều kiện WHERE cho an toàn)
    public boolean updateUserWithId(User user) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_USER_NAME, user.getUsername()); // Cập nhật cả username mới
        values.put(DBHelper.COL_USER_FULLNAME, user.getFullname());
        values.put(DBHelper.COL_USER_EMAIL, user.getEmail());
        values.put(DBHelper.COL_USER_PHONE, user.getPhone());
        values.put(DBHelper.COL_USER_ADDRESS, user.getAddress());

        int rows = database.update(TABLE_USER, values, DBHelper.COL_USER_ID + "=?",
                new String[]{String.valueOf(user.getId())});
        return rows > 0;
    }

    private User cursorToUser(Cursor cursor) {
        return new User(
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


}