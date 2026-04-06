package com.example.ungdungbantraicay.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ungdungbantraicay.Activities.FruitDetailActivity;
import com.example.ungdungbantraicay.Adapter.FruitAdapter;
import com.example.ungdungbantraicay.DAO.CategoryDAO;
import com.example.ungdungbantraicay.DAO.FruitDAO;
import com.example.ungdungbantraicay.Model.Category;
import com.example.ungdungbantraicay.Model.Fruit;
import com.example.ungdungbantraicay.R;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {

    private RecyclerView recyclerAllProducts;
    private androidx.appcompat.widget.SearchView searchView;
    private FruitAdapter fruitAdapter;
    private List<Fruit> fruitList;
    private FruitDAO fruitDAO;

    public ProductFragment() {}
    com.google.android.material.chip.ChipGroup chipGroupCategories;
    int currentCategoryId = 0; // 0 là tất cả
    String currentQuery = "";
    String currentSort = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        chipGroupCategories = view.findViewById(R.id.chipGroupCategories);

        // 1. Ánh xạ
        recyclerAllProducts = view.findViewById(R.id.recyclerAllProducts);
        searchView = view.findViewById(R.id.searchViewProduct);
        fruitDAO = new FruitDAO(getContext());
        setupCategoryChips();

        // 2. Khởi tạo danh sách ban đầu
        fruitList = fruitDAO.getAllFruits();
        fruitAdapter = new FruitAdapter(getContext(), (ArrayList<Fruit>) fruitList, fruit -> {
            // Chuyển sang màn hình chi tiết khi click vào item
            Intent intent = new Intent(getActivity(), FruitDetailActivity.class);
            intent.putExtra("fruit_item", fruit);
            startActivity(intent);
        });

        recyclerAllProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerAllProducts.setAdapter(fruitAdapter);

        // 3. Xử lý sự kiện tìm kiếm
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentQuery = query;
                applyFilter();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                applyFilter();
                return true;
            }
        });

        // Xử lý các nút lọc
        view.findViewById(R.id.btnSortPriceAsc).setOnClickListener(v -> {
            currentSort = "PRICE_ASC";
            applyFilter();
        });

        view.findViewById(R.id.btnSortPriceDesc).setOnClickListener(v -> {
            currentSort = "PRICE_DESC";
            applyFilter();
        });

        view.findViewById(R.id.btnSortRating).setOnClickListener(v -> {
            currentSort = "RATING";
            applyFilter();
        });

        return view;
    }

    private void setupCategoryChips() {
        CategoryDAO categoryDAO = new CategoryDAO(getContext());
        List<Category> categories = categoryDAO.getAllCategory();

        // 1. Thêm Chip "Tất cả" mặc định
        addCategoryChip(0, "Tất cả");

        // 2. Thêm các Chip từ Database
        for (Category cat : categories) {
            addCategoryChip(cat.getId(), cat.getName());
        }
    }

    private void addCategoryChip(int id, String name) {
        com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(getContext());
        chip.setText(name);
        chip.setCheckable(true);
        chip.setClickable(true);

        // Nếu là chip "Tất cả", cho nó được chọn sẵn
        if (id == 0) chip.setChecked(true);

        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                currentCategoryId = id;
                applyFilter();
            }
        });
        chipGroupCategories.addView(chip);
    }

    private void applyFilter() {
        // Gọi hàm search mới với 3 tham số
        List<Fruit> filteredList = fruitDAO.searchFruits(currentQuery, currentCategoryId, currentSort);
        fruitList.clear();
        fruitList.addAll(filteredList);
        fruitAdapter.notifyDataSetChanged();
    }
}