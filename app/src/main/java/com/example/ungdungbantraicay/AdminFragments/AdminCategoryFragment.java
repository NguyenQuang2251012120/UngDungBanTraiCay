package com.example.ungdungbantraicay.AdminFragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.ungdungbantraicay.Adapter.CategoryAdapter;
import com.example.ungdungbantraicay.DAO.CategoryDAO;
import com.example.ungdungbantraicay.Model.Category;
import com.example.ungdungbantraicay.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AdminCategoryFragment extends Fragment {
    private RecyclerView rvCategory;
    private FloatingActionButton fabAdd;
    private CategoryDAO dao;
    private CategoryAdapter adapter;
    private ArrayList<Category> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_category, container, false);

        rvCategory = view.findViewById(R.id.rvAdminCategory);
        fabAdd = view.findViewById(R.id.fabAddCategory);
        dao = new CategoryDAO(getContext());

        loadData();

        fabAdd.setOnClickListener(v -> showDialogAddEdit(null));

        return view;
    }

    private void loadData() {
        list = (ArrayList<Category>) dao.getAllCategoryForAdmin();
        adapter = new CategoryAdapter(getContext(), list, category -> showDialogAddEdit(category));
        rvCategory.setAdapter(adapter);
    }

    private void showDialogAddEdit(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        EditText edtName = dialogView.findViewById(R.id.edtCatName);
        CheckBox cbStatus = dialogView.findViewById(R.id.cbStatus);

        if (category != null) {
            builder.setTitle("Sửa danh mục");
            edtName.setText(category.getName());
            cbStatus.setChecked(category.getStatus() == 1);
        } else {
            builder.setTitle("Thêm danh mục");
            cbStatus.setVisibility(View.GONE); // Thêm mới thì mặc định hiện
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = edtName.getText().toString();
            if (category == null) {
                dao.insertCategory(name);
            } else {
                category.setName(name);
                category.setStatus(cbStatus.isChecked() ? 1 : 0);
                dao.updateCategory(category);
            }
            loadData();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}