package com.example.ungdungbantraicay.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.User;

public class UserDAO {

    DBHelper dbHelper;

    public UserDAO(Context context){
        dbHelper = new DBHelper(context);
    }

    public boolean checkLogin(String username, String password){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM User WHERE username=? AND password=?",
                new String[]{username,password}
        );

        return cursor.getCount() > 0;
    }

    public boolean checkUsername(String username){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM User WHERE username=?",
                new String[]{username}
        );

        return cursor.getCount() > 0;
    }

    // thêm hàm đăng ký
    public void insertUser(String username,String password,String fullname,
                           String email,String phone,String address){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(
                "INSERT INTO User(username,password,fullname,email,phone,address,role) VALUES(?,?,?,?,?,?,?)",
                new Object[]{username,password,fullname,email,phone,address,"user"}
        );
    }
    public Cursor getUserByUsername(String username){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM User WHERE username=?",
                new String[]{username}
        );

        return cursor;
    }
    public boolean updateUser(User user){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(
                "UPDATE User SET fullname=?, email=?, phone=?, address=? WHERE username=?",
                new Object[]{
                        user.getFullname(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getAddress(),
                        user.getUsername()
                }
        );

        return true;
    }
    public boolean changePassword(String username,String newPassword){

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL(
                "UPDATE User SET password=? WHERE username=?",
                new Object[]{newPassword,username}
        );

        return true;
    }


    public User getUserInfo(String username){

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM User WHERE username=?",
                new String[]{username}
        );

        if(cursor.moveToFirst()){

            User user = new User(
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
            return user;
        }

        cursor.close();
        return null;
    }



}