package com.example.ungdungbantraicay.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.Adapter.FruitSizeAdapter;
import com.example.ungdungbantraicay.Adapter.ReviewAdapter;
import com.example.ungdungbantraicay.DAO.CartDAO;
import com.example.ungdungbantraicay.DAO.CategoryDAO;
import com.example.ungdungbantraicay.DAO.FruitSizeDAO;
import com.example.ungdungbantraicay.DAO.ReviewDAO;
import com.example.ungdungbantraicay.Model.CartItem;
import com.example.ungdungbantraicay.Model.Category;
import com.example.ungdungbantraicay.Model.Fruit;
import com.example.ungdungbantraicay.Model.FruitSize;
import com.example.ungdungbantraicay.Model.Review;
import com.example.ungdungbantraicay.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class FruitDetailActivity extends AppCompatActivity {

    private ImageView imgFruit;
    private TextView tvName, tvDescription, tvCategory, tvFruitPrice, tvAverageRating, tvStatusDetail;
    private RatingBar ratingBarMain;
    private RecyclerView recyclerSize, recyclerReviews;
    private MaterialButton btnAddToCart, btnWriteReview;

    private int currentFruitId;
    private FruitSizeDAO fruitSizeDAO;
    private CategoryDAO categoryDAO;
    private ReviewDAO reviewDAO;
    private CartDAO cartDAO;

    private int currentUserId;
    private int selectedSizeId = -1;

    private FruitSizeAdapter sizeAdapter;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_detail);

        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        currentUserId = pref.getInt("userId", -1);

        initViews();

        cartDAO = new CartDAO(this);
        fruitSizeDAO = new FruitSizeDAO(this);
        categoryDAO = new CategoryDAO(this);
        reviewDAO = new ReviewDAO(this);

        Fruit fruit = (Fruit) getIntent().getSerializableExtra("fruit_item");

        if (fruit != null) {
            currentFruitId = fruit.getId();
            displayFruitInfo(fruit);
            loadReviewData(fruit.getId());
            loadSizeData(fruit.getId());

            // Bước 1: Kiểm tra quyền đánh giá ngay khi vào màn hình
            checkReviewEligibility(fruit.getId());

            btnAddToCart.setOnClickListener(v -> {
                if (currentUserId == -1) {
                    Toast.makeText(this, "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show();
                } else {
                    handleAddToCart();
                }
            });

            btnWriteReview.setOnClickListener(v -> showReviewDialog(fruit.getId()));
        }
    }

    private void initViews() {
        imgFruit = findViewById(R.id.imgFruit);
        tvName = findViewById(R.id.tvFruitName);
        tvDescription = findViewById(R.id.tvFruitDescription);
        tvCategory = findViewById(R.id.tvCategory);
        tvFruitPrice = findViewById(R.id.tvFruitPrice);
        recyclerSize = findViewById(R.id.recyclerSize);
        recyclerReviews = findViewById(R.id.recyclerReviews);
        ratingBarMain = findViewById(R.id.ratingBarMain);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        tvStatusDetail = findViewById(R.id.tvStatusDetail);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnWriteReview = findViewById(R.id.btnWriteReview);
    }

    // TÍCH HỢP: Kiểm tra điều kiện hiện nút Đánh giá
    public void checkReviewEligibility(int fruitId) {
        if (currentUserId == -1) {
            btnWriteReview.setVisibility(View.GONE);
            return;
        }

        // Đã mua + Đã nhận (Status 3) VÀ Chưa đánh giá trái cây này
        boolean purchased = reviewDAO.canUserReview(currentUserId, fruitId);
        boolean alreadyReviewed = reviewDAO.isAlreadyReviewed(currentUserId, fruitId);

        if (purchased && !alreadyReviewed) {
            btnWriteReview.setVisibility(View.VISIBLE);
        } else {
            btnWriteReview.setVisibility(View.GONE);
        }
    }

    // TÍCH HỢP: Dialog nhập liệu chuyên nghiệp
    private void showReviewDialog(int fruitId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_review, null);
        builder.setView(dialogView);

        RatingBar rbReview = dialogView.findViewById(R.id.rbReview);
        EditText edtComment = dialogView.findViewById(R.id.edtReviewComment);

        builder.setTitle("Đánh giá sản phẩm");
        builder.setPositiveButton("Gửi", (dialog, which) -> {
            float rating = rbReview.getRating();
            String comment = edtComment.getText().toString().trim();

            if (rating == 0) {
                Toast.makeText(this, "Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show();
                return;
            }

            Review newReview = new Review();
            newReview.setUserId(currentUserId);
            newReview.setFruitId(fruitId);
            newReview.setRating((int) rating);
            newReview.setComment(comment);

            if (reviewDAO.insertReview(newReview)) {
                Toast.makeText(this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();

                // Cập nhật lại UI: load lại list và ẩn nút đánh giá
                loadReviewData(fruitId);
                checkReviewEligibility(fruitId);
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    // 1. Cập nhật hàm loadReviewData để truyền Listener vào Adapter
    public void loadReviewData(int fruitId) {
        float avg = reviewDAO.getAvgRating(fruitId);
        ratingBarMain.setRating(avg);
        tvAverageRating.setText(String.format("%.1f/5", avg));

        reviewList = reviewDAO.getReviewsByFruitId(fruitId);

        // Triển khai Interface mới từ ReviewAdapter
        reviewAdapter = new ReviewAdapter(this, reviewList, currentUserId, new ReviewAdapter.OnReviewActionListener() {
            @Override
            public void onEdit(Review review, int position) {
                showEditReviewDialog(review, position);
            }

            @Override
            public void onDelete(Review review, int position) {
                showDeleteConfirmDialog(review, position);
            }
        });

        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setAdapter(reviewAdapter);
        recyclerReviews.setNestedScrollingEnabled(false);
    }

    // 2. Logic XÓA Đánh giá (Chuyển từ Adapter sang Activity)
    private void showDeleteConfirmDialog(Review review, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa đánh giá")
                .setMessage("Bạn có chắc chắn muốn xóa nhận xét này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (reviewDAO.deleteReview(review.getId())) {
                        reviewList.remove(position);
                        reviewAdapter.notifyItemRemoved(position);
                        reviewAdapter.notifyItemRangeChanged(position, reviewList.size());

                        Toast.makeText(this, "Đã xóa đánh giá!", Toast.LENGTH_SHORT).show();

                        // Cập nhật lại UI tổng thể
                        updateReviewStats(review.getFruitId());
                        checkReviewEligibility(review.getFruitId());
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // 3. Logic SỬA Đánh giá (Chuyển từ Adapter sang Activity)
    private void showEditReviewDialog(Review review, int position) {
        View view = getLayoutInflater().inflate(R.layout.dialog_review, null);
        RatingBar rbReview = view.findViewById(R.id.rbReview);
        EditText edtComment = view.findViewById(R.id.edtReviewComment);

        rbReview.setRating(review.getRating());
        edtComment.setText(review.getComment());

        new AlertDialog.Builder(this)
                .setTitle("Chỉnh sửa đánh giá")
                .setView(view)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    float newRating = rbReview.getRating();
                    String newComment = edtComment.getText().toString().trim();

                    if (newRating == 0) {
                        Toast.makeText(this, "Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (reviewDAO.updateReview(review.getId(), (int) newRating, newComment)) {
                        review.setRating((int) newRating);
                        review.setComment(newComment);
                        reviewAdapter.notifyItemChanged(position);

                        updateReviewStats(review.getFruitId());
                        Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Hàm phụ để cập nhật lại số sao trung bình trên tiêu đề
    private void updateReviewStats(int fruitId) {
        float avg = reviewDAO.getAvgRating(fruitId);
        ratingBarMain.setRating(avg);
        tvAverageRating.setText(String.format("%.1f/5", avg));
    }
    private void loadSizeData(int fruitId) {
        List<FruitSize> sizeList = fruitSizeDAO.getSizesByFruitId(fruitId);

        if (sizeList != null && !sizeList.isEmpty()) {
            int defaultPos = 0;
            for (int i = 0; i < sizeList.size(); i++) {
                if (sizeList.get(i).getStatus() == 1) {
                    defaultPos = i;
                    break;
                }
            }

            FruitSize defaultSize = sizeList.get(defaultPos);
            selectedSizeId = defaultSize.getId();
            tvFruitPrice.setText(String.format("%,d VND", defaultSize.getPrice()));
            updateUIByStatus(defaultSize);

            sizeAdapter = new FruitSizeAdapter(this, (ArrayList<FruitSize>) sizeList, fruitSize -> {
                selectedSizeId = fruitSize.getId();
                tvFruitPrice.setText(String.format("%,d VND", fruitSize.getPrice()));
                updateUIByStatus(fruitSize);
            });

            sizeAdapter.setSelectedPosition(defaultPos);
            recyclerSize.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerSize.setAdapter(sizeAdapter);
        }
    }

    private void updateUIByStatus(FruitSize fruitSize) {
        if (fruitSize.getStatus() == 1) {
            tvStatusDetail.setText("Trạng thái: Còn hàng");
            tvStatusDetail.setTextColor(android.graphics.Color.parseColor("#4CAF50"));
            btnAddToCart.setEnabled(true);
            btnAddToCart.setText("THÊM VÀO GIỎ HÀNG");
            btnAddToCart.setAlpha(1.0f);
        } else {
            tvStatusDetail.setText("Trạng thái: Size này hiện đã hết hàng");
            tvStatusDetail.setTextColor(android.graphics.Color.RED);
            btnAddToCart.setEnabled(false);
            btnAddToCart.setText("HẾT HÀNG");
            btnAddToCart.setAlpha(0.5f);
        }
    }

    private void handleAddToCart() {
        FruitSize selectedSize = sizeAdapter.getSelectedSize();
        if (selectedSize == null) {
            Toast.makeText(this, "Vui lòng chọn kích thước!", Toast.LENGTH_SHORT).show();
            return;
        }

        int cartId = cartDAO.getOrCreateCartId(currentUserId);
        CartItem item = new CartItem();
        item.setCartId(cartId);
        item.setFruitSizeId(selectedSize.getId());
        item.setQuantity(selectedSize.getQuantity());

        cartDAO.addToCart(item);
        Toast.makeText(this, "Đã thêm " + selectedSize.getQuantity() + " sản phẩm vào giỏ!", Toast.LENGTH_SHORT).show();
    }

    private void displayFruitInfo(Fruit fruit) {
        tvName.setText(fruit.getName());
        tvDescription.setText(fruit.getDescription());

        // CHỈ CẦN DÒNG NÀY:
        com.example.ungdungbantraicay.Helper.ImageHelper.loadFruitImage(this, fruit.getImage(), imgFruit);

        Category category = categoryDAO.getCategoryById(fruit.getCategoryId());
        if (category != null) {
            tvCategory.setText("Danh mục: " + category.getName());
        }
    }
}