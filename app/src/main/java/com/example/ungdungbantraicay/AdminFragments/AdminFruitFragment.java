package com.example.ungdungbantraicay.AdminFragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ungdungbantraicay.AdminAdapter.AdminFruitAdapter;
import com.example.ungdungbantraicay.DAO.CategoryDAO;
import com.example.ungdungbantraicay.DAO.FruitDAO;
import com.example.ungdungbantraicay.Model.Category;
import com.example.ungdungbantraicay.Model.Fruit;
import com.example.ungdungbantraicay.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AdminFruitFragment extends Fragment {

    RecyclerView rvFruit;
    FruitDAO fruitDAO;
    AdminFruitAdapter adapter;
    List<Fruit> fruitList;

    private Uri selectedImageUri;
    private ImageView imgPreview;

    // Bộ lắng nghe kết quả chọn ảnh từ thư viện
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (imgPreview != null) {
                        imgPreview.setImageURI(selectedImageUri);
                    }
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_fruit, container, false);
        rvFruit = view.findViewById(R.id.rvAdminFruit);
        fruitDAO = new FruitDAO(getContext());
        FloatingActionButton fabAdd = view.findViewById(R.id.fabAddFruit);

        fabAdd.setOnClickListener(v -> showDialogFruit(null));

        loadData();
        return view;
    }

    private void loadData() {
        fruitList = fruitDAO.getAllFruits();
        adapter = new AdminFruitAdapter(getContext(), fruitList, new AdminFruitAdapter.OnFruitActionListener() {
            @Override
            public void onEdit(Fruit fruit) {
                showDialogFruit(fruit);
            }

            @Override
            public void onDelete(Fruit fruit) {
                confirmDelete(fruit);
            }
        });
        rvFruit.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFruit.setAdapter(adapter);
    }

    private void showDialogFruit(Fruit fruit) {
        selectedImageUri = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_add_fruit, null);

        EditText edtName = view.findViewById(R.id.edtFruitName);
        EditText edtDesc = view.findViewById(R.id.edtFruitDesc);
        imgPreview = view.findViewById(R.id.imgPreview);
        Button btnSelect = view.findViewById(R.id.btnSelectImage);
        Spinner spnCat = view.findViewById(R.id.spnCategory);
        CheckBox chkStatus = view.findViewById(R.id.chkStatus);

        CategoryDAO catDAO = new CategoryDAO(getContext());
        List<Category> catList = catDAO.getAllCategory();
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, getCategoryNames(catList));
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCat.setAdapter(catAdapter);

        boolean isEdit = (fruit != null);
        if (isEdit) {
            builder.setTitle("Cập nhật trái cây");
            edtName.setText(fruit.getName());
            edtDesc.setText(fruit.getDescription());
            chkStatus.setChecked(fruit.getStatus() == 1);
            spnCat.setSelection(getCategoryPosition(catList, fruit.getCategoryId()));

            // Load ảnh hiện tại vào preview
            loadImageToView(fruit.getImage(), imgPreview);
        } else {
            builder.setTitle("Thêm trái cây mới");
        }

        btnSelect.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });

        builder.setView(view);
        builder.setPositiveButton(fruit != null ? "Cập nhật" : "Thêm mới", null); // Để null để xử lý riêng
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Xử lý sự kiện nút POSITIVE (Lưu/Cập nhật)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String desc = edtDesc.getText().toString().trim();
            int catId = catList.get(spnCat.getSelectedItemPosition()).getId();
            int status = chkStatus.isChecked() ? 1 : 0;
            int currentId = (fruit == null) ? -1 : fruit.getId();

            // 1. Kiểm tra trống
            if (name.isEmpty()) {
                edtName.setError("Tên không được để trống!");
                return;
            }

            // 2. KIỂM TRA TRÙNG TÊN
            if (fruitDAO.isFruitNameExists(name, currentId)) {
                edtName.setError("Tên trái cây này đã tồn tại!");
                Toast.makeText(getContext(), "Vui lòng chọn tên khác!", Toast.LENGTH_SHORT).show();
                return; // Dừng lại, không cho lưu
            }

            // 3. Xử lý lưu ảnh (giữ nguyên logic của bạn)
            String finalImagePath = (fruit != null) ? fruit.getImage() : "apple";
            if (selectedImageUri != null) {
                finalImagePath = saveImageToInternal(selectedImageUri);
            }

            if (fruit != null) {
                // Update
                fruit.setName(name);
                fruit.setDescription(desc);
                fruit.setImage(finalImagePath);
                fruit.setCategoryId(catId);
                fruit.setStatus(status);
                fruitDAO.updateFruitFull(fruit);
            } else {
                // Insert
                Fruit newFruit = new Fruit(0, name, desc, finalImagePath, catId, status, 0.0f);
                fruitDAO.insertFruit(newFruit);
            }

            loadData();
            dialog.dismiss(); // Chỉ đóng khi mọi thứ đã OK
        });
    }

    // Hàm lưu Uri ảnh vào thư mục files của App
    private String saveImageToInternal(Uri uri) {
        try {
            String fileName = "img_" + System.currentTimeMillis() + ".jpg";
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.close();
            return fileName;
        } catch (Exception e) {
            return "apple";
        }
    }

    // Hàm load ảnh thông minh
    private void loadImageToView(String fileName, ImageView imageView) {
        int resId = getResources().getIdentifier(fileName, "drawable", getContext().getPackageName());
        if (resId != 0) {
            Glide.with(this).load(resId).into(imageView);
        } else {
            File file = new File(getContext().getFilesDir(), fileName);
            Glide.with(this).load(file).into(imageView);
        }
    }

    private void confirmDelete(Fruit fruit) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa " + fruit.getName() + " không?")
                .setPositiveButton("Xóa", (d, w) -> {
                    int result = fruitDAO.deleteFruitSmart(fruit.getId());

                    if (result == 1) {
                        Toast.makeText(getContext(),
                                "Sản phẩm đã có đơn hàng nên hệ thống đã CHUYỂN VÀO TRẠNG THÁI NGỪNG BÁN để giữ lịch sử.",
                                Toast.LENGTH_LONG).show();
                    } else if (result == 2) {
                        Toast.makeText(getContext(), "Đã XÓA VĨNH VIỄN sản phẩm khỏi hệ thống.",
                                Toast.LENGTH_SHORT).show();
                    }

                    loadData(); // Load lại danh sách
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private List<String> getCategoryNames(List<Category> list) {
        List<String> names = new ArrayList<>();
        for (Category c : list) names.add(c.getName());
        return names;
    }

    private int getCategoryPosition(List<Category> list, int catId) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == catId) return i;
        }
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Mỗi khi Admin quay lại màn hình này (ví dụ sau khi chỉnh sửa Size hoặc Category)
        // hàm loadData() sẽ chạy để cập nhật lại giao diện ngay lập tức.
        loadData();
    }
}