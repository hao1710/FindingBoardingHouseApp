package com.example.findingboardinghouseapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.Model.RoomTypeCRUD;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateRoomTypeActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_FROM_CREATE_ROOM_TYPE = 34;
    public static final int RESULT_CODE_FROM_CREATE_ROOM_TYPE = 33;

    private TextInputLayout textInputName, textInputArea, textInputDescription, textInputNumberPeople, textInputPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room_type);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        RoomType roomType = (RoomType) bundle.getSerializable("newRoomType");

        // mapping
        textInputName = findViewById(R.id.crt_textInput_name);
        textInputArea = findViewById(R.id.crt_textInput_area);
        textInputPrice = findViewById(R.id.crt_textInput_price);
        textInputNumberPeople = findViewById(R.id.crt_textInput_numberPeople);
        textInputDescription = findViewById(R.id.crt_textInput_description);
        Button buttonCreate = findViewById(R.id.crt_button_create);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = textInputName.getEditText().getText().toString().trim();
                String area = textInputArea.getEditText().getText().toString().trim();
                String price = textInputPrice.getEditText().getText().toString().trim();
                String numberPeople = textInputNumberPeople.getEditText().getText().toString().trim();
                String description = textInputDescription.getEditText().getText().toString().trim();
                if (!validateName(name) | !validateArea(area) | !validatePrice(price) | !validateNumberPeople(numberPeople) | !validateDescription(description)) {
                    return;
                }
                RoomTypeCRUD roomTypeCRUD = new RoomTypeCRUD();
                roomTypeCRUD.setName(name);
                roomTypeCRUD.setArea(Double.parseDouble(area));
                roomTypeCRUD.setDescription(description);
                roomTypeCRUD.setNumberPeople(Double.parseDouble(numberPeople));
                roomTypeCRUD.setPrice(Double.parseDouble(price));
                FirebaseFirestore.getInstance().collection("boardingHouse").document(roomType.getIdBoardingHouse()).collection("roomType")
                        .add(roomTypeCRUD);
                Toast.makeText(getApplicationContext(), "Thêm loại phòng thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent();
        setResult(RESULT_CODE_FROM_CREATE_ROOM_TYPE, intent1);
        finish();
        super.onBackPressed();
    }

    private boolean validateName(String name) {
        if (name.isEmpty()) {
            textInputName.setError("Vui lòng điền tên loại phòng");
            return false;
        } else {
            textInputName.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        }
    }

    private boolean validateArea(String area) {
        if (area.isEmpty()) {
            textInputArea.setError("Vui lòng điền diện tích loại phòng");
            return false;
        } else {
            textInputArea.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        }
    }

    private boolean validatePrice(String price) {
        if (price.isEmpty()) {
            textInputPrice.setError("Vui lòng điền giá loại phòng");
            return false;
        } else {
            textInputPrice.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        }
    }

    private boolean validateNumberPeople(String numberPeople) {
        if (numberPeople.isEmpty()) {
            textInputNumberPeople.setError("Vui lòng điền số người ở tối đa");
            return false;
        } else {
            textInputNumberPeople.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        }
    }

    private boolean validateDescription(String description) {
        if (description.isEmpty()) {
            textInputDescription.setError("Vui lòng điền số người ở tối đa");
            return false;
        } else {
            textInputDescription.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        }
    }
}