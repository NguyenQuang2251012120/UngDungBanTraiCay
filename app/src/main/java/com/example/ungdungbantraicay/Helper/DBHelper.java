package com.example.ungdungbantraicay.Helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "FruitShop.db";
    private static final int DB_VERSION = 11; // Tăng lên 10 để làm mới toàn bộ cấu trúc

    // =============================================================
    // ĐỊNH NGHĨA TÊN BẢNG VÀ CỘT (GIỮ NGUYÊN)
    // =============================================================

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

    public static final String TABLE_CATEGORY = "Category";
    public static final String COL_CAT_ID = "id";
    public static final String COL_CAT_NAME = "name";
    public static final String COL_CAT_STATUS = "status";

    public static final String TABLE_FRUIT = "Fruit";
    public static final String COL_FRUIT_ID = "id";
    public static final String COL_FRUIT_NAME = "name";
    public static final String COL_FRUIT_DESC = "description";
    public static final String COL_FRUIT_IMG = "image";
    public static final String COL_FRUIT_CAT_ID = "category_id";
    public static final String COL_FRUIT_STATUS = "status";

    public static final String TABLE_FRUIT_SIZE = "FruitSize";
    public static final String COL_SIZE_ID = "id";
    public static final String COL_SIZE_FRUIT_ID = "fruit_id";
    public static final String COL_SIZE_NAME = "size";
    public static final String COL_SIZE_PRICE = "price";
    public static final String COL_SIZE_STATUS = "status";

    public static final String TABLE_REVIEW = "Review";
    public static final String COL_REV_ID = "id";
    public static final String COL_REV_USER_ID = "user_id";
    public static final String COL_REV_FRUIT_ID = "fruit_id";
    public static final String COL_REV_RATING = "rating";
    public static final String COL_REV_COMMENT = "comment";
    public static final String COL_REV_DATE = "created_at";

    public static final String TABLE_CART = "Cart";
    public static final String COL_CART_ID = "id";
    public static final String COL_CART_USER_ID = "user_id";
    public static final String COL_CART_DATE = "created_at";

    public static final String TABLE_CART_ITEM = "CartItem";
    public static final String COL_CI_ID = "id";
    public static final String COL_CI_CART_ID = "cart_id";
    public static final String COL_CI_SIZE_ID = "fruit_size_id";
    public static final String COL_CI_QUANTITY = "quantity";

    public static final String TABLE_ORDER = "Orders";
    public static final String COL_ORDER_ID = "id";
    public static final String COL_ORDER_USER_ID = "user_id";
    public static final String COL_ORDER_TOTAL = "total_price";
    public static final String COL_ORDER_STATUS = "status";
    public static final String COL_ORDER_ADDRESS = "delivery_address";
    public static final String COL_ORDER_DATE = "created_at";
    public static final String COL_ORDER_RECEIVER_NAME = "receiver_name";
    public static final String COL_ORDER_RECEIVER_PHONE = "receiver_phone";
    public static final String COL_ORDER_PAYMENT_METHOD = "payment_method";
    public static final String COL_USER_TOKEN = "fcmToken";

    public static final String TABLE_ORDER_ITEM = "OrderItem";
    public static final String COL_OI_ID = "id";
    public static final String COL_OI_ORDER_ID = "order_id";
    public static final String COL_OI_SIZE_ID = "fruit_size_id";
    public static final String COL_OI_QUANTITY = "quantity";
    public static final String COL_OI_PRICE = "price";

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_CONFIRMED = 1;
    public static final int STATUS_SHIPPING = 2;
    public static final int STATUS_SUCCESS = 3;
    public static final int STATUS_CANCELLED = 4;

    // =============================================================
    // CÂU LỆNH TẠO BẢNG (ĐÃ SỬA LỖI)
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
            + COL_USER_TOKEN + " TEXT, "
            + COL_USER_STATUS + " INTEGER DEFAULT 1)";

    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY + " ("
            + COL_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CAT_NAME + " TEXT, "
            + COL_CAT_STATUS + " INTEGER DEFAULT 1)"; // THÊM STATUS

    private static final String CREATE_TABLE_FRUIT = "CREATE TABLE " + TABLE_FRUIT + " ("
            + COL_FRUIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_FRUIT_NAME + " TEXT, "
            + COL_FRUIT_DESC + " TEXT, "
            + COL_FRUIT_IMG + " TEXT, "
            + COL_FRUIT_CAT_ID + " INTEGER, "
            + COL_FRUIT_STATUS + " INTEGER DEFAULT 1, " // THÊM STATUS
            + "FOREIGN KEY(" + COL_FRUIT_CAT_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COL_CAT_ID + "))";

    private static final String CREATE_TABLE_FRUIT_SIZE = "CREATE TABLE " + TABLE_FRUIT_SIZE + " ("
            + COL_SIZE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_SIZE_FRUIT_ID + " INTEGER, "
            + COL_SIZE_NAME + " TEXT, "
            + COL_SIZE_PRICE + " INTEGER, "
            + COL_SIZE_STATUS + " INTEGER DEFAULT 1, " // THÊM STATUS
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
            + COL_ORDER_RECEIVER_NAME + " TEXT, "
            + COL_ORDER_RECEIVER_PHONE + " TEXT, "
            + COL_ORDER_PAYMENT_METHOD + " INTEGER DEFAULT 0, "
            + "FOREIGN KEY(" + COL_ORDER_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COL_USER_ID + "))";

    private static final String CREATE_TABLE_ORDER_ITEM = "CREATE TABLE " + TABLE_ORDER_ITEM + " ("
            + COL_OI_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_OI_ORDER_ID + " INTEGER, "
            + COL_OI_SIZE_ID + " INTEGER, "
            + COL_OI_QUANTITY + " INTEGER, "
            + COL_OI_PRICE + " INTEGER, "
            + "FOREIGN KEY(" + COL_OI_ORDER_ID + ") REFERENCES " + TABLE_ORDER + "(" + COL_ORDER_ID + "), "
            + "FOREIGN KEY(" + COL_OI_SIZE_ID + ") REFERENCES " + TABLE_FRUIT_SIZE + "(" + COL_SIZE_ID + "))";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_FRUIT);
        db.execSQL(CREATE_TABLE_FRUIT_SIZE);
        db.execSQL(CREATE_TABLE_REVIEW);
        db.execSQL(CREATE_TABLE_CART);
        db.execSQL(CREATE_TABLE_CART_ITEM);
        db.execSQL(CREATE_TABLE_ORDER);
        db.execSQL(CREATE_TABLE_ORDER_ITEM);

        // Chèn dữ liệu mẫu (Giữ nguyên phần Insert của bạn)
        insertSampleData(db);
    }

    private void insertSampleData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_USER + " (username, password, fullname, role) VALUES ('admin', '123456', 'Quản trị viên', 'admin')");
        db.execSQL("INSERT INTO " + TABLE_USER + " (username, password, fullname, role) VALUES ('khachhang', '123456', 'Nguyễn Văn A', 'user')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + " (name) VALUES ('Táo & Lê')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + " (name) VALUES ('Trái cây có múi')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + " (name) VALUES ('Nhiệt đới')");
        db.execSQL("INSERT INTO " + TABLE_CATEGORY + " (name) VALUES ('Dâu tây & Quả mọng')");
        db.execSQL("INSERT INTO " + TABLE_FRUIT + " (name, description, image, category_id) VALUES ('Táo Envy Mỹ', 'Táo Envy có độ giòn cao, ngọt đậm và thơm.', 'apple', 1)");
        db.execSQL("INSERT INTO " + TABLE_FRUIT + " (name, description, image, category_id) VALUES ('Cam Sành', 'Cam sành mọng nước, vị chua ngọt tự nhiên.', 'orange', 2)");
        db.execSQL("INSERT INTO " + TABLE_FRUIT + " (name, description, image, category_id) VALUES ('Chuối Laba', 'Chuối Laba Đà Lạt thơm dẻo, giàu dinh dưỡng.', 'banana', 3)");
        db.execSQL("INSERT INTO " + TABLE_FRUIT + " (name, description, image, category_id) VALUES ('Dâu Tây Đà Lạt', 'Dâu tây đỏ mọng, vị chua thanh.', 'strawberry', 4)");
        db.execSQL("INSERT INTO " + TABLE_FRUIT_SIZE + " (fruit_id, size, price) VALUES (1, 'Size Nhỏ (S)', 120000)");
        db.execSQL("INSERT INTO " + TABLE_FRUIT_SIZE + " (fruit_id, size, price) VALUES (1, 'Size Lớn (L)', 180000)");
        db.execSQL("INSERT INTO " + TABLE_FRUIT_SIZE + " (fruit_id, size, price) VALUES (2, '1kg', 45000)");
        db.execSQL("INSERT INTO " + TABLE_FRUIT_SIZE + " (fruit_id, size, price) VALUES (3, 'Nải (~1.5kg)', 35000)");
        db.execSQL("INSERT INTO " + TABLE_FRUIT_SIZE + " (fruit_id, size, price) VALUES (4, 'Hộp 250g', 85000)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEW);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRUIT_SIZE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FRUIT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    // =============================================================
    // HELPER METHODS (GIỮ NGUYÊN)
    // =============================================================

    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COL_USER_NAME + "=? AND " + COL_USER_PASS + "=?",
                new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public static String getStatusName(int status) {
        switch (status) {
            case STATUS_PENDING:   return "Chờ xác nhận";
            case STATUS_CONFIRMED: return "Đã xác nhận";
            case STATUS_SHIPPING:  return "Đang giao hàng";
            case STATUS_SUCCESS:   return "Giao thành công";
            case STATUS_CANCELLED: return "Đã hủy";
            default: return "Không xác định";
        }
    }

    // Hàm lấy danh sách đánh giá kèm theo tên đầy đủ của người dùng (JOIN bảng User)
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
}