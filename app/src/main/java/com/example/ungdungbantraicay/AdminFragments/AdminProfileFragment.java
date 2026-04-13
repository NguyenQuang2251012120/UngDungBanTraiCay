package com.example.ungdungbantraicay.AdminFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.example.ungdungbantraicay.Activities.LoginActivity;
import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

public class AdminProfileFragment extends Fragment {

    TextView txtUsername, txtFullname, txtEmail, txtPhone, txtAddress;
    Button btnEditProfile, btnChangePassword, btnLogout;
    UserDAO userDAO;

    public AdminProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        // Ánh xạ View chính xác với XML
        txtUsername = view.findViewById(R.id.txtUsername);
        txtFullname = view.findViewById(R.id.txtFullname);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtAddress = view.findViewById(R.id.txtAddress);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        userDAO = new UserDAO(getActivity());

        // Load dữ liệu lần đầu
        loadUser();

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AdminEditProfileActivity.class));
        });

        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AdminChangePasswordActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        SharedPreferences prefs = getActivity().getSharedPreferences("USER_FILE", Context.MODE_PRIVATE);
                        prefs.edit().clear().apply();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUser(); // Cập nhật lại nếu người dùng vừa sửa thông tin ở màn hình khác quay về
    }

    private void loadUser() {
        SharedPreferences prefs = getActivity().getSharedPreferences("USER_FILE", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "");

        User user = userDAO.getUserInfo(username);

        if (user != null) {
            txtUsername.setText("@" + user.getUsername());
            txtFullname.setText(user.getFullname());
            txtEmail.setText(user.getEmail());
            txtPhone.setText(user.getPhone());
            txtAddress.setText(user.getAddress());
        }
    }
}
