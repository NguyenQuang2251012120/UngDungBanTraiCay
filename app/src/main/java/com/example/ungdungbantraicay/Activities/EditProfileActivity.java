package com.example.ungdungbantraicay.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditProfileActivity extends AppCompatActivity {

    EditText edtFullname, edtEmail, edtPhone, edtAddress;
    Button btnUpdate;
    UserDAO userDAO;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // ánh xạ view trước
        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnUpdate = findViewById(R.id.btnUpdate);

        userDAO = new UserDAO(this);

        SharedPreferences prefs = getSharedPreferences("USER_FILE",MODE_PRIVATE);
        String username = prefs.getString("username","");

        user = userDAO.getUserInfo(username);

        if(user != null){
            edtFullname.setText(user.getFullname());
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getPhone());
            edtAddress.setText(user.getAddress());
        }

        btnUpdate.setOnClickListener(v -> {

            user.setFullname(edtFullname.getText().toString());
            user.setEmail(edtEmail.getText().toString());
            user.setPhone(edtPhone.getText().toString());
            user.setAddress(edtAddress.getText().toString());

            boolean result = userDAO.updateUser(user);

            if(result){
                Toast.makeText(this,"Cập nhật thành công",Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(this,"Cập nhật thất bại",Toast.LENGTH_SHORT).show();
            }
        });
    }
}