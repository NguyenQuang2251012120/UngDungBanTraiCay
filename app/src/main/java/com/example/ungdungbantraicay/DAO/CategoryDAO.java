package com.example.ungdungbantraicay.DAO;

import android.content.Context;
import android.database.Cursor;

import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.Category;

import java.util.ArrayList;

public class CategoryDAO {

    DBHelper db;

    public CategoryDAO(Context context) {
        db = new DBHelper(context);
    }

    public ArrayList<Category> getAllCategory(){

        ArrayList<Category> list = new ArrayList<>();

        Cursor cursor = db.getData("SELECT * FROM Category");

        while(cursor.moveToNext()){

            Category c = new Category();

            c.setId(cursor.getInt(0));
            c.setName(cursor.getString(1));

            list.add(c);
        }

        return list;
    }

    // =============================
    // LẤY CATEGORY THEO ID
    // =============================

    public Category getCategoryById(int id){

        Category category = null;

        Cursor cursor = db.getData(
                "SELECT * FROM Category WHERE id=" + id
        );

        if(cursor.moveToFirst()){

            category = new Category();

            category.setId(cursor.getInt(0));
            category.setName(cursor.getString(1));
        }

        return category;
    }
}