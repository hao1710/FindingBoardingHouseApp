package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

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

    int flag = -1;
    @SuppressLint({"NonConstantResourceId", "LogNotTimber"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.main_bottomNavigationView);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    if (flag != 0) {
                        flag = 0;
                        Log.i("Flag", "home");
                        Fragment selectedFragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout, selectedFragment).commit();
                    }
                    break;
                case R.id.account:
                    if (flag != 1) {
                        flag = 1;
                        Log.i("Flag", "account");
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

                                Fragment selectedFragment = new AccountFragment();
                                selectedFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout, selectedFragment).commit();

                            } else {
                                Admin admin = new Admin();
                                admin.setIdAdmin(sharedPreferences.getString("id", null));
                                admin.setEmailAdmin(sharedPreferences.getString("email", null));
                                admin.setPasswordAdmin(sharedPreferences.getString("password", null));

                                Bundle bundle = new Bundle();
                                bundle.putSerializable("admin", admin);

                                Fragment selectedFragment = new AdminFragment();
                                selectedFragment.setArguments(bundle);
                                getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout, selectedFragment).commit();

                            }
                        } else {
                            Fragment selectedFragment = new LogInFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_frameLayout, selectedFragment).commit();

                        }
                    }

                    break;
            }

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