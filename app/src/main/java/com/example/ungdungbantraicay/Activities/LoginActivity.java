package com.example.ungdungbantraicay.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

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
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        userDAO = new UserDAO(this);

        btnLogin.setOnClickListener(v -> login());

        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void login(){

        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        if(userDAO.checkLogin(username,password)){

            // LƯU USER ĐANG ĐĂNG NHẬP
            SharedPreferences preferences = getSharedPreferences("USER_FILE", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("username", username);
            editor.apply();

            Toast.makeText(this,"Đăng nhập thành công",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

        }else{
            Toast.makeText(this,"Sai tài khoản hoặc mật khẩu",Toast.LENGTH_SHORT).show();
        }
    }
}