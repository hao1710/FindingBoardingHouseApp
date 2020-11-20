package com.example.findingboardinghouseapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;

public class AccountSettingActivity extends AppCompatActivity {
    public static final int RESULT_CODE_SETTING_ACCOUNT = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Landlord landlord = (Landlord) bundle.getSerializable("landlord");


        // mapping
        TextView textViewNameLandlord = findViewById(R.id.as_name_landlord);
        TextView textViewAddressLandlord = findViewById(R.id.as_address_landlord);
        TextView textViewPhoneNumberLandlord = findViewById(R.id.as_phoneNumber_landlord);
        TextView textViewEmailLandlord = findViewById(R.id.as_email_landlord);
        TextView textViewPasswordLandlord = findViewById(R.id.as_password_landlord);

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
        setResult(RESULT_CODE_SETTING_ACCOUNT, intent);
        finish();
        super.onBackPressed();
    }
}