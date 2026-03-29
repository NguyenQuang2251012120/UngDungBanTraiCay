package com.example.ungdungbantraicay.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ungdungbantraicay.Fragments.CartFragment;
import com.example.ungdungbantraicay.Fragments.HomeFragment;
import com.example.ungdungbantraicay.Fragments.ProductFragment;
import com.example.ungdungbantraicay.Fragments.ProfileFragment;
import com.example.ungdungbantraicay.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNav = findViewById(R.id.bottom_nav);

        // Fragment mặc định khi mở app
        loadFragment(new HomeFragment());

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

            else if(item.getItemId() == R.id.menu_profile){
                fragment = new ProfileFragment();
            }

            if(fragment != null){
                loadFragment(fragment);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();
    }
}