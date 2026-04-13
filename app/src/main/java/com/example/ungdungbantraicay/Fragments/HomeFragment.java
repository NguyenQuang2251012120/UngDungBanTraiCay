package com.example.ungdungbantraicay.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ungdungbantraicay.Activities.FruitDetailActivity;
import com.example.ungdungbantraicay.Adapter.CategoryAdapter;
import com.example.ungdungbantraicay.Adapter.FruitAdapter;
import com.example.ungdungbantraicay.DAO.CategoryDAO;
import com.example.ungdungbantraicay.DAO.FruitDAO;
import com.example.ungdungbantraicay.Model.Category;
import com.example.ungdungbantraicay.Model.Fruit;
import com.example.ungdungbantraicay.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    RecyclerView recyclerFruit, recyclerCategory;
    FruitAdapter fruitAdapter;
    CategoryAdapter categoryAdapter;

    // Khởi tạo sẵn List để tránh NullPointerException khi gọi .clear()
    List<Fruit> fruitList = new ArrayList<>();
    List<Category> categoryList = new ArrayList<>();

    FruitDAO fruitDAO;
    CategoryDAO categoryDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Ánh xạ và khởi tạo DAO
        recyclerFruit = view.findViewById(R.id.recyclerFruit);
        recyclerCategory = view.findViewById(R.id.recyclerCategory);
        fruitDAO = new FruitDAO(getContext());
        categoryDAO = new CategoryDAO(getContext());

        // 2. Thiết lập Adapter cho Danh mục (Category)
        categoryAdapter = new CategoryAdapter(getContext(), (ArrayList<Category>) categoryList, category -> {
            // Lọc trái cây theo danh mục
            List<Fruit> filteredFruits = fruitDAO.getFruitsByCategory(category.getId());
            fruitList.clear();
            fruitList.addAll(filteredFruits);
            fruitAdapter.notifyDataSetChanged();
        });
        recyclerCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerCategory.setAdapter(categoryAdapter);

        // 3. Thiết lập Adapter cho Trái cây (Fruit)
        fruitAdapter = new FruitAdapter(getContext(), (ArrayList<Fruit>) fruitList, fruit -> {
            Intent intent = new Intent(getActivity(), FruitDetailActivity.class);
            intent.putExtra("fruit_item", fruit);
            startActivity(intent);
        });
        recyclerFruit.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerFruit.setNestedScrollingEnabled(false);
        recyclerFruit.setAdapter(fruitAdapter);

        // Đừng gọi loadData trực tiếp ở đây vì onResume sẽ lo việc này ngay sau đó
        return view;
    }

    // --- HÀM QUAN TRỌNG: Tự động làm mới khi quay lại ---
    @Override
    public void onResume() {
        super.onResume();
        loadData(); // Cập nhật lại dữ liệu mỗi khi màn hình hiện ra
    }

    private void loadData() {
        // Cập nhật Danh mục
        categoryList.clear();
        categoryList.addAll(categoryDAO.getAllCategory());
        categoryAdapter.notifyDataSetChanged();

        // Cập nhật Trái cây (Load lại từ DB để lấy giá mới nhất, ảnh mới...)
        fruitList.clear();
        fruitList.addAll(fruitDAO.getAllFruits());
        fruitAdapter.notifyDataSetChanged();
    }
}