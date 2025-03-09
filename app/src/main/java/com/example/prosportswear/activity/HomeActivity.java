package com.example.prosportswear.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.prosportswear.adapter.CarouselAdapter;
import com.example.prosportswear.R;


public class HomeActivity extends AppCompatActivity {

    Button logout, home, store, cart, profile;
    private ViewPager2 viewPager;
    private int[] images = {R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground}; // Add your drawable images here
    private Handler handler = new Handler();
    private Runnable autoScrollRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize buttons
        logout = findViewById(R.id.logoutBtn);
        profile = findViewById(R.id.profile_button);
        cart = findViewById(R.id.cart_button);
        store = findViewById(R.id.store_button);
        home = findViewById(R.id.home_button);

        // Initialize ViewPager2 for carousel
        viewPager = findViewById(R.id.carouselRecyclerView);
        CarouselAdapter adapter = new CarouselAdapter(images);
        viewPager.setAdapter(adapter);

        // Set up smooth scrolling and initial item
        viewPager.setOffscreenPageLimit(3);
        viewPager.setCurrentItem(0);

        // Start auto-scroll for the carousel
        autoScrollCarousel();

        // Profile Button Click
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Cart Button Click
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Store Button Click
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, StoreActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Logout Button Click
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LogoutActivity.class);
                startActivity(intent);
                finish(); // Close HomeActivity
            }
        });
    }

    // Auto-scroll functionality for ViewPager2
    private void autoScrollCarousel() {
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % images.length; // Loop back to the first item
                viewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 3000); // Scroll every 3 seconds
            }
        };
        handler.postDelayed(autoScrollRunnable, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacks(autoScrollRunnable);
    }
}
