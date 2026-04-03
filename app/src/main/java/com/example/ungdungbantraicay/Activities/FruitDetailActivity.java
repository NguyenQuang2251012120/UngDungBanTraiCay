package com.example.ungdungbantraicay.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView tvName, tvDescription, tvCategory, tvFruitPrice, tvAverageRating;
    private RatingBar ratingBarMain;
    private RecyclerView recyclerSize, recyclerReviews;
    private MaterialButton btnAddToCart; // Thêm nút này

    private FruitSizeDAO fruitSizeDAO;
    private CategoryDAO categoryDAO;
    private ReviewDAO reviewDAO;
    private CartDAO cartDAO; // Thêm CartDAO

    private int currentUserId;
    private int selectedSizeId = -1; // Biến lưu size đang được chọn

    private FruitSizeAdapter sizeAdapter;
    private ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_detail);

        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        currentUserId = pref.getInt("userId", -1);

        initViews();

        // --- 2. Nhận dữ liệu từ Intent ---
        // Khởi tạo CartDAO
        cartDAO = new CartDAO(this);

        Fruit fruit = (Fruit) getIntent().getSerializableExtra("fruit_item");

        if (fruit != null) {
            fruitSizeDAO = new FruitSizeDAO(this);
            categoryDAO = new CategoryDAO(this);
            reviewDAO = new ReviewDAO(this);

            displayFruitInfo(fruit);
            loadReviewData(fruit.getId());
            loadSizeData(fruit.getId());

            // Xử lý sự kiện click Thêm Giỏ
            btnAddToCart.setOnClickListener(v -> {
                if (currentUserId == -1) {
                    Toast.makeText(this, "Vui lòng đăng nhập trước!", Toast.LENGTH_SHORT).show();
                } else {
                    handleAddToCart();
                }
            });
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

        // Ánh xạ nút Thêm Giỏ (Nhớ đặt ID trong XML là btnAddToCart)
        btnAddToCart = findViewById(R.id.btnAddToCart);
    }

    private void loadSizeData(int fruitId) {
        List<FruitSize> sizeList = fruitSizeDAO.getSizesByFruitId(fruitId);

        if (sizeList != null && !sizeList.isEmpty()) {
            // Mặc định chọn size đầu tiên khi vừa vào màn hình
            selectedSizeId = sizeList.get(0).getId();
            tvFruitPrice.setText(String.format("%,d VND", sizeList.get(0).getPrice()));

            sizeAdapter = new FruitSizeAdapter(this, (ArrayList<FruitSize>) sizeList, fruitSize -> {
                if (fruitSize != null) {
                    // Cập nhật size Id khi người dùng click vào RecyclerView
                    selectedSizeId = fruitSize.getId();
                    tvFruitPrice.setText(String.format("%,d VND", fruitSize.getPrice()));
                }
            });

            recyclerSize.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerSize.setAdapter(sizeAdapter);
            recyclerSize.setNestedScrollingEnabled(false);
        }
    }

    private void handleAddToCart() {
        // 1. Lấy đối tượng FruitSize đang được chọn trực tiếp từ Adapter
        FruitSize selectedSize = sizeAdapter.getSelectedSize();

        if (selectedSize == null) {
            Toast.makeText(this, "Vui lòng chọn kích thước!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Lấy hoặc tạo Cart ID cho người dùng
        int cartId = cartDAO.getOrCreateCartId(currentUserId);

        // 3. Tạo đối tượng CartItem để đưa vào giỏ
        CartItem item = new CartItem();
        item.setCartId(cartId);
        item.setFruitSizeId(selectedSize.getId());

        // QUAN TRỌNG: Lấy số lượng thực tế từ đối tượng selectedSize
        // chứ không để số 1 cố định nữa
        item.setQuantity(selectedSize.getQuantity());

        // 4. Thực hiện lưu vào DB
        cartDAO.addToCart(item);

        Toast.makeText(this, "Đã thêm " + selectedSize.getQuantity() + " sản phẩm vào giỏ!", Toast.LENGTH_SHORT).show();
    }

    private void displayFruitInfo(Fruit fruit) {
        tvName.setText(fruit.getName());
        tvDescription.setText(fruit.getDescription());

        // Hiển thị ảnh
        int resId = getResources().getIdentifier(fruit.getImage(), "drawable", getPackageName());
        if (resId != 0) imgFruit.setImageResource(resId);

        // Hiển thị Category
        Category category = categoryDAO.getCategoryById(fruit.getCategoryId());
        if (category != null) {
            tvCategory.setText("Danh mục: " + category.getName());
        }
    }

    private void loadReviewData(int fruitId) {
        // Điểm trung bình
        float avg = reviewDAO.getAvgRating(fruitId);
        ratingBarMain.setRating(avg);
        tvAverageRating.setText(String.format("%.1f/5", avg));

        // Danh sách review
        List<Review> reviews = reviewDAO.getReviewsByFruitId(fruitId);
        reviewAdapter = new ReviewAdapter(this, reviews);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerReviews.setAdapter(reviewAdapter);
        recyclerReviews.setNestedScrollingEnabled(false);
    }
}