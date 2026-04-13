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
import com.example.ungdungbantraicay.Helper.DBHelper;
import com.example.ungdungbantraicay.Model.Order;
import com.example.ungdungbantraicay.R;

import java.util.List;


public class OrderFragment extends Fragment {
    private RecyclerView recyclerOrder;
    private OrderDAO orderDAO;
    private List<Order> orderList;
    private OrderAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        recyclerOrder = view.findViewById(R.id.recyclerOrder);
        orderDAO = new OrderDAO(getContext());

        recyclerOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    // Tự động cập nhật lại danh sách khi người dùng quay lại tab này
    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        android.content.SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", android.content.Context.MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);

        if (userId != -1) {
            orderList = orderDAO.getOrdersByUserId(userId);

            // Triển khai cả 2 phương thức của Interface
            adapter = new OrderAdapter(getContext(), orderList, new OrderAdapter.OnOrderClickListener() {
                @Override
                public void onOrderClick(Order order) {
                    Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                    intent.putExtra("order_object", order);
                    startActivity(intent);
                }

                @Override
                public void onCancelClick(Order order) {
                    showConfirmCancelDialog(order);
                }
            });
            recyclerOrder.setAdapter(adapter);
        }
    }

    private void showConfirmCancelDialog(Order order) {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Hủy đơn hàng")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng #" + order.getId() + " không?")
                .setPositiveButton("Hủy đơn", (dialog, which) -> {
                    // Cập nhật trạng thái thành STATUS_CANCELLED (thường là 4)
                    if (orderDAO.updateOrderStatus(order.getId(), DBHelper.STATUS_CANCELLED)) {
                        android.widget.Toast.makeText(getContext(), "Đã hủy đơn hàng thành công", android.widget.Toast.LENGTH_SHORT).show();
                        loadData(); // Tải lại danh sách
                    } else {
                        android.widget.Toast.makeText(getContext(), "Không thể hủy đơn hàng", android.widget.Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Quay lại", null)
                .show();
    }
}