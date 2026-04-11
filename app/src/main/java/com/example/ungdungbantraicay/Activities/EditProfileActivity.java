package com.example.ungdungbantraicay.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

public class EditProfileActivity extends AppCompatActivity {

    // 1. Khai báo các View
    private EditText edtFullname, edtUsername, edtEmail, edtPhone, edtAddress;
    private Button btnUpdate;

    // 2. Khai báo các đối tượng dữ liệu
    private UserDAO userDAO;
    private User currentUser;
    private String currentUsernamePref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Khởi tạo View
        initViews();

        // Khởi tạo DAO
        userDAO = new UserDAO(this);

        // Lấy thông tin user hiện tại từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        currentUsernamePref = prefs.getString("username", "");

        // Tải dữ liệu lên Form
        loadUserData();

        // Thiết lập sự kiện
        btnUpdate.setOnClickListener(v -> handleUpdate());
    }

    /**
     * Ánh xạ các View từ Layout
     */
    private void initViews() {
        edtFullname = findViewById(R.id.edtFullname);
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    /**
     * Lấy dữ liệu từ Database và hiển thị lên giao diện
     */
    private void loadUserData() {
        currentUser = userDAO.getUserInfo(currentUsernamePref);

        if (currentUser != null) {
            edtFullname.setText(currentUser.getFullname());
            edtUsername.setText(currentUser.getUsername());
            edtEmail.setText(currentUser.getEmail());
            edtPhone.setText(currentUser.getPhone());
            edtAddress.setText(currentUser.getAddress());
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Xử lý logic cập nhật thông tin
     */
    private void handleUpdate() {
        if (currentUser == null) return;

        // 1. Lấy dữ liệu từ EditText
        String newUsername = edtUsername.getText().toString().trim();
        String name = edtFullname.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String oldUsername = currentUser.getUsername();

        // 2. Kiểm tra dữ liệu đầu vào (Validation)
        if (name.isEmpty() || newUsername.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Xử lý logic đổi tên đăng nhập (nếu có)
        if (!newUsername.equals(oldUsername)) {
            if (userDAO.checkUsernameExists(newUsername)) {
                Toast.makeText(this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Cập nhật lại SharedPreferences nếu đổi username thành công
            SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
            pref.edit().putString("username", newUsername).apply();
            currentUser.setUsername(newUsername);
        }

        // 4. Cập nhật đối tượng user
        currentUser.setFullname(name);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        // 5. Ghi vào database
        if (userDAO.updateUserWithId(currentUser)) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Thông báo cho màn hình trước đó nếu cần load lại
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }
}