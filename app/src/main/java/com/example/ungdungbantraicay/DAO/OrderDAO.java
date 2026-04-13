package com.example.ungdungbantraicay.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.CartItem;
import com.example.ungdungbantraicay.Model.Order;
import com.example.ungdungbantraicay.Model.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public OrderDAO(Context context) {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    // HÀM QUAN TRỌNG: Thực hiện đặt hàng
    // CẬP NHẬT: Thêm tham số paymentMethod
    public boolean placeOrder(int userId, int totalPrice, String address, String name, String phone, int paymentMethod, List<CartItem> items) {
        database.beginTransaction();
        try {
            ContentValues orderValues = new ContentValues();
            orderValues.put(DBHelper.COL_ORDER_USER_ID, userId);
            orderValues.put(DBHelper.COL_ORDER_TOTAL, totalPrice);

            // Nếu trả MoMo (1) thì trạng thái là Đã xác nhận (1), Tiền mặt (0) là Chờ xác nhận (0)
            int status = (paymentMethod == 1) ? 1 : 0;
            orderValues.put(DBHelper.COL_ORDER_STATUS, status);

            orderValues.put(DBHelper.COL_ORDER_ADDRESS, address);
            orderValues.put(DBHelper.COL_ORDER_RECEIVER_NAME, name);
            orderValues.put(DBHelper.COL_ORDER_RECEIVER_PHONE, phone);
            orderValues.put(DBHelper.COL_ORDER_PAYMENT_METHOD, paymentMethod); // Lưu phương thức thanh toán

            long orderId = database.insert(DBHelper.TABLE_ORDER, null, orderValues);
            if (orderId == -1) return false;

            // Lưu chi tiết sản phẩm
            for (CartItem item : items) {
                ContentValues detailValues = new ContentValues();
                detailValues.put(DBHelper.COL_OI_ORDER_ID, (int) orderId);
                detailValues.put(DBHelper.COL_OI_SIZE_ID, item.getFruitSizeId());
                detailValues.put(DBHelper.COL_OI_QUANTITY, item.getQuantity());
                detailValues.put(DBHelper.COL_OI_PRICE, item.getPrice());
                database.insert(DBHelper.TABLE_ORDER_ITEM, null, detailValues);
            }

            // Xóa giỏ hàng sau khi đặt thành công
            String getCartIdSql = "SELECT id FROM Cart WHERE user_id = ?";
            Cursor cursor = database.rawQuery(getCartIdSql, new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                int cartId = cursor.getInt(0);
                database.delete(DBHelper.TABLE_CART_ITEM, "cart_id = ?", new String[]{String.valueOf(cartId)});
            }
            cursor.close();

            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            database.endTransaction();
        }
    }
    // 2. Sửa hàm lấy danh sách đơn hàng
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM " + DBHelper.TABLE_ORDER + " WHERE user_id = ? ORDER BY id DESC";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_ID)));
                order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_USER_ID)));
                order.setTotalPrice(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_TOTAL)));
                order.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_STATUS)));
                order.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_ADDRESS)));
                order.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_DATE)));
                order.setReceiverName(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_RECEIVER_NAME)));
                order.setReceiverPhone(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_RECEIVER_PHONE)));
                // ĐỌC CỘT PHƯƠNG THỨC THANH TOÁN
                order.setPaymentMethod(cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COL_ORDER_PAYMENT_METHOD)));
                list.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<OrderItem> getOrderDetails(int orderId) {
        List<OrderItem> list = new ArrayList<>();
        // Thêm f.id vào SELECT để phục vụ việc đánh giá
        String sql = "SELECT oi.*, f.id AS fruitId, f.name, f.image, fs.size " +
                "FROM " + DBHelper.TABLE_ORDER_ITEM + " oi " +
                "JOIN " + DBHelper.TABLE_FRUIT_SIZE + " fs ON oi.fruit_size_id = fs.id " +
                "JOIN " + DBHelper.TABLE_FRUIT + " f ON fs.fruit_id = f.id " +
                "WHERE oi.order_id = ?";

        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(orderId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                OrderItem item = new OrderItem();

                // Lấy dữ liệu an toàn bằng tên cột
                item.setId(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_OI_ID)));
                item.setQuantity(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_OI_QUANTITY)));
                item.setPrice(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_OI_PRICE)));

                // Dữ liệu từ các bảng JOIN
                item.setFruitId(cursor.getInt(cursor.getColumnIndex("fruitId"))); // Cần thiết cho đánh giá
                item.setFruitName(cursor.getString(cursor.getColumnIndex(DBHelper.COL_FRUIT_NAME)));
                item.setFruitImage(cursor.getString(cursor.getColumnIndex(DBHelper.COL_FRUIT_IMG)));
                item.setSizeName(cursor.getString(cursor.getColumnIndex(DBHelper.COL_SIZE_NAME)));

                list.add(item);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        return list;
    }

    // Lấy toàn bộ đơn hàng (kèm tên User qua JOIN)
    public Cursor getAllOrdersWithUserInfo() {
        // Query lấy thêm cả payment_method
        String query = "SELECT o.*, u." + DBHelper.COL_USER_FULLNAME +
                " FROM " + DBHelper.TABLE_ORDER + " o " +
                " LEFT JOIN " + DBHelper.TABLE_USER + " u ON o." + DBHelper.COL_ORDER_USER_ID + " = u." + DBHelper.COL_USER_ID +
                " ORDER BY o." + DBHelper.COL_ORDER_ID + " DESC";
        return database.rawQuery(query, null);
    }

    // Cập nhật trạng thái đơn hàng
    public boolean updateOrderStatus(int orderId, int newStatus) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COL_ORDER_STATUS, newStatus);
        return database.update(DBHelper.TABLE_ORDER, values,
                DBHelper.COL_ORDER_ID + "=?", new String[]{String.valueOf(orderId)}) > 0;
    }
}