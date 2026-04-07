package com.example.ungdungbantraicay.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

public class LoginActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword;
    Button btnLogin;
    TextView txtRegister;
    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDAO = new UserDAO(this);

        // 1. KIỂM TRA ĐĂNG NHẬP TỰ ĐỘNG
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String savedUser = pref.getString("username", "");

        if (!savedUser.isEmpty()) {
            User user = userDAO.getUserInfo(savedUser);
            if (user != null && user.getStatus() == 1) {
                // CHỈNH SỬA TẠI ĐÂY: Truyền role vào để điều hướng đúng
                navigateToHome(user.getRole());
                return;
            } else {
                pref.edit().clear().apply();
                if (user != null && user.getStatus() == 0) {
                    Toast.makeText(this, "Tài khoản đã bị khóa", Toast.LENGTH_LONG).show();
                }
            }
        }

        setContentView(R.layout.activity_login);

        // 2. ÁNH XẠ
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        // 3. SỰ KIỆN
        btnLogin.setOnClickListener(v -> handleLogin());
        txtRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void handleLogin() {
        String userStr = edtUsername.getText().toString().trim();
        String passStr = edtPassword.getText().toString().trim();

        if (userStr.isEmpty() || passStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = userDAO.login(userStr, passStr);

        if (user != null) {
            if (user.getStatus() == 1) {
                saveSession(user);
                Toast.makeText(this, "Chào mừng " + user.getFullname(), Toast.LENGTH_SHORT).show();

                // CHỈNH SỬA TẠI ĐÂY: Điều hướng dựa trên role của user vừa login
                navigateToHome(user.getRole());
            } else {
                showLockedAccountDialog();
            }
        } else {
            Toast.makeText(this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveSession(User user) {
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("username", user.getUsername());
        editor.putInt("userId", user.getId());
        editor.putString("role", user.getRole());
        editor.apply();
    }

    private void showLockedAccountDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Tài khoản bị vô hiệu hóa")
                .setMessage("Tài khoản này đã bị xóa hoặc bị khóa bởi hệ thống. Vui lòng liên hệ với bộ phận hỗ trợ để biết thêm chi tiết.")
                .setPositiveButton("Đã hiểu", null)
                .show();
    }

    // CẬP NHẬT PHƯƠNG THỨC NÀY
    private void navigateToHome(String role) {
        Intent intent;
        if ("admin".equalsIgnoreCase(role)) {
            // Nếu là admin thì đi tới AdminMainActivity
            intent = new Intent(this, AdminMainActivity.class);
        } else {
            // Nếu là user (hoặc vai trò khác) thì đi tới HomeActivity của người dùng
            intent = new Intent(this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
