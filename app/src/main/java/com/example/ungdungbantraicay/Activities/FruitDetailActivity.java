package com.example.ungdungbantraicay.Activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.Adapter.FruitSizeAdapter;
import com.example.ungdungbantraicay.DAO.CategoryDAO;
import com.example.ungdungbantraicay.DAO.FruitDAO;
import com.example.ungdungbantraicay.DAO.FruitSizeDAO;
import com.example.ungdungbantraicay.Model.Category;
import com.example.ungdungbantraicay.Model.Fruit;
import com.example.ungdungbantraicay.Model.FruitSize;
import com.example.ungdungbantraicay.R;

import java.util.ArrayList;

public class FruitDetailActivity extends AppCompatActivity {

    ImageView imgFruit;
    TextView tvName,tvDescription,tvCategory;
    RecyclerView recyclerSize;

    FruitDAO fruitDAO;
    FruitSizeDAO fruitSizeDAO;
    CategoryDAO categoryDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_detail);

        imgFruit = findViewById(R.id.imgFruit);
        tvName = findViewById(R.id.tvFruitName);
        tvDescription = findViewById(R.id.tvFruitDescription);
        tvCategory = findViewById(R.id.tvCategory);
        recyclerSize = findViewById(R.id.recyclerSize);

        int fruitId = getIntent().getIntExtra("fruit_id",-1);

        fruitDAO = new FruitDAO(this);
        fruitSizeDAO = new FruitSizeDAO(this);
        categoryDAO = new CategoryDAO(this);

        Fruit fruit = fruitDAO.getFruitById(fruitId);

        tvName.setText(fruit.getName());
        tvDescription.setText(fruit.getDescription());

        int resId = getResources().getIdentifier(
                fruit.getImage(),
                "drawable",
                getPackageName()
        );

        imgFruit.setImageResource(resId);

        Category category = categoryDAO.getCategoryById(fruit.getCategoryId());
        tvCategory.setText("Category: " + category.getName());

        ArrayList<FruitSize> sizeList = fruitSizeDAO.getSizeByFruitId(fruitId);

        FruitSizeAdapter adapter = new FruitSizeAdapter(this,sizeList);

        recyclerSize.setLayoutManager(new LinearLayoutManager(this));
        recyclerSize.setAdapter(adapter);
    }
}