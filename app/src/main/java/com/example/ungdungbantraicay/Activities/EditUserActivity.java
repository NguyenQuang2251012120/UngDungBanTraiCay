package com.example.ungdungbantraicay.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

public class EditUserActivity extends AppCompatActivity {
    EditText edtUsername, edtFullname, edtEmail, edtPhone, edtAddress;
    Button btnSave;
    User user;
    UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        // Ánh xạ view
        edtUsername = findViewById(R.id.edtUsername);
        edtFullname = findViewById(R.id.edtFullname);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnSave = findViewById(R.id.btnSave);

        userDAO = new UserDAO(this);

        user = (User) getIntent().getSerializableExtra("user");

        if (user != null) {
            edtUsername.setText(user.getUsername());
            edtFullname.setText(user.getFullname());
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getPhone());
            edtAddress.setText(user.getAddress());
        }

        btnSave.setOnClickListener(v -> updateUser());
    }

    private void updateUser() {
        String fullname = edtFullname.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (fullname.isEmpty()) {
            Toast.makeText(this, "Nhập họ tên", Toast.LENGTH_SHORT).show();
            return;
        }

        user.setFullname(fullname);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);

        boolean result = userDAO.updateUser(user);

        if (result) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}