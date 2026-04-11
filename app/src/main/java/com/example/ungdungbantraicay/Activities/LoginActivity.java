package com.example.ungdungbantraicay.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ungdungbantraicay.AdminFragments.AdminMainActivity;
import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

public class LoginActivity extends AppCompatActivity {

    // 1. Khai báo các View
    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView txtRegister;

    // 2. Khai báo các đối tượng hỗ trợ
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo DAO trước khi kiểm tra auto-login
        userDAO = new UserDAO(this);

        // Bước 1: Kiểm tra đăng nhập tự động (Xử lý trước khi setContentView để tránh giật màn hình)
        checkAutoLogin();

        setContentView(R.layout.activity_login);

        // Bước 2: Ánh xạ View
        initViews();

        // Bước 3: Thiết lập sự kiện
        initEvents();
    }

    /**
     * Ánh xạ các thành phần giao diện
     */
    private void initViews() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
    }

    /**
     * Thiết lập các sự kiện click
     */
    private void initEvents() {
        btnLogin.setOnClickListener(v -> handleLogin());

        txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    /**
     * Kiểm tra phiên đăng nhập cũ trong SharedPreferences
     */
    private void checkAutoLogin() {
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String savedUser = pref.getString("username", "");

        if (!savedUser.isEmpty()) {
            User user = userDAO.getUserInfo(savedUser);
            if (user != null && user.getStatus() == 1) {
                navigateToHome(user.getRole());
            } else {
                // Nếu user bị khóa hoặc không tồn tại, xóa session cũ
                pref.edit().clear().apply();
                if (user != null && user.getStatus() == 0) {
                    Toast.makeText(this, "Tài khoản của bạn đã bị khóa", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Xử lý logic khi bấm nút Đăng nhập
     */
    private void handleLogin() {
        String userStr = edtUsername.getText().toString().trim();
        String passStr = edtPassword.getText().toString().trim();

        if (userStr.isEmpty() || passStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = userDAO.login(userStr, passStr);

        if (user != null) {
            if (user.getStatus() == 1) {
                saveSession(user);
                Toast.makeText(this, "Chào mừng " + user.getFullname(), Toast.LENGTH_SHORT).show();
                navigateToHome(user.getRole());
            } else {
                showLockedAccountDialog();
            }
        } else {
            Toast.makeText(this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Lưu thông tin người dùng vào bộ nhớ tạm
     */
    private void saveSession(User user) {
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", user.getUsername());
        editor.putInt("userId", user.getId());
        editor.putString("role", user.getRole());
        editor.apply();
    }

    /**
     * Điều hướng người dùng dựa trên vai trò (Role)
     */
    private void navigateToHome(String role) {
        Intent intent;
        if ("admin".equalsIgnoreCase(role)) {
            intent = new Intent(this, AdminMainActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        startActivity(intent);
        finish(); // Kết thúc LoginActivity để không quay lại được bằng nút Back
    }

    /**
     * Hiển thị thông báo khi tài khoản bị khóa
     */
    private void showLockedAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Tài khoản bị vô hiệu hóa")
                .setMessage("Tài khoản này đã bị khóa bởi hệ thống. Vui lòng liên hệ hỗ trợ.")
                .setPositiveButton("Đã hiểu", null)
                .show();
    }
}