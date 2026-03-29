package com.example.ungdungbantraicay.Activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.R;
import com.example.ungdungbantraicay.Helper.DBHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUsername, edtPassword, edtConfirm, edtFullname, edtEmail, edtPhone, edtAddress;
    Button btnRegister;

    DBHelper dbHelper;
    UserDAO userDAO;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);
        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnRegister = findViewById(R.id.btnRegister);
        userDAO = new UserDAO(this);
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        btnRegister.setOnClickListener(v -> register());
    }

    private void register(){

        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();
        String confirm = edtConfirm.getText().toString();
        String fullname = edtFullname.getText().toString();
        String email = edtEmail.getText().toString();
        String phone = edtPhone.getText().toString();
        String address = edtAddress.getText().toString();

        if(!password.equals(confirm)){
            Toast.makeText(this,"Mật khẩu không khớp",Toast.LENGTH_SHORT).show();
            return;
        }

        if(userDAO.checkUsername(username)){
            Toast.makeText(this,"Username đã tồn tại",Toast.LENGTH_SHORT).show();
            return;
        }

        userDAO.insertUser(username,password,fullname,email,phone,address);

        Toast.makeText(this,"Đăng ký thành công",Toast.LENGTH_SHORT).show();
        finish();
    }}