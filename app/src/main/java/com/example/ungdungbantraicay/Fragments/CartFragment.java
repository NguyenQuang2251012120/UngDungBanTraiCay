package com.example.ungdungbantraicay.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ungdungbantraicay.Adapter.CartAdapter;
import com.example.ungdungbantraicay.DAO.CartDAO;
import com.example.ungdungbantraicay.DAO.OrderDAO;
import com.example.ungdungbantraicay.DAO.UserDAO;
import com.example.ungdungbantraicay.Model.CartItem;
import com.example.ungdungbantraicay.R;

import java.util.List;

public class CartFragment extends Fragment {
    RecyclerView recyclerCart;
    TextView tvTotalPrice;
    CartDAO cartDAO;
    List<CartItem> cartList;
    CartAdapter adapter;
    int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerCart = view.findViewById(R.id.recyclerCart);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        cartDAO = new CartDAO(getContext());

        android.content.SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", Context.MODE_PRIVATE);
        userId = pref.getInt("userId", -1);

        loadData();

        view.findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            handleCheckout();
        });

        return view;
    }

    private void loadData() {
        if (userId != -1) {
            cartList = cartDAO.getItemsByUserId(userId);
            adapter = new CartAdapter(getContext(), cartList, this::calculateTotal);
            recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerCart.setAdapter(adapter);
            calculateTotal();
        }
    }

    private int calculateTotalValue() {
        int total = 0;
        if (cartList != null) {
            for (CartItem item : cartList) {
                total += (item.getPrice() * item.getQuantity());
            }
        }
        return total;
    }

    private void calculateTotal() {
        int total = calculateTotalValue();
        tvTotalPrice.setText(String.format("%,d VND", total));
    }

    // --- LOGIC THANH TOÁN CÓ ĐỊA CHỈ ---
    private void handleCheckout() {
        if (cartList == null || cartList.isEmpty()) {
            Toast.makeText(getContext(), "Giỏ hàng rỗng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Lấy địa chỉ mặc định của User từ Database
        UserDAO userDAO = new UserDAO(getContext());
        String defaultAddress = userDAO.getAddressByUserId(userId);

        // 2. Tạo EditText để khách nhập/sửa địa chỉ
        final EditText edtAddress = new EditText(getContext());
        edtAddress.setHint("Nhập địa chỉ nhận hàng...");
        edtAddress.setText(defaultAddress); // Tự điền địa chỉ cũ nếu có

        // Thêm lề (padding) cho EditText để giao diện cân đối
        FrameLayout container = new FrameLayout(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 50; params.rightMargin = 50;
        edtAddress.setLayoutParams(params);
        container.addView(edtAddress);

        // 3. Hiện Dialog xác nhận
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận đơn hàng")
                .setMessage("Kiểm tra lại địa chỉ giao hàng của bạn:")
                .setView(container) // Đưa ô nhập địa chỉ vào Dialog
                .setPositiveButton("Đặt ngay", (dialog, which) -> {
                    String finalAddress = edtAddress.getText().toString().trim();
                    if (finalAddress.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng không để trống địa chỉ!", Toast.LENGTH_SHORT).show();
                    } else {
                        performOrder(finalAddress);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performOrder(String finalAddress) {
        OrderDAO orderDAO = new OrderDAO(getContext());
        int total = calculateTotalValue();

        // Gửi địa chỉ thật từ Dialog vào hàm placeOrder
        boolean isSuccess = orderDAO.placeOrder(userId, total, finalAddress, cartList);

        if (isSuccess) {
            Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
            cartList.clear();
            adapter.notifyDataSetChanged();
            calculateTotal();
        } else {
            Toast.makeText(getContext(), "Lỗi hệ thống, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }
}