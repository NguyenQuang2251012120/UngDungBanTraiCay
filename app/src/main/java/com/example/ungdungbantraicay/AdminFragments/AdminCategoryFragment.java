package com.example.ungdungbantraicay.AdminFragments;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ungdungbantraicay.AdminAdapter.AdminCategoryAdapter;
import com.example.ungdungbantraicay.DAO.CategoryDAO;
import com.example.ungdungbantraicay.Model.Category;
import com.example.ungdungbantraicay.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AdminCategoryFragment extends Fragment {
    private RecyclerView rvCategory;
    private FloatingActionButton fabAdd;
    private CategoryDAO dao;
    private AdminCategoryAdapter adapter;
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

        // Use the correct Adapter class and Interface methods
        adapter = new AdminCategoryAdapter(getContext(), list, new AdminCategoryAdapter.OnAdminCategoryClickListener() {
            @Override
            public void onEditClick(Category category) { // Change name from onCategoryClick
                showDialogAddEdit(category);
            }

            @Override
            public void onDeleteClick(Category category) { // This matches
                showConfirmDeleteDialog(category);
            }
        });

        rvCategory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategory.setAdapter(adapter);
    }

    private void showConfirmDeleteDialog(Category category) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa danh mục '" + category.getName())
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (dao.deleteCategory(category.getId())) {
                        Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                        loadData(); // Cập nhật lại danh sách
                    } else {
                        // Thông báo khi vướng khóa ngoại
                        Toast.makeText(getContext(), "Lỗi: Danh mục này đang chứa sản phẩm, không thể xóa!", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    private void showDialogAddEdit(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);
        EditText edtName = dialogView.findViewById(R.id.edtCatName);
        CheckBox cbStatus = dialogView.findViewById(R.id.cbStatus);

        if (category != null) {
            builder.setTitle("Sửa danh mục: " + category.getName());
            edtName.setText(category.getName());
            cbStatus.setChecked(category.getStatus() == 1);
            cbStatus.setVisibility(View.VISIBLE);
        } else {
            builder.setTitle("Thêm danh mục mới");
            cbStatus.setVisibility(View.GONE);
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Lưu", null); // Đặt null để xử lý riêng sự kiện click
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Xử lý sự kiện nút LƯU (Ngăn đóng dialog nếu có lỗi)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            int currentId = (category == null) ? -1 : category.getId();

            // LỖI 1: Tên để trống
            if (name.isEmpty()) {
                edtName.setError("Tên danh mục không được để trống!");
                return;
            }

            // LỖI 2: Trùng tên danh mục
            if (dao.isCategoryNameExists(name, currentId)) {
                edtName.setError("Tên danh mục này đã tồn tại!");
                Toast.makeText(getContext(), "Không được đặt tên trùng!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Nếu hợp lệ thì mới thực hiện lưu
            if (category == null) {
                if (dao.insertCategory(name)) {
                    Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                }
            } else {
                category.setName(name);
                category.setStatus(cbStatus.isChecked() ? 1 : 0);
                if (dao.updateCategory(category)) {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                }
            }

            loadData();
            dialog.dismiss(); // Chỉ đóng dialog khi dữ liệu hợp lệ và đã lưu
        });
    }
}