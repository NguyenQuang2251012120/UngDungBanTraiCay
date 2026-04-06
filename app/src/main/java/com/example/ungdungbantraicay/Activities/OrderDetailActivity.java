package com.example.ungdungbantraicay.Activities;

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

    TextView tvId, tvAddress, tvTotal, tvStatus;
    RecyclerView recyclerItems;
    OrderDAO orderDAO;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);

        // Lấy userId để đánh giá
        android.content.SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        currentUserId = pref.getInt("userId", -1);

        // Fix lề màn hình
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvId = findViewById(R.id.tvDetailOrderId);
        tvAddress = findViewById(R.id.tvDetailAddress);
        tvTotal = findViewById(R.id.tvDetailTotal);
        tvStatus = findViewById(R.id.tvDetailStatus);
        recyclerItems = findViewById(R.id.recyclerOrderItems);
        orderDAO = new OrderDAO(this);

        Order order = (Order) getIntent().getSerializableExtra("order_object");

        if (order != null) {
            tvId.setText("Đơn hàng #" + order.getId());
            tvAddress.setText("Địa chỉ: " + order.getAddress());
            tvTotal.setText(String.format("Tổng tiền: %,d VND", order.getTotalPrice()));
            tvStatus.setText("Trạng thái: " + DBHelper.getStatusName(order.getStatus()));

            // FIX 1: Truyền vào đối tượng order thay vì int
            loadOrderItems(order);
        }
    }

    private void loadOrderItems(Order order) {
        List<OrderItem> details = orderDAO.getOrderDetails(order.getId());

        // Cập nhật Adapter với logic mới
        OrderItemAdapter adapter = new OrderItemAdapter(this, details, order.getStatus(), currentUserId, new OrderItemAdapter.OnOrderItemActionListener() {
            @Override
            public void onReview(OrderItem item) {
                showReviewDialog(item.getFruitId(), item.getFruitName());
            }

            @Override
            public void onDeleteReview(OrderItem item) {
                confirmDeleteReview(item);
            }
        });

        recyclerItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerItems.setAdapter(adapter);
    }

    private void showReviewDialog(int fruitId, String fruitName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_review, null);
        builder.setView(dialogView);
        builder.setTitle("Đánh giá: " + fruitName);

        RatingBar rbReview = dialogView.findViewById(R.id.rbReview);
        EditText edtComment = dialogView.findViewById(R.id.edtReviewComment);

        builder.setPositiveButton("Gửi", (dialog, which) -> {
            Review review = new Review();
            review.setUserId(currentUserId);
            review.setFruitId(fruitId);
            review.setRating((int) rbReview.getRating());
            review.setComment(edtComment.getText().toString().trim());

            ReviewDAO reviewDAO = new ReviewDAO(this);
            if (reviewDAO.insertReview(review)) {
                Toast.makeText(this, "Đánh giá thành công!", Toast.LENGTH_SHORT).show();

                // LOAD LẠI để nút đổi từ "Đánh giá" sang "Xóa đánh giá"
                Order order = (Order) getIntent().getSerializableExtra("order_object");
                loadOrderItems(order);
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void confirmDeleteReview(OrderItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa đánh giá")
                .setMessage("Bạn muốn xóa đánh giá của món " + item.getFruitName() + " để viết lại?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    ReviewDAO reviewDAO = new ReviewDAO(this);
                    if (reviewDAO.deleteReviewByUserAndFruit(currentUserId, item.getFruitId())) {
                        Toast.makeText(this, "Đã xóa đánh giá!", Toast.LENGTH_SHORT).show();

                        // QUAN TRỌNG: Load lại danh sách để nút "Đánh giá" hiện ra lại
                        Order order = (Order) getIntent().getSerializableExtra("order_object");
                        loadOrderItems(order);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}