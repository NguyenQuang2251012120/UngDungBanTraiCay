package com.example.ungdungbantraicay.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ungdungbantraicay.DAO.CartDAO;
import com.example.ungdungbantraicay.DAO.OrderDAO;
import com.example.ungdungbantraicay.Fragments.CartFragment;
import com.example.ungdungbantraicay.Fragments.HomeFragment;
import com.example.ungdungbantraicay.Fragments.OrderFragment;
import com.example.ungdungbantraicay.Fragments.ProductFragment;
import com.example.ungdungbantraicay.Fragments.ProfileFragment;
import com.example.ungdungbantraicay.Model.CartItem;
import com.example.ungdungbantraicay.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.bottom_nav);

        // Fragment mặc định khi mở app
        loadFragment(new HomeFragment());

        // Kiểm tra kết quả VNPay ngay khi Activity khởi tạo
        handleVNPayResult(getIntent());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            if(item.getItemId() == R.id.menu_home){
                fragment = new HomeFragment();
            }
            else if(item.getItemId() == R.id.menu_search){
                fragment = new ProductFragment();
            }
            else if(item.getItemId() == R.id.menu_cart){
                fragment = new CartFragment();
            }
            else if(item.getItemId() == R.id.menu_order){
                fragment = new OrderFragment();
            }
            else if(item.getItemId() == R.id.menu_profile){
                fragment = new ProfileFragment();
            }

            if(fragment != null){
                loadFragment(fragment);
            }
            return true;
        });
    }

    // Xử lý khi trình duyệt gọi ngược lại App (Activity đang chạy ngầm)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleVNPayResult(intent);
    }

    private void handleVNPayResult(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && "vnpay".equals(uri.getScheme())) {
            String responseCode = uri.getQueryParameter("vnp_ResponseCode");

            if ("00".equals(responseCode)) {
                android.content.SharedPreferences tempPref = getSharedPreferences("TEMP_ORDER", MODE_PRIVATE);
                String name = tempPref.getString("name", "");
                String phone = tempPref.getString("phone", "");
                String address = tempPref.getString("address", "");

                // LOG ĐỂ KIỂM TRA
                android.util.Log.d("VNPAY_DEBUG", "Lấy dữ liệu tạm: " + name + " | " + phone);

                if (!name.isEmpty()) {
                    processOrderAfterPayment(address, name, phone);
                } else {
                    Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người nhận (TEMP_ORDER empty)", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Giao dịch thất bại. Mã lỗi: " + responseCode, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void processOrderAfterPayment(String address, String name, String phone) {
        OrderDAO orderDAO = new OrderDAO(this);
        CartDAO cartDAO = new CartDAO(this);

        int userId = getSharedPreferences("USER_FILE", MODE_PRIVATE).getInt("userId", -1);
        List<CartItem> items = cartDAO.getItemsByUserId(userId);

        if (userId == -1 || items == null || items.isEmpty()) {
            android.util.Log.e("VNPAY_DEBUG", "UserId: " + userId + " | CartItems size: " + (items != null ? items.size() : "null"));
            Toast.makeText(this, "Lỗi: Giỏ hàng trống hoặc chưa đăng nhập!", Toast.LENGTH_LONG).show();
            return;
        }

        int total = 0;
        for (CartItem i : items) total += (i.getPrice() * i.getQuantity());

        boolean success = orderDAO.placeOrder(userId, total, address, name, phone, 1, items);

        if (success) {
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_LONG).show();
            getSharedPreferences("TEMP_ORDER", MODE_PRIVATE).edit().clear().apply();

            // CHỈ CẦN DÒNG NÀY: Nó sẽ tự bôi xanh icon Hóa đơn và tự load OrderFragment
            bottomNav.setSelectedItemId(R.id.menu_order);

        } else {
            Toast.makeText(this, "Lỗi khi lưu đơn hàng!", Toast.LENGTH_SHORT).show();
        }
    }
//        boolean success = orderDAO.placeOrder(userId, total, address, name, phone, 1, items);
//
//        if (success) {
//            Toast.makeText(this, "Thanh toán thành công! Đơn hàng đã được tạo.", Toast.LENGTH_LONG).show();
//            getSharedPreferences("TEMP_ORDER", MODE_PRIVATE).edit().clear().apply();
//            bottomNav.setSelectedItemId(R.id.menu_order);
//            loadFragment(new OrderFragment());
//        } else {
//            Toast.makeText(this, "Lỗi khi lưu đơn hàng vào Database!", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }
}