package com.example.ungdungbantraicay.DAO;

import android.content.Context;
import android.database.Cursor;

import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.FruitSize;

import java.util.ArrayList;

public class FruitSizeDAO {

    DBHelper db;

    public FruitSizeDAO(Context context) {
        db = new DBHelper(context);
    }

    public ArrayList<FruitSize> getSizeByFruitId(int fruitId){

        ArrayList<FruitSize> list = new ArrayList<>();

        Cursor cursor = db.getData(
                "SELECT * FROM FruitSize WHERE fruit_id=" + fruitId
        );

        while(cursor.moveToNext()){

            FruitSize size = new FruitSize();

            size.setId(cursor.getInt(0));
            size.setFruitId(cursor.getInt(1));
            size.setSize(cursor.getString(2));
            size.setPrice(cursor.getInt(3));

            list.add(size);
        }

        return list;
    }
}