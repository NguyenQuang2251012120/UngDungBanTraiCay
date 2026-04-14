package com.example.ungdungbantraicay.AdminFragments;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.ungdungbantraicay.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);

        // Ánh xạ
        bottomNav = findViewById(R.id.admin_bottom_nav);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Thiết lập Fragment mặc định khi vừa vào (Ví dụ: Quản lý trái cây)
        loadFragment(new AdminFruitFragment());
        bottomNav.setSelectedItemId(R.id.menu_fruit);

        // Xử lý sự kiện click menu
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.menu_user) {
                selectedFragment = new AdminUserFragment();
            } else if (id == R.id.menu_fruit) {
                selectedFragment = new AdminFruitFragment();
            } else if (id == R.id.menu_catergory) {
                selectedFragment = new AdminCategoryFragment();
            } else if (id == R.id.menu_order) {
                selectedFragment = new AdminOrderFragment();
            } else if (id == R.id.menu_profile) {
                selectedFragment = new AdminProfileFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });
    }

    // Hàm bổ trợ để thay đổi Fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.admin_fragment_container, fragment)
                .commit();
    }
}