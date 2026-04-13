package com.example.ungdungbantraicay.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.example.ungdungbantraicay.Activities.ChangePasswordActivity;
import com.example.ungdungbantraicay.Activities.EditProfileActivity;
import com.example.ungdungbantraicay.Activities.LoginActivity;
import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;

public class ProfileFragment extends Fragment {

    TextView txtUsername, txtFullname, txtEmail, txtPhone, txtAddress;
    Button btnEditProfile, btnChangePassword, btnLogout, btnDeleteAccount; // Thêm btnDeleteAccount
    UserDAO userDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // --- Ánh xạ các View cũ giữ nguyên ---
        txtUsername = view.findViewById(R.id.txtUsername);
        txtFullname = view.findViewById(R.id.txtFullname);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtAddress = view.findViewById(R.id.txtAddress);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);

        // --- Ánh xạ nút xóa mới ---
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);

        userDAO = new UserDAO(getActivity());
        loadUser();

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), EditProfileActivity.class));
        });

        btnChangePassword.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
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

        btnDeleteAccount.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                    .setTitle("CẢNH BÁO!")
                    .setMessage("Bạn có chắc chắn muốn xóa tài khoản này? Mọi dữ liệu sẽ biến mất vĩnh viễn!")
                    .setPositiveButton("Xóa luôn", (dialog, which) -> {
                        // 1. Lấy thông tin user hiện tại
                        SharedPreferences prefs = getActivity().getSharedPreferences("USER_FILE", Context.MODE_PRIVATE);
                        String username = prefs.getString("username", "");
                        User currentUser = userDAO.getUserInfo(username);

                        if (currentUser != null) {
                            // 2. Gọi DAO để xóa trong SQLite
                            if (userDAO.deleteUser(currentUser.getId())) {
                                // 3. Xóa sạch Session (SharedPreferences)
                                prefs.edit().clear().apply();

                                // 4. Đuổi ra màn hình Login và xóa sạch lịch sử chạy (Back không quay lại được)
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                                Toast.makeText(getActivity(), "Tài khoản của bạn đã bị xóa!", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Không thể xóa (có thể bạn đã có đơn hàng)!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
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
