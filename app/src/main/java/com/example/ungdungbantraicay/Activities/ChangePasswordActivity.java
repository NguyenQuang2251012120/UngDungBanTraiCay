package com.example.ungdungbantraicay.Activities;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    Button btnChangePassword;
    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edtOldPassword = findViewById(R.id.edtOldPassword);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        userDAO = new UserDAO(this);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(v -> {

            String oldPass = edtOldPassword.getText().toString();
            String newPass = edtNewPassword.getText().toString();
            String confirm = edtConfirmPassword.getText().toString();

            SharedPreferences prefs = getSharedPreferences("USER_FILE",MODE_PRIVATE);
            String username = prefs.getString("username","");

            User user = userDAO.getUserInfo(username);

            if(!oldPass.equals(user.getPassword())){
                Toast.makeText(this,"Sai mật khẩu cũ",Toast.LENGTH_SHORT).show();
                return;
            }

            if(!newPass.equals(confirm)){
                Toast.makeText(this,"Mật khẩu không khớp",Toast.LENGTH_SHORT).show();
                return;
            }

            boolean result = userDAO.changePassword(username,newPass);

            if(result){
                Toast.makeText(this,"Đổi mật khẩu thành công",Toast.LENGTH_SHORT).show();
                finish();
            }

        });
    }
}