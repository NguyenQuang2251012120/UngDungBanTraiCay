package com.example.ungdungbantraicay.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ungdungbantraicay.Activities.OrderDetailActivity;
import com.example.ungdungbantraicay.Adapter.OrderAdapter;
import com.example.ungdungbantraicay.DAO.OrderDAO;
import com.example.ungdungbantraicay.Model.Order;
import com.example.ungdungbantraicay.R;

import java.util.List;


public class OrderFragment extends Fragment {
    private RecyclerView recyclerOrder;
    private OrderDAO orderDAO;
    private List<Order> orderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        recyclerOrder = view.findViewById(R.id.recyclerOrder);
        orderDAO = new OrderDAO(getContext());

        // Lấy userId từ SharedPreferences
        android.content.SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", android.content.Context.MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);

        if (userId != -1) {
            orderList = orderDAO.getOrdersByUserId(userId);

            // THÊM THAM SỐ THỨ 3 (Xử lý click)
            OrderAdapter adapter = new OrderAdapter(getContext(), orderList, order -> {
                // Mở màn hình chi tiết khi nhấn vào đơn hàng
                Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                intent.putExtra("order_object", order);
                startActivity(intent);
            });

            recyclerOrder.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerOrder.setAdapter(adapter);
        }
        return view;
    }
}