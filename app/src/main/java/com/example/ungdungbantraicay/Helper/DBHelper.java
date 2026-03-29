package com.example.ungdungbantraicay.Helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "FruitShop.db";
    private static final int DB_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // =============================
        // USER
        // =============================

        String createUser = "CREATE TABLE User (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "fullname TEXT," +
                "email TEXT," +
                "phone TEXT," +
                "address TEXT," +
                "role TEXT)";

        db.execSQL(createUser);

        db.execSQL("INSERT INTO User(username,password,fullname,email,phone,address,role) VALUES(" +
                "'admin','123456','Administrator','admin@gmail.com','0123456789','HCM','admin')");

        // =============================
        // CATEGORY
        // =============================

        String createCategory = "CREATE TABLE Category (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT)";

        db.execSQL(createCategory);

        // dữ liệu mẫu
        db.execSQL("INSERT INTO Category(name) VALUES('Apple')");
        db.execSQL("INSERT INTO Category(name) VALUES('Citrus')");
        db.execSQL("INSERT INTO Category(name) VALUES('Tropical')");
        // =============================
        // FRUIT
        // =============================

        String createFruit = "CREATE TABLE Fruit (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "description TEXT," +
                "image TEXT," +
                "category_id INTEGER," +
                "FOREIGN KEY(category_id) REFERENCES Category(id))";

        db.execSQL(createFruit);

        // =============================
        // FRUIT SIZE
        // =============================

        String createFruitSize = "CREATE TABLE FruitSize (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "fruit_id INTEGER," +
                "size TEXT," +
                "price INTEGER," +
                "FOREIGN KEY(fruit_id) REFERENCES Fruit(id))";

        db.execSQL(createFruitSize);


        // =============================
        // CART
        // =============================

        String createCart = "CREATE TABLE Cart (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "created_at TEXT," +
                "FOREIGN KEY(user_id) REFERENCES User(id))";

        db.execSQL(createCart);


        // =============================
        // CART ITEM
        // =============================

        String createCartItem = "CREATE TABLE CartItem (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cart_id INTEGER," +
                "fruit_size_id INTEGER," +
                "quantity INTEGER," +
                "FOREIGN KEY(cart_id) REFERENCES Cart(id)," +
                "FOREIGN KEY(fruit_size_id) REFERENCES FruitSize(id))";

        db.execSQL(createCartItem);


        // =============================
        // ORDER
        // =============================

        String createOrder = "CREATE TABLE Orders (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER," +
                "total_price INTEGER," +
                "status TEXT," +
                "created_at TEXT," +
                "FOREIGN KEY(user_id) REFERENCES User(id))";

        db.execSQL(createOrder);


        // =============================
        // ORDER ITEM
        // =============================

        String createOrderItem = "CREATE TABLE OrderItem (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "order_id INTEGER," +
                "fruit_size_id INTEGER," +
                "quantity INTEGER," +
                "price INTEGER," +
                "FOREIGN KEY(order_id) REFERENCES Orders(id)," +
                "FOREIGN KEY(fruit_size_id) REFERENCES FruitSize(id))";

        db.execSQL(createOrderItem);



        // =============================
        // DATA SAMPLE
        // =============================

        db.execSQL("INSERT INTO Fruit(name,description,image,category_id) VALUES('Apple','Fresh apple','apple',1)");

        db.execSQL("INSERT INTO Fruit(name,description,image,category_id) VALUES('Orange','Juicy orange','orange',2)");

        db.execSQL("INSERT INTO Fruit(name,description,image,category_id) VALUES('Banana','Sweet banana','banana',3)");

        db.execSQL("INSERT INTO FruitSize(fruit_id,size,price) VALUES(1,'S',20000)");
        db.execSQL("INSERT INTO FruitSize(fruit_id,size,price) VALUES(1,'M',30000)");
        db.execSQL("INSERT INTO FruitSize(fruit_id,size,price) VALUES(1,'L',40000)");

        // Orange
        db.execSQL("INSERT INTO FruitSize(fruit_id,size,price) VALUES(2,'S',25000)");
        db.execSQL("INSERT INTO FruitSize(fruit_id,size,price) VALUES(2,'M',35000)");
        db.execSQL("INSERT INTO FruitSize(fruit_id,size,price) VALUES(2,'L',45000)");

// Banana
        db.execSQL("INSERT INTO FruitSize(fruit_id,size,price) VALUES(3,'S',15000)");
        db.execSQL("INSERT INTO FruitSize(fruit_id,size,price) VALUES(3,'M',25000)");
        db.execSQL("INSERT INTO FruitSize(fruit_id,size,price) VALUES(3,'L',35000)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS Fruit");
        db.execSQL("DROP TABLE IF EXISTS FruitSize");
        db.execSQL("DROP TABLE IF EXISTS Cart");
        db.execSQL("DROP TABLE IF EXISTS CartItem");
        db.execSQL("DROP TABLE IF EXISTS Orders");
        db.execSQL("DROP TABLE IF EXISTS OrderItem");

        onCreate(db);
    }

    // =============================
    // QUERY
    // =============================

    public Cursor getData(String sql){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(sql, null);
    }

    // =============================
    // INSERT / UPDATE / DELETE
    // =============================

    public void executeSQL(String sql){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    public boolean checkLogin(String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM User WHERE username=? AND password=?",
                new String[]{username,password}
        );
        return cursor.getCount() > 0;
    }

    public boolean checkUsername(String username){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM User WHERE username=?",
                new String[]{username}
        );

        return cursor.getCount() > 0;
    }
}