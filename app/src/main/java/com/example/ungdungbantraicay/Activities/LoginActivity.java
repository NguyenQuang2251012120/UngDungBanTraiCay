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
import com.example.ungdungbantraicay.R;

public class LoginActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword;
    Button btnLogin;
    TextView txtRegister;
    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. KIỂM TRA ĐĂNG NHẬP TỰ ĐỘNG (Auto-login)
        SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
        String savedUser = pref.getString("username", "");
        if (!savedUser.isEmpty()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        // 2. ÁNH XẠ
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        userDAO = new UserDAO(this);

        // 3. XỬ LÝ SỰ KIỆN
        btnLogin.setOnClickListener(v -> handleLogin());

        txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void handleLogin() {
        String user = edtUsername.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đủ tài khoản & mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userDAO.checkLogin(user, pass)) {
            // LƯU PHIÊN ĐĂNG NHẬP
            SharedPreferences pref = getSharedPreferences("USER_FILE", MODE_PRIVATE);
            pref.edit().putString("username", user).apply();

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
        }
    }
}