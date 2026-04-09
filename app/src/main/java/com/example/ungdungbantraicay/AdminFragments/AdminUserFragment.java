package com.example.ungdungbantraicay.AdminFragments;

import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.*;
import android.widget.Button;
import com.example.ungdungbantraicay.Adapter.UserAdapter;
import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.User;
import com.example.ungdungbantraicay.R;
import com.example.ungdungbantraicay.Activities.AddUserActivity;
import android.content.Intent;
import android.widget.EditText;

import java.util.*;

public class AdminUserFragment extends Fragment {

    RecyclerView recycler;
    Button btnAdd;
    ArrayList<User> list;
    UserAdapter adapter;
    UserDAO dao;

    EditText edtSearch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_admin_user, container, false);

        recycler = view.findViewById(R.id.recyclerUser);
        btnAdd = view.findViewById(R.id.btnAddUser);

        dao = new UserDAO(getContext());
        list = new ArrayList<>();

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(getContext(), list);
        recycler.setAdapter(adapter);

        loadData();

        btnAdd.setOnClickListener(v ->
                startActivity(new Intent(getContext(), AddUserActivity.class)));

        return view;
    }

    private void loadData() {
        Cursor c = dao.getAllUsers();
        list.clear();

        while (c.moveToNext()) {
            list.add(new User(
                    c.getInt(c.getColumnIndexOrThrow("id")),
                    c.getString(c.getColumnIndexOrThrow("username")),
                    c.getString(c.getColumnIndexOrThrow("password")),
                    c.getString(c.getColumnIndexOrThrow("fullname")),
                    c.getString(c.getColumnIndexOrThrow("email")),
                    c.getString(c.getColumnIndexOrThrow("phone")),
                    c.getString(c.getColumnIndexOrThrow("address")),
                    c.getString(c.getColumnIndexOrThrow("role")),
                    c.getInt(c.getColumnIndexOrThrow("status"))
            ));
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}