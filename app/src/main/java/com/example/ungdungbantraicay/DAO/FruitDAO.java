package com.example.ungdungbantraicay.DAO;

import android.content.Context;
import android.database.Cursor;

import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.Fruit;

import java.util.ArrayList;

public class FruitDAO {

    DBHelper db;

    public FruitDAO(Context context) {
        db = new DBHelper(context);
    }

    // Lấy toàn bộ trái cây
    public ArrayList<Fruit> getAllFruit() {

        ArrayList<Fruit> list = new ArrayList<>();

        Cursor cursor = db.getData("SELECT * FROM Fruit");

        while (cursor.moveToNext()) {

            Fruit fruit = new Fruit();

            fruit.setId(cursor.getInt(0));
            fruit.setName(cursor.getString(1));
            fruit.setDescription(cursor.getString(2));
            fruit.setImage(cursor.getString(3));

            list.add(fruit);
        }

        return list;
    }

    public ArrayList<Fruit> getFruitByCategory(int categoryId){

        ArrayList<Fruit> list = new ArrayList<>();

        Cursor cursor = db.getData(
                "SELECT * FROM Fruit WHERE category_id=" + categoryId
        );

        while(cursor.moveToNext()){

            Fruit fruit = new Fruit();

            fruit.setId(cursor.getInt(0));
            fruit.setName(cursor.getString(1));
            fruit.setDescription(cursor.getString(2));
            fruit.setImage(cursor.getString(3));
            fruit.setCategoryId(cursor.getInt(4));

            list.add(fruit);
        }

        return list;
    }

    // Lấy trái cây theo ID
    public Fruit getFruitById(int id) {

        Cursor cursor = db.getData("SELECT * FROM Fruit WHERE id=" + id);

        if (cursor.moveToFirst()) {

            Fruit fruit = new Fruit();

            fruit.setId(cursor.getInt(0));
            fruit.setName(cursor.getString(1));
            fruit.setDescription(cursor.getString(2));
            fruit.setImage(cursor.getString(3));
            fruit.setCategoryId(cursor.getInt(4)); // ✅ thêm dòng này

            return fruit;
        }

        return null;
    }


}