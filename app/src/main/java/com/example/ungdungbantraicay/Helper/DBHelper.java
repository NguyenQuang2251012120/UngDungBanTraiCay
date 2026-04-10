package com.example.ungdungbantraicay.Helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Tên và Phiên bản Database
    private static final String DB_NAME = "FruitShop.db";
    private static final int DB_VERSION = 6; // Tăng version lên vì thêm bảng Review

    // =============================================================
    // ĐỊNH NGHĨA TÊN BẢNG VÀ CỘT (CONSTANTS)
    // =============================================================

    // Bảng User
    public static final String TABLE_USER = "User";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "username";
    public static final String COL_USER_PASS = "password";
    public static final String COL_USER_FULLNAME = "fullname";
    public static final String COL_USER_EMAIL = "email";
    public static final String COL_USER_PHONE = "phone";
    public static final String COL_USER_ADDRESS = "address";
    public static final String COL_USER_ROLE = "role";
    public static final String COL_USER_STATUS = "status";

    // Bảng Category
    public static final String TABLE_CATEGORY = "Category";
    public static final String COL_CAT_ID = "id";
    public static final String COL_CAT_NAME = "name";
    public static final String COL_CAT_STATUS = "status";

    // Bảng Fruit
    public static final String TABLE_FRUIT = "Fruit";
    public static final String COL_FRUIT_ID = "id";
    public static final String COL_FRUIT_NAME = "name";
    public static final String COL_FRUIT_DESC = "description";
    public static final String COL_FRUIT_IMG = "image";
    public static final String COL_FRUIT_CAT_ID = "category_id";
    public static final String COL_FRUIT_STATUS = "status";

    // Bảng FruitSize
    public static final String TABLE_FRUIT_SIZE = "FruitSize";
    public static final String COL_SIZE_ID = "id";
    public static final String COL_SIZE_FRUIT_ID = "fruit_id";
    public static final String COL_SIZE_NAME = "size";
    public static final String COL_SIZE_PRICE = "price";
    public static final String COL_SIZE_STATUS = "status";

    // Bảng Review (Mới thêm)
    public static final String TABLE_REVIEW = "Review";
    public static final String COL_REV_ID = "id";
    public static final String COL_REV_USER_ID = "user_id";
    public static final String COL_REV_FRUIT_ID = "fruit_id";
    public static final String COL_REV_RATING = "rating";
    public static final String COL_REV_COMMENT = "comment";
    public static final String COL_REV_DATE = "created_at";

    // Bảng Cart (Giỏ của từng User)
    public static final String TABLE_CART = "Cart";
    public static final String COL_CART_ID = "id";
    public static final String COL_CART_USER_ID = "user_id";
    public static final String COL_CART_DATE = "created_at";

    // Bảng CartItem (Các món trong Giỏ)
    public static final String TABLE_CART_ITEM = "CartItem";
    public static final String COL_CI_ID = "id";
    public static final String COL_CI_CART_ID = "cart_id";
    public static final String COL_CI_SIZE_ID = "fruit_size_id";
    public static final String COL_CI_QUANTITY = "quantity";

    // Bảng Orders
    public static final String TABLE_ORDER = "Orders";
    public static final String COL_ORDER_ID = "id";
    public static final String COL_ORDER_USER_ID = "user_id";
    public static final String COL_ORDER_TOTAL = "total_price";
    public static final String COL_ORDER_STATUS = "status";
    public static final String COL_ORDER_ADDRESS = "delivery_address";
    public static final String COL_ORDER_DATE = "created_at";
    public static final String COL_ORDER_RECEIVER_NAME = "receiver_name";
    public static final String COL_ORDER_RECEIVER_PHONE = "receiver_phone";

    // Bảng OrderItem (Chi tiết đơn hàng)
    public static final String TABLE_ORDER_ITEM = "OrderItem";
    public static final String COL_OI_ID = "id";
    public static final String COL_OI_ORDER_ID = "order_id";
    public static final String COL_OI_SIZE_ID = "fruit_size_id";
    public static final String COL_OI_QUANTITY = "quantity";
    public static final String COL_OI_PRICE = "price";

    public static final int STATUS_PENDING = 0;   // Chờ xác nhận
    public static final int STATUS_CONFIRMED = 1; // Đã xác nhận
    public static final int STATUS_SHIPPING = 2;  // Đang giao
    public static final int STATUS_SUCCESS = 3;   // Giao thành công
    public static final int STATUS_CANCELLED = 4; // Đã hủy
    // =============================================================
    // CÂU LỆNH TẠO BẢNG (CREATE TABLE STRINGS)
    // =============================================================

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + " ("
            + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_USER_NAME + " TEXT UNIQUE, "
            + COL_USER_PASS + " TEXT, "
            + COL_USER_FULLNAME + " TEXT, "
            + COL_USER_EMAIL + " TEXT, "
            + COL_USER_PHONE + " TEXT, "
            + COL_USER_ADDRESS + " TEXT, "
            + COL_USER_ROLE + " TEXT, "
            + COL_USER_STATUS + " INTEGER DEFAULT 1)"; // Thêm cột status

    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + " ("
            + COL_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CAT_NAME + " TEXT, "
            + COL_CAT_STATUS + " INTEGER DEFAULT 1)"; // Thêm cột status

    private static final String CREATE_TABLE_FRUIT = "CREATE TABLE " + TABLE_FRUIT + " ("
            + COL_FRUIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_FRUIT_NAME + " TEXT, "
            + COL_FRUIT_DESC + " TEXT, "
            + COL_FRUIT_IMG + " TEXT, "
            + COL_FRUIT_CAT_ID + " INTEGER, "
            + COL_FRUIT_STATUS + " INTEGER DEFAULT 1, " // Thêm cột status vào đây
            + "FOREIGN KEY(" + COL_FRUIT_CAT_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COL_CAT_ID + "))";

    private static final String CREATE_TABLE_FRUIT_SIZE = "CREATE TABLE " + TABLE_FRUIT_SIZE + " ("
            + COL_SIZE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_SIZE_FRUIT_ID + " INTEGER, "
            + COL_SIZE_NAME + " TEXT, "
            + COL_SIZE_PRICE + " INTEGER, "
            + COL_SIZE_STATUS + " INTEGER DEFAULT 1, " // THÊM DÒNG NÀY
            + "FOREIGN KEY(" + COL_SIZE_FRUIT_ID + ") REFERENCES " + TABLE_FRUIT + "(" + COL_FRUIT_ID + "))";

    private static final String CREATE_TABLE_REVIEW = "CREATE TABLE " + TABLE_REVIEW + " ("
            + COL_REV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_REV_USER_ID + " INTEGER, "
            + COL_REV_FRUIT_ID + " INTEGER, "
            + COL_REV_RATING + " INTEGER, "
            + COL_REV_COMMENT + " TEXT, "
            + COL_REV_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY(" + COL_REV_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COL_USER_ID + "), "
            + "FOREIGN KEY(" + COL_REV_FRUIT_ID + ") REFERENCES " + TABLE_FRUIT + "(" + COL_FRUIT_ID + "))";


    private static final String CREATE_TABLE_CART = "CREATE TABLE " + TABLE_CART + " ("
            + COL_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CART_USER_ID + " INTEGER, "
            + COL_CART_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY(" + COL_CART_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COL_USER_ID + "))";

    private static final String CREATE_TABLE_CART_ITEM = "CREATE TABLE " + TABLE_CART_ITEM + " ("
            + COL_CI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CI_CART_ID + " INTEGER, "
            + COL_CI_SIZE_ID + " INTEGER, "
            + COL_CI_QUANTITY + " INTEGER, "
            + "FOREIGN KEY(" + COL_CI_CART_ID + ") REFERENCES " + TABLE_CART + "(" + COL_CART_ID + "), "
            + "FOREIGN KEY(" + COL_CI_SIZE_ID + ") REFERENCES " + TABLE_FRUIT_SIZE + "(" + COL_SIZE_ID + "))";

    private static final String CREATE_TABLE_ORDER = "CREATE TABLE " + TABLE_ORDER + " ("
            + COL_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_ORDER_USER_ID + " INTEGER, "
            + COL_ORDER_TOTAL + " INTEGER, "
            + COL_ORDER_STATUS + " INTEGER DEFAULT 0, "
            + COL_ORDER_ADDRESS + " TEXT, "
            + COL_ORDER_DATE + " TEXT DEFAULT CURRENT_TIMESTAMP, "
            + COL_ORDER_RECEIVER_NAME + " TEXT, " // Cột mới 1
            + COL_ORDER_RECEIVER_PHONE + " TEXT, " // Cột mới 2
            + "FOREIGN KEY(" + COL_ORDER_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COL_USER_ID + "))";

    private static final String CREATE_TABLE_ORDER_ITEM = "CREATE TABLE " + TABLE_ORDER_ITEM + " ("
            + COL_OI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_OI_ORDER_ID + " INTEGER, "
            + COL_OI_SIZE_ID + " INTEGER, "
            + COL_OI_QUANTITY + " INTEGER, "
            + COL_OI_PRICE + " INTEGER, "
            + "FOREIGN KEY(" + COL_OI_ORDER_ID + ") REFERENCES " + TABLE_ORDER + "(" + COL_ORDER_ID + "), "
            + "FOREIGN KEY(" + COL_OI_SIZE_ID + ") REFERENCES " + TABLE_FRUIT_SIZE + "(" + COL_SIZE_ID + "))";

    // =============================================================
    // HÀM KHỞI TẠO VÀ QUẢN LÝ VERSION
    // =============================================================

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo các bảng
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_FRUIT);
        db.execSQL(CREATE_TABLE_FRUIT_SIZE);
        db.execSQL(CREATE_TABLE_REVIEW);
        db.execSQL(CREATE_TABLE_CART);
        db.execSQL(CREATE_TABLE_CART_ITEM);
        db.execSQL(CREATE_TABLE_ORDER);
        db.execSQL(CREATE_TABLE_ORDER_ITEM);

        // 2. Chèn dữ liệu mẫu cho USER
        db.execSQL("INSERT INTO " + TABLE_USER + " (username, password, fullname, role) VALUES ('admin', '123456', 'Quản trị viên', 'admin')");
        db.execSQL("INSERT INTO " + TABLE_USER + " (username, password, fullname, role) VALUES ('khachhang', '123456', 'Nguyễn Văn A', 'user')");

        // 3. Chèn dữ liệu mẫu cho CATEGORY (ID sẽ tự tăng: 1, 2, 3, 4, 5)
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + " (name) VALUES ('Táo & Lê')");       // ID: 1
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + " (name) VALUES ('Trái cây có múi')"); // ID: 2
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + " (name) VALUES ('Nhiệt đới')");      // ID: 3
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + " (name) VALUES ('Dâu tây & Quả mọng')"); // ID: 4

        // 4. Chèn dữ liệu mẫu cho FRUIT
        // Táo (Category 1)
        db.execSQL("INSERT INTO " + TABLE_FRUIT + " (name, description, image, category_id) VALUES ('Táo Envy Mỹ', 'Táo Envy có độ giòn cao, ngọt đậm và thơm.', 'apple', 1)"); // ID: 1
        // Cam (Category 2)
        db.execSQL("INSERT INTO " + TABLE_FRUIT + " (name, description, image, category_id) VALUES ('Cam Sành', 'Cam sành mọng nước, vị chua ngọt tự nhiên.', 'orange', 2)"); // ID: 2
        // Chuối (Category 3)
        db.execSQL("INSERT INTO " + TABLE_FRUIT + " (name, description, image, category_id) VALUES ('Chuối Laba', 'Chuối Laba Đà Lạt thơm dẻo, giàu dinh dưỡng.', 'banana', 3)"); // ID: 3
        // Dâu (Category 4)
        db.execSQL("INSERT INTO " + TABLE_FRUIT + " (name, description, image, category_id) VALUES ('Dâu Tây Đà Lạt', 'Dâu tây đỏ mọng, vị chua thanh.', 'strawberry', 4)"); // ID: 4

        // 5. Chèn dữ liệu mẫu cho FRUIT_SIZE (Giá theo từng loại)
        // Size cho Táo (Fruit_id: 1)
        db.execSQL("INSERT INTO " + TABLE_FRUIT_SIZE + " (fruit_id, size, price) VALUES (1, 'Size Nhỏ (S)', 120000)");
        db.execSQL("INSERT INTO " + TABLE_FRUIT_SIZE + " (fruit_id, size, price) VALUES (1, 'Size Lớn (L)', 180000)");

        // Size cho Cam (Fruit_id: 2)
        db.execSQL("INSERT INTO " + TABLE_FRUIT_SIZE + " (fruit_id, size, price) VALUES (2, '1kg', 45000)");
        db.execSQL("INSERT INTO " + TABLE_FRUIT_SIZE + " (fruit_id, size, price) VALUES (2, 'Túi 5kg', 200000)");

        // Size cho Chuối (Fruit_id: 3)
        db.execSQL("INSERT INTO " + TABLE_FRUIT_SIZE + " (fruit_id, size, price) VALUES (3, 'Nải (~1.5kg)', 35000)");

        // Size cho Dâu (Fruit_id: 4)
        db.execSQL("INSERT INTO " + TABLE_FRUIT_SIZE + " (fruit_id, size, price) VALUES (4, 'Hộp 250g', 85000)");
        db.execSQL("INSERT INTO " + TABLE_FRUIT_SIZE + " (fruit_id, size, price) VALUES (4, 'Hộp 500g', 160000)");

        // 6. Chèn dữ liệu mẫu cho REVIEW
        db.execSQL("INSERT INTO " + TABLE_REVIEW + " (user_id, fruit_id, rating, comment) VALUES (2, 1, 5, 'Táo rất giòn và ngọt, giao hàng nhanh!')");
        db.execSQL("INSERT INTO " + TABLE_REVIEW + " (user_id, fruit_id, rating, comment) VALUES (2, 4, 4, 'Dâu tươi nhưng hơi chua một chút.')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEW);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRUIT_SIZE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRUIT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);

        // Tạo lại từ đầu
        onCreate(db);
    }

    // =============================================================
    // CÁC HÀM TIỆN ÍCH (HELPER METHODS)
    // =============================================================

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COL_USER_NAME + "=? AND " + COL_USER_PASS + "=?",
                new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
    // Hàm lấy danh sách đánh giá theo ID trái cây (Kèm tên User)
    public Cursor getReviewsWithUserName(int fruitId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Câu lệnh SQL JOIN để lấy fullname từ bảng User dựa vào user_id trong bảng Review
        String query = "SELECT r.*, u." + COL_USER_FULLNAME +
                " FROM " + TABLE_REVIEW + " r " +
                " JOIN " + TABLE_USER + " u ON r." + COL_REV_USER_ID + " = u." + COL_USER_ID +
                " WHERE r." + COL_REV_FRUIT_ID + " = ?" +
                " ORDER BY r." + COL_REV_ID + " DESC";

        return db.rawQuery(query, new String[]{String.valueOf(fruitId)});
    }
    public static String getStatusName(int status) {
        switch (status) {
            case DBHelper.STATUS_PENDING:   return "Chờ xác nhận";
            case DBHelper.STATUS_CONFIRMED: return "Đã xác nhận";
            case DBHelper.STATUS_SHIPPING:  return "Đang giao hàng";
            case DBHelper.STATUS_SUCCESS:   return "Giao thành công";
            case DBHelper.STATUS_CANCELLED: return "Đã hủy";
            default: return "Không xác định";
        }
    }

}