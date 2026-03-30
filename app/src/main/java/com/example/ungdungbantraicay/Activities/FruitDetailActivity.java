package com.example.ungdungbantraicay.Activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.Adapter.FruitSizeAdapter;
import com.example.ungdungbantraicay.DAO.CategoryDAO;
import com.example.ungdungbantraicay.DAO.FruitSizeDAO;
import com.example.ungdungbantraicay.Model.Category;
import com.example.ungdungbantraicay.Model.Fruit;
import com.example.ungdungbantraicay.Model.FruitSize;
import com.example.ungdungbantraicay.R;

import java.util.ArrayList;

public class FruitDetailActivity extends AppCompatActivity {

    ImageView imgFruit;
    TextView tvName, tvDescription, tvCategory;
    RecyclerView recyclerSize;

    // Không cần FruitDAO nữa vì đã nhận được Object Fruit từ Intent
    FruitSizeDAO fruitSizeDAO;
    CategoryDAO categoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_detail);

        // Ánh xạ view
        imgFruit = findViewById(R.id.imgFruit);
        tvName = findViewById(R.id.tvFruitName);
        tvDescription = findViewById(R.id.tvFruitDescription);
        tvCategory = findViewById(R.id.tvCategory);
        recyclerSize = findViewById(R.id.recyclerSize);

        // 1. NHẬN OBJECT (Thay vì nhận ID)
        Fruit fruit = (Fruit) getIntent().getSerializableExtra("fruit_item");

        if (fruit != null) {
            fruitSizeDAO = new FruitSizeDAO(this);
            categoryDAO = new CategoryDAO(this);

            // 2. Hiển thị thông tin trực tiếp từ Object fruit
            tvName.setText(fruit.getName());
            tvDescription.setText(fruit.getDescription());

            int resId = getResources().getIdentifier(
                    fruit.getImage(),
                    "drawable",
                    getPackageName()
            );
            imgFruit.setImageResource(resId);

            // 3. Lấy tên Category (Vẫn cần DAO vì Object Fruit chỉ chứa ID category)
            Category category = categoryDAO.getCategoryById(fruit.getCategoryId());
            if (category != null) {
                tvCategory.setText("Danh mục: " + category.getName());
            }

            // 4. Lấy danh sách Size dựa trên ID của Object nhận được
            ArrayList<FruitSize> sizeList = fruitSizeDAO.getSizeByFruitId(fruit.getId());

            FruitSizeAdapter adapter = new FruitSizeAdapter(this, sizeList);
            // Bạn có thể dùng HORIZONTAL nếu muốn danh sách size nằm ngang cho đẹp
            recyclerSize.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerSize.setAdapter(adapter);
        }
    }
}