package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.findingboardinghouseapp.Model.Admin;
import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String MY_PREFERENCES = "MyPre";
    public SharedPreferences sharedPreferences;

    BottomNavigationView bottomNavigationView;
    private FirebaseUser firebaseUser;

    @SuppressLint({"NonConstantResourceId", "LogNotTimber"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.main_bottomNavigationView);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.home:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.account:
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                        if (!Objects.equals(firebaseUser.getEmail(), "anhhaovo1710@gmail.com")) {
                            Landlord landlord = new Landlord();

                            landlord.setIdLandlord(sharedPreferences.getString("id", null));
                            landlord.setNameLandlord(sharedPreferences.getString("name", null));
                            landlord.setAddressLandlord(sharedPreferences.getString("address", null));
                            landlord.setPhoneNumberLandlord(sharedPreferences.getString("phoneNumber", null));
                            landlord.setEmailLandlord(sharedPreferences.getString("email", null));
                            landlord.setPasswordLandlord(sharedPreferences.getString("password", null));

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("landlord", landlord);

                            selectedFragment = new AccountFragment();
                            selectedFragment.setArguments(bundle);
                        } else {
                            Admin admin = new Admin();
                            admin.setIdAdmin(sharedPreferences.getString("id", null));
                            admin.setEmailAdmin(sharedPreferences.getString("email", null));
                            admin.setPasswordAdmin(sharedPreferences.getString("password", null));

                            Bundle bundle = new Bundle();
                            bundle.putSerializable("admin", admin);

                            selectedFragment = new AdminFragment();
                            selectedFragment.setArguments(bundle);
                        }
                    } else {
                        selectedFragment = new LogInFragment();
                    }
                    break;
            }
            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout, selectedFragment).commit();
            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}