package com.example.findingboardinghouseapp.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public static final String MY_PREFERENCES = "MyPre";
    public SharedPreferences sharedPreferences;
    public BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.home:
                        selectedFragment = new HomeFragment();
                        break;
//                    case R.id.search:
//                        selectedFragment = new SearchFragment();
//                        break;
                    case R.id.account:
                        Landlord landlord = new Landlord();
                        if (sharedPreferences.contains("email") && sharedPreferences.contains("password")) {
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
//                            FragmentTransaction fragmentTransaction = ((FragmentActivity) getApplicationContext()).getSupportFragmentManager().beginTransaction();
//                            fragmentTransaction.replace(R.id.frame_layout, selectedFragment);
//                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                            fragmentTransaction.commit();
                        } else {
                            selectedFragment = new LogInFragment();
                        }
                        break;


                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, selectedFragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.home);
    }
}