package com.example.ungdungbantraicay.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ungdungbantraicay.Helper.DBHelper;

public class StatisticDAO {
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public StatisticDAO(Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // 1. Thống kê doanh thu theo từng User đặt hàng
    public Cursor getRevenueByUser() {
        String query = "SELECT u." + DBHelper.COL_USER_FULLNAME + ", " +
                "COUNT(o." + DBHelper.COL_ORDER_ID + ") AS total_orders, " +
                "SUM(o." + DBHelper.COL_ORDER_TOTAL + ") AS total_spent " +
                "FROM " + DBHelper.TABLE_USER + " u " +
                "JOIN " + DBHelper.TABLE_ORDER + " o ON u." + DBHelper.COL_USER_ID + " = o." + DBHelper.COL_ORDER_USER_ID + " " +
                "WHERE o." + DBHelper.COL_ORDER_STATUS + " = " + DBHelper.STATUS_SUCCESS + " " +
                "GROUP BY u." + DBHelper.COL_USER_ID + " " +
                "ORDER BY total_spent DESC";
        return db.rawQuery(query, null);
    }

    // 2. Thống kê doanh thu theo Ngày
    public Cursor getRevenueByDay() {
        String query = "SELECT DATE(" + DBHelper.COL_ORDER_DATE + ") AS order_date, " +
                "SUM(" + DBHelper.COL_ORDER_TOTAL + ") AS daily_revenue " +
                "FROM " + DBHelper.TABLE_ORDER + " " +
                "WHERE " + DBHelper.COL_ORDER_STATUS + " = " + DBHelper.STATUS_SUCCESS + " " +
                "GROUP BY order_date " +
                "ORDER BY order_date DESC";
        return db.rawQuery(query, null);
    }

    // 3. Thống kê doanh thu theo Tháng
    public Cursor getRevenueByMonth() {
        String query = "SELECT strftime('%m/%Y', " + DBHelper.COL_ORDER_DATE + ") AS month_year, " +
                "SUM(" + DBHelper.COL_ORDER_TOTAL + ") AS monthly_revenue " +
                "FROM " + DBHelper.TABLE_ORDER + " " +
                "WHERE " + DBHelper.COL_ORDER_STATUS + " = " + DBHelper.STATUS_SUCCESS + " " +
                "GROUP BY month_year " +
                "ORDER BY " + DBHelper.COL_ORDER_DATE + " DESC";
        return db.rawQuery(query, null);
    }

    // 4. Thống kê doanh thu theo Danh mục sản phẩm (Category)
    public Cursor getRevenueByCategory() {
        String query = "SELECT c." + DBHelper.COL_CAT_NAME + ", " +
                "SUM(oi." + DBHelper.COL_OI_QUANTITY + " * oi." + DBHelper.COL_OI_PRICE + ") AS cat_revenue " +
                "FROM " + DBHelper.TABLE_CATEGORY + " c " +
                "JOIN " + DBHelper.TABLE_FRUIT + " f ON c." + DBHelper.COL_CAT_ID + " = f." + DBHelper.COL_FRUIT_CAT_ID + " " +
                "JOIN " + DBHelper.TABLE_FRUIT_SIZE + " fs ON f." + DBHelper.COL_FRUIT_ID + " = fs." + DBHelper.COL_SIZE_FRUIT_ID + " " +
                "JOIN " + DBHelper.TABLE_ORDER_ITEM + " oi ON fs." + DBHelper.COL_SIZE_ID + " = oi." + DBHelper.COL_OI_SIZE_ID + " " +
                "JOIN " + DBHelper.TABLE_ORDER + " o ON oi." + DBHelper.COL_OI_ORDER_ID + " = o." + DBHelper.COL_ORDER_ID + " " +
                "WHERE o." + DBHelper.COL_ORDER_STATUS + " = " + DBHelper.STATUS_SUCCESS + " " +
                "GROUP BY c." + DBHelper.COL_CAT_ID + " " +
                "ORDER BY cat_revenue DESC";
        return db.rawQuery(query, null);
    }

    // 5. Trích lọc Sản phẩm bán chạy nhất (Best Selling)
    public Cursor getBestSellingFruits(int limit) {
        String query = "SELECT f." + DBHelper.COL_FRUIT_NAME + ", " +
                "SUM(oi." + DBHelper.COL_OI_QUANTITY + ") AS total_sold " +
                "FROM " + DBHelper.TABLE_FRUIT + " f " +
                "JOIN " + DBHelper.TABLE_FRUIT_SIZE + " fs ON f." + DBHelper.COL_FRUIT_ID + " = fs." + DBHelper.COL_SIZE_FRUIT_ID + " " +
                "JOIN " + DBHelper.TABLE_ORDER_ITEM + " oi ON fs." + DBHelper.COL_SIZE_ID + " = oi." + DBHelper.COL_OI_SIZE_ID + " " +
                "JOIN " + DBHelper.TABLE_ORDER + " o ON oi." + DBHelper.COL_OI_ORDER_ID + " = o." + DBHelper.COL_ORDER_ID + " " +
                "WHERE o." + DBHelper.COL_ORDER_STATUS + " = " + DBHelper.STATUS_SUCCESS + " " +
                "GROUP BY f." + DBHelper.COL_FRUIT_ID + " " +
                "ORDER BY total_sold DESC " +
                "LIMIT " + limit;
        return db.rawQuery(query, null);
    }
}