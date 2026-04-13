package com.example.ungdungbantraicay.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.Adapter.OrderItemAdapter;
import com.example.ungdungbantraicay.DAO.OrderDAO;
import com.example.ungdungbantraicay.DAO.ReviewDAO;
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.Order;
import com.example.ungdungbantraicay.Model.OrderItem;
import com.example.ungdungbantraicay.Model.Review;
import com.example.ungdungbantraicay.R;

import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    // 1. Khai báo các View
    private TextView tvId, tvAddress, tvTotal, tvStatus;
    private RecyclerView recyclerItems;

    // 2. Khai báo các đối tượng hỗ trợ
    private OrderDAO orderDAO;
    private ReviewDAO reviewDAO;
    private int currentUserId;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);

        // Khởi tạo các thành phần cơ bản
        initViews();
        setupWindowInsets();

        // Khởi tạo DAO
        orderDAO = new OrderDAO(this);
        reviewDAO = new ReviewDAO(this);

        // Lấy thông tin User và Order
        initData();
    }

    /**
     * Ánh xạ các View từ XML
     */
    private void initViews() {
        tvId = findViewById(R.id.tvDetailOrderId);
        tvAddress = findViewById(R.id.tvDetailAddress);
        tvTotal = findViewById(R.id.tvDetailTotal);
        tvStatus = findViewById(R.id.tvDetailStatus);
        recyclerItems = findViewById(R.id.recyclerOrderItems);
    }

    /**
     * Xử lý lề màn hình (Edge-to-Edge)
     */
    private void setupWindowInsets() {
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    /**
     * Tải dữ liệu ban đầu
     */
    private void initData() {
        // Lấy userId từ SharedPreferences
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        currentUserId = pref.getInt("userId", -1);

        // Nhận đối tượng Order từ Intent
        currentOrder = (Order) getIntent().getSerializableExtra("order_object");

        if (currentOrder != null) {
            displayOrderInfo();
            loadOrderItems();
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Hiển thị thông tin tổng quát của đơn hàng
     */
    private void displayOrderInfo() {
        tvId.setText("Đơn hàng #" + currentOrder.getId());
        tvAddress.setText("Địa chỉ: " + currentOrder.getAddress());
        tvTotal.setText(String.format("Tổng tiền: %,d VND", currentOrder.getTotalPrice()));
        tvStatus.setText("Trạng thái: " + DBHelper.getStatusName(currentOrder.getStatus()));
    }

    /**
     * Tải danh sách các món trong đơn hàng lên RecyclerView
     */
    private void loadOrderItems() {
        List<OrderItem> details = orderDAO.getOrderDetails(currentOrder.getId());

        OrderItemAdapter adapter = new OrderItemAdapter(
                this,
                details,
                currentOrder.getStatus(),
                currentUserId,
                new OrderItemAdapter.OnOrderItemActionListener() {
                    @Override
                    public void onReview(OrderItem item) {
                        showReviewDialog(item.getFruitId(), item.getFruitName());
                    }

                    @Override
                    public void onDeleteReview(OrderItem item) {
                        confirmDeleteReview(item);
                    }
                }
        );

        recyclerItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerItems.setAdapter(adapter);
    }

    /**
     * Hiển thị Dialog đánh giá sản phẩm
     */
    private void showReviewDialog(int fruitId, String fruitName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_review, null);
        builder.setView(dialogView);
        builder.setTitle("Đánh giá: " + fruitName);

        RatingBar rbReview = dialogView.findViewById(R.id.rbReview);
        EditText edtComment = dialogView.findViewById(R.id.edtReviewComment);

        builder.setPositiveButton("Gửi", (dialog, which) -> {
            float rating = rbReview.getRating();
            if (rating == 0) {
                Toast.makeText(this, "Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show();
                return;
            }

            Review review = new Review();
            review.setUserId(currentUserId);
            review.setFruitId(fruitId);
            review.setRating((int) rating);
            review.setComment(edtComment.getText().toString().trim());

            if (reviewDAO.insertReview(review)) {
                Toast.makeText(this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                loadOrderItems(); // Làm mới danh sách để cập nhật nút bấm
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    /**
     * Hiển thị xác nhận xóa đánh giá
     */
    private void confirmDeleteReview(OrderItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa đánh giá")
                .setMessage("Bạn muốn xóa đánh giá của món " + item.getFruitName() + " để viết lại?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (reviewDAO.deleteReviewByUserAndFruit(currentUserId, item.getFruitId())) {
                        Toast.makeText(this, "Đã xóa đánh giá!", Toast.LENGTH_SHORT).show();
                        loadOrderItems(); // Làm mới danh sách
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}