package com.example.ungdungbantraicay.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.example.ungdungbantraicay.Helper.VNPayHelper;
import com.example.ungdungbantraicay.Model.CartItem;
import com.example.ungdungbantraicay.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class CartFragment extends Fragment {
    RecyclerView recyclerCart;
    TextView tvTotalPrice;
    CartDAO cartDAO;
    List<CartItem> cartList;
    CartAdapter adapter;
    int userId;
    private LinearLayout layoutEmptyCart;
    private String tmpName, tmpPhone, tmpAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        layoutEmptyCart = view.findViewById(R.id.layoutEmptyCart);

        Button btnGoHome = view.findViewById(R.id.btnGoHome);

        btnGoHome.setOnClickListener(v -> {
            // Tìm BottomNavigationView bằng đúng ID trong activity_home.xml
            BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottom_nav);

            if (bottomNav != null) {
                // Gọi ID đúng từ file menu: menu_home
                bottomNav.setSelectedItemId(R.id.menu_home);
            }
        });

        recyclerCart = view.findViewById(R.id.recyclerCart);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        cartDAO = new CartDAO(getContext());

        android.content.SharedPreferences pref = getActivity().getSharedPreferences("USER_FILE", Context.MODE_PRIVATE);
        userId = pref.getInt("userId", -1);

        loadData();

        view.findViewById(R.id.btnCheckout).setOnClickListener(v -> handleCheckout());

        return view;
    }

    private void loadData() {
        if (userId != -1) {
            cartList = cartDAO.getItemsByUserId(userId);

            if (cartList == null || cartList.isEmpty()) {
                recyclerCart.setVisibility(View.GONE);
                layoutEmptyCart.setVisibility(View.VISIBLE);
                tvTotalPrice.setText("0 VND");
                return;
            } else {
                recyclerCart.setVisibility(View.VISIBLE);
                layoutEmptyCart.setVisibility(View.GONE);
            }

            // Triển khai Interface mới từ CartAdapter
            adapter = new CartAdapter(getContext(), cartList, new CartAdapter.CartUpdateListener() {
                @Override
                public void onIncreaseQuantity(CartItem item, int position) {
                    int newQty = item.getQuantity() + 1;
                    updateCartQuantity(item, newQty, position);
                }

                @Override
                public void onDecreaseQuantity(CartItem item, int position) {
                    if (item.getQuantity() > 1) {
                        int newQty = item.getQuantity() - 1;
                        updateCartQuantity(item, newQty, position);
                    }
                }

                @Override
                public void onDeleteItem(CartItem item, int position) {
                    deleteCartItem(item, position);
                    loadData();
                }
            });

            recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerCart.setAdapter(adapter);
            calculateTotal();
        }
    }

    // --- CÁC HÀM XỬ LÝ LOGIC NGHIỆP VỤ (Vừa DB vừa UI) ---

    private void updateCartQuantity(CartItem item, int newQty, int position) {
        // 1. Cập nhật SQLite
        cartDAO.updateQuantity(item.getId(), newQty);

        // 2. Cập nhật Object trong list hiện tại
        item.setQuantity(newQty);

        // 3. Báo Adapter vẽ lại đúng dòng đó
        adapter.notifyItemChanged(position);

        // 4. Tính lại tổng tiền trên màn hình
        calculateTotal();
    }

    private void deleteCartItem(CartItem item, int position) {
        // 1. Xóa khỏi SQLite
        cartDAO.deleteItem(item.getId());

        // 2. Xóa khỏi List dữ liệu
        cartList.remove(position);

        // 3. Thông báo hiệu ứng xóa cho RecyclerView
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, cartList.size());

        // 4. Tính lại tổng tiền
        calculateTotal();
    }

    private void calculateTotal() {
        int total = 0;
        if (cartList != null) {
            for (CartItem item : cartList) {
                total += (item.getPrice() * item.getQuantity());
            }
        }
        tvTotalPrice.setText(String.format("%,d VND", total));
    }

    private int calculateTotalValue() {
        int total = 0;
        for (CartItem item : cartList) total += (item.getPrice() * item.getQuantity());
        return total;
    }

    // --- LOGIC THANH TOÁN GIỮ NGUYÊN NHƯNG GỌI HÀM SẠCH SẼ HƠN ---
//    private void handleCheckout() {
//        if (cartList == null || cartList.isEmpty()) {
//            Toast.makeText(getContext(), "Giỏ hàng rỗng!", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        UserDAO userDAO = new UserDAO(getContext());
//        String defaultAddress = userDAO.getAddressByUserId(userId);
//
////        final EditText edtAddress = new EditText(getContext());
////        edtAddress.setHint("Nhập địa chỉ nhận hàng...");
////        edtAddress.setText(defaultAddress);
////
////        FrameLayout container = new FrameLayout(getContext());
////        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
////                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////        params.leftMargin = 50; params.rightMargin = 50;
////        edtAddress.setLayoutParams(params);
////        container.addView(edtAddress);
//
////        new AlertDialog.Builder(getContext())
////                .setTitle("Xác nhận đơn hàng")
////                .setMessage("Kiểm tra lại địa chỉ giao hàng của bạn:")
////                .setView(container)
////                .setPositiveButton("Đặt ngay", (dialog, which) -> {
////                    String finalAddress = edtAddress.getText().toString().trim();
////                    if (finalAddress.isEmpty()) {
////                        Toast.makeText(getContext(), "Vui lòng không để trống địa chỉ!", Toast.LENGTH_SHORT).show();
////                    } else {
////                        performOrder(finalAddress);
////                    }
////                })
////                .setNegativeButton("Hủy", null)
////                .show();
//        LayoutInflater inflater = LayoutInflater.from(getContext());
//        View view = inflater.inflate(R.layout.dialog_checkout, null);
//
//        EditText edtName = view.findViewById(R.id.edtName);
//        EditText edtPhone = view.findViewById(R.id.edtPhone);
//        EditText edtTime = view.findViewById(R.id.edtTime);
//        EditText edtAddress = view.findViewById(R.id.edtAddress);
//        edtAddress.setText(defaultAddress);
//
//
//        AlertDialog dialog = new AlertDialog.Builder(getContext())
//                .setTitle("Xác nhận đơn hàng")
//                .setView(view)
//                .setPositiveButton("Đặt ngay", null)
//                .setNegativeButton("Hủy", null)
//                .create();
//        dialog.show();
//
//        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
//            String name = edtName.getText().toString().trim();
//            String phone = edtPhone.getText().toString().trim();
//            String address = edtAddress.getText().toString().trim();
//            String time = edtTime.getText().toString().trim();
//
//            if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || time.isEmpty()) {
//                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            else {
//                performOrder(address);
//            }
//            dialog.dismiss();
//        });
//    }

private void handleCheckout() {
    if (cartList == null || cartList.isEmpty()) {
        Toast.makeText(getContext(), "Giỏ hàng rỗng!", Toast.LENGTH_SHORT).show();
        return;
    }

    UserDAO userDAO = new UserDAO(getContext());
    String defaultAddress = userDAO.getAddressByUserId(userId);

    LayoutInflater inflater = LayoutInflater.from(getContext());
    View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_checkout, null);
    EditText edtName = dialogView.findViewById(R.id.edtName);
    EditText edtPhone = dialogView.findViewById(R.id.edtPhone);
    EditText edtAddress = dialogView.findViewById(R.id.edtAddress);
    RadioButton rbVNPay = dialogView.findViewById(R.id.rbVNPay); // Nút VNPay

    AlertDialog dialog = new AlertDialog.Builder(getContext())
            .setTitle("Xác nhận đơn hàng")
            .setView(dialogView)
            .setPositiveButton("Xác nhận", null)
            .setNegativeButton("Hủy", null)
            .create();

    dialog.show();

// TRONG CartFragment.java -> handleCheckout()
    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
        tmpName = edtName.getText().toString().trim();
        tmpPhone = edtPhone.getText().toString().trim();
        tmpAddress = edtAddress.getText().toString().trim();

        // 1. Kiểm tra trống
        if (tmpName.isEmpty() || tmpPhone.isEmpty() || tmpAddress.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Kiểm tra độ dài số điện thoại (VD: từ 10-11 số)
        if (tmpPhone.length() < 10 || tmpPhone.length() > 11) {
            edtPhone.setError("Số điện thoại phải có 10-11 chữ số!");
            return;
        }

        if (rbVNPay != null && rbVNPay.isChecked()) {
            // --- BƯỚC QUAN TRỌNG NHẤT: Lưu vào bộ nhớ tạm ---
            android.content.SharedPreferences tempPref = getActivity().getSharedPreferences("TEMP_ORDER", Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = tempPref.edit();
            editor.putString("name", tmpName);
            editor.putString("phone", tmpPhone);
            editor.putString("address", tmpAddress);
            editor.apply(); // Lưu ngay lập tức

            // Sau đó mới mở VNPay
            String url = VNPayHelper.createPaymentUrl(calculateTotalValue(), "ORDER_" + System.currentTimeMillis());
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } else {
            performOrder(tmpAddress, tmpName, tmpPhone, 0);
        }
        dialog.dismiss();
    });
}
    private void performOrder(String address, String name, String phone, int paymentMethod) {
        OrderDAO orderDAO = new OrderDAO(getContext());
        int total = calculateTotalValue();

        // Truyền đủ 7 tham số
        boolean isSuccess = orderDAO.placeOrder(userId, total, address, name, phone, paymentMethod, cartList);

        if (isSuccess) {
            Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
            cartList.clear();
            adapter.notifyDataSetChanged();
            calculateTotal();
        } else {
            Toast.makeText(getContext(), "Lỗi hệ thống, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }}

