package com.example.changehome.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.changehome.R;
import com.example.changehome.fragments.AddFragment;
import com.example.changehome.fragments.SearchFragment;
import com.example.changehome.fragments.ContactFragment;
import com.example.changehome.fragments.SettingsFragment;
import com.example.changehome.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_menu);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Cargar fragmento inicial
        if (savedInstanceState == null) {
            currentFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, currentFragment)
                    .commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    int id = item.getItemId();

                    if (id == R.id.nav_home) {
                        selectedFragment = new HomeFragment();
                    } else if (id == R.id.nav_customers) {
                        selectedFragment = new ContactFragment();
                    } else if (id == R.id.nav_search) {
                        selectedFragment = new SearchFragment();
                    } else if (id == R.id.nav_add) {
                        // CORREGIDO: Ahora lleva a AddFragment
                        selectedFragment = new AddFragment();
                    } else if (id == R.id.nav_settings) {
                        // CORREGIDO: Ahora lleva a SettingsFragment
                        selectedFragment = new SettingsFragment();
                    } else {
                        return false;
                    }

                    // Evitar recargar el mismo fragment
                    if (currentFragment != null && currentFragment.getClass() == selectedFragment.getClass()) {
                        return true;
                    }

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();

                    currentFragment = selectedFragment;
                    return true;
                }
            };
}