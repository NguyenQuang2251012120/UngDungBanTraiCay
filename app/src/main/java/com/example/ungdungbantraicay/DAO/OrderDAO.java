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
    public boolean placeOrder(int userId, int totalPrice, String address, List<CartItem> items) {
        database.beginTransaction();
        try {
            ContentValues orderValues = new ContentValues();
            orderValues.put(DBHelper.COL_ORDER_USER_ID, userId);
            orderValues.put(DBHelper.COL_ORDER_TOTAL, totalPrice);
            orderValues.put(DBHelper.COL_ORDER_STATUS, DBHelper.STATUS_PENDING);

            // LƯU ĐỊA CHỈ TẠI THỜI ĐIỂM ĐẶT HÀNG
            orderValues.put(DBHelper.COL_ORDER_ADDRESS, address);

            long orderId = database.insert(DBHelper.TABLE_ORDER, null, orderValues);

            if (orderId == -1) return false;

            // 2. Duyệt danh sách món trong giỏ để chép sang OrderItem
            for (CartItem item : items) {
                ContentValues detailValues = new ContentValues();
                detailValues.put(DBHelper.COL_OI_ORDER_ID, (int) orderId);
                detailValues.put(DBHelper.COL_OI_SIZE_ID, item.getFruitSizeId());
                detailValues.put(DBHelper.COL_OI_QUANTITY, item.getQuantity());
                detailValues.put(DBHelper.COL_OI_PRICE, item.getPrice()); // Lưu giá tại thời điểm mua

                long detailId = database.insert(DBHelper.TABLE_ORDER_ITEM, null, detailValues);
                if (detailId == -1) throw new Exception("Lỗi chèn chi tiết đơn hàng");
            }

            // 3. Xóa sạch giỏ hàng của User này sau khi đặt thành công
            // Tìm cart_id của user
            String getCartIdSql = "SELECT id FROM Cart WHERE user_id = ?";
            Cursor cursor = database.rawQuery(getCartIdSql, new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                int cartId = cursor.getInt(0);
                database.delete(DBHelper.TABLE_CART_ITEM, "cart_id = ?", new String[]{String.valueOf(cartId)});
            }
            cursor.close();

            // Đánh dấu giao dịch thành công
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            // Kết thúc giao dịch (Nếu không thành công, mọi thay đổi sẽ bị Rollback - hoàn tác)
            database.endTransaction();
        }

    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM " + DBHelper.TABLE_ORDER +
                " WHERE " + DBHelper.COL_ORDER_USER_ID + " = ?" +
                " ORDER BY " + DBHelper.COL_ORDER_ID + " DESC";

        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                Order order = new Order();
                order.setId(cursor.getInt(0));
                order.setUserId(cursor.getInt(1));
                order.setTotalPrice(cursor.getInt(2));
                order.setStatus(cursor.getInt(3));
                order.setAddress(cursor.getString(4)); // Cột address chúng ta mới thêm
                order.setCreatedAt(cursor.getString(5));
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
}