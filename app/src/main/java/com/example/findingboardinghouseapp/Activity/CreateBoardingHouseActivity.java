package com.example.findingboardinghouseapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.R;

public class CreateBoardingHouseActivity extends AppCompatActivity {
    public static final int RESULT_CODE_CREATE_BOARDING_HOUSE = 98;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_boarding_house);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        BoardingHouse boardingHouse = (BoardingHouse) bundle.getSerializable("boardingHouse");
        Toast.makeText(getApplicationContext(), boardingHouse.getIdOwnerBoardingHouse(), Toast.LENGTH_SHORT).show();

        // mapping
        Button buttonCreateBoardingHouse = findViewById(R.id.cbh_button_create);
        Button buttonPickLocation = findViewById(R.id.cbh_button_pick_location);

        buttonCreateBoardingHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        buttonPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CODE_CREATE_BOARDING_HOUSE, intent);
        finish();
        super.onBackPressed();
    }
}