package com.example.findingboardinghouseapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;

public class AccountSettingActivity extends AppCompatActivity {
    private TextView textViewNameLandlord, textViewAddressLandlord, textViewPhoneNumberLandlord, textViewEmailLandlord, textViewPasswordLandlord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Landlord landlord = (Landlord) bundle.getSerializable("landlord");


        // mapping
        textViewNameLandlord = findViewById(R.id.as_name_landlord);
        textViewAddressLandlord = findViewById(R.id.as_address_landlord);
        textViewPhoneNumberLandlord = findViewById(R.id.as_phoneNumber_landlord);
        textViewEmailLandlord = findViewById(R.id.as_email_landlord);
        textViewPasswordLandlord = findViewById(R.id.as_password_landlord);

        // do something

        textViewNameLandlord.setText(landlord.getNameLandlord());
        textViewAddressLandlord.setText(landlord.getAddressLandlord());
        textViewPhoneNumberLandlord.setText(landlord.getPhoneNumberLandlord());
        textViewEmailLandlord.setText(landlord.getEmailLandlord());
        textViewPasswordLandlord.setText(landlord.getPasswordLandlord());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(98, intent);
        finish();
        super.onBackPressed();
    }
}