package com.example.ungdungbantraicay.Fragments;

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

    EditText edtPassword;
    Button btnEditProfile, btnChangePassword, btnLogout;

    UserDAO userDAO;
    boolean isPasswordVisible = false;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtUsername = view.findViewById(R.id.txtUsername);
        txtFullname = view.findViewById(R.id.txtFullname);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtAddress = view.findViewById(R.id.txtAddress);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnLogout = view.findViewById(R.id.btnLogout);


        userDAO = new UserDAO(getActivity());

        loadUserInfo();
        btnEditProfile.setOnClickListener(v -> {

                    Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(intent);

        });
        btnChangePassword.setOnClickListener(v -> {

                    Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                    startActivity(intent);

        });

            btnLogout.setOnClickListener(v -> {

                    SharedPreferences prefs = getActivity()
                            .getSharedPreferences("USER_FILE", getActivity().MODE_PRIVATE);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);

                    getActivity().finish();
            });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUser();
    }

    void loadUser(){

        UserDAO userDAO = new UserDAO(getContext());

        SharedPreferences prefs = getActivity()
                .getSharedPreferences("USER_FILE", getActivity().MODE_PRIVATE);

        String username = prefs.getString("username","");

        User user = userDAO.getUserInfo(username);

        if(user != null){
            txtUsername.setText(user.getUsername());
            txtFullname.setText(user.getFullname());
            txtEmail.setText(user.getEmail());
            txtPhone.setText(user.getPhone());
            txtAddress.setText(user.getAddress());
        }
    }
    private void loadUserInfo(){

        SharedPreferences prefs = getActivity()
                .getSharedPreferences("USER_FILE", getActivity().MODE_PRIVATE);

        String username = prefs.getString("username","");

        Cursor cursor = userDAO.getUserByUsername(username);

        if(cursor != null && cursor.moveToFirst()){

            txtUsername.setText("Username: " + cursor.getString(1));
            txtFullname.setText("Họ tên: " + cursor.getString(3));
            txtEmail.setText("Email: " + cursor.getString(4));
            txtPhone.setText("Phone: " + cursor.getString(5));
            txtAddress.setText("Address: " + cursor.getString(6));

            cursor.close();
        }
    }



    }
