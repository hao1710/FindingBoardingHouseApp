package com.example.findingboardinghouseapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.Facility;
import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.Model.RoomTypeCRUD;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateRoomTypeActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_FROM_CREATE_ROOM_TYPE = 34;
    public static final int RESULT_CODE_FROM_CREATE_ROOM_TYPE = 33;

    private TextInputLayout textInputName, textInputArea, textInputPrice, textInputElectricityPrice, textInputWaterPrice, textInputNumberPeople, textInputDescription;
    private CheckBox checkBoxGac, checkBoxWCRieng, checkBoxGiuong, checkBoxTuQuanAo, checkBoxMayLanh, checkBoxTuLanh, checkBoxMayGiat, checkBoxBep, checkBoxWifiFree;
    private Button buttonCreate;

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
//        textInputElectricityPrice = findViewById(R.id.crt_textInput_price_electricity);
//        textInputWaterPrice = findViewById(R.id.crt_textInput_price_water);
        textInputNumberPeople = findViewById(R.id.crt_textInput_numberPeople);
        textInputDescription = findViewById(R.id.crt_textInput_description);

        checkBoxGac = findViewById(R.id.crt_checkBox_gac);
        checkBoxWCRieng = findViewById(R.id.crt_checkBox_wcrieng);
        checkBoxGiuong = findViewById(R.id.crt_checkBox_giuong);
        checkBoxTuQuanAo = findViewById(R.id.crt_checkBox_tuquanao);
        checkBoxMayLanh = findViewById(R.id.crt_checkBox_maylanh);
        checkBoxTuLanh = findViewById(R.id.crt_checkBox_tulanh);
        checkBoxMayGiat = findViewById(R.id.crt_checkBox_maygiat);
        checkBoxBep = findViewById(R.id.crt_checkBox_bep);
        checkBoxWifiFree = findViewById(R.id.crt_checkBox_wififree);

        buttonCreate = findViewById(R.id.crt_button_create);

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = textInputName.getEditText().getText().toString().trim();
                String area = textInputArea.getEditText().getText().toString().trim();
                String price = textInputPrice.getEditText().getText().toString().trim();
//                String electricity = textInputElectricityPrice.getEditText().getText().toString().trim();
//                String water = textInputWaterPrice.getEditText().getText().toString().trim();
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
                Map<String, Facility> facility = new HashMap<>();


                Facility facility1 = new Facility();
                facility1.setName("Gác");
                facility1.setStatus(checkBoxGac.isChecked());
                facility1.setImage("ic_gac");
                facility.put("gac", facility1);

                Facility facility2 = new Facility();
                facility2.setName("WC riêng");
                facility2.setStatus(checkBoxWCRieng.isChecked());
                facility2.setImage("ic_wc");
                facility.put("wc", facility2);

                Facility facility3 = new Facility();
                facility3.setName("Giường");
                facility3.setStatus(checkBoxGiuong.isChecked());
                facility3.setImage("ic_bed");
                facility.put("giuong", facility3);

                Facility facility4 = new Facility();
                facility4.setName("Tủ quần áo");
                facility4.setStatus(checkBoxTuQuanAo.isChecked());
                facility4.setImage("ic_wardrobe");
                facility.put("tuquanao", facility4);

                Facility facility5 = new Facility();
                facility5.setName("Máy lạnh");
                facility5.setStatus(checkBoxMayLanh.isChecked());
                facility5.setImage("ic_air");
                facility.put("maylanh", facility5);

                Facility facility6 = new Facility();
                facility6.setName("Tủ lạnh");
                facility6.setStatus(checkBoxTuLanh.isChecked());
                facility6.setImage("ic_fridge");
                facility.put("tulanh", facility6);

                Facility facility7 = new Facility();
                facility7.setImage("ic_kitchen_shelf");
                facility7.setName("Kệ bếp");
                facility7.setStatus(checkBoxBep.isChecked());
                facility.put("ke", facility7);

                Facility facility8 = new Facility();
                facility8.setName("Wifi free");
                facility8.setImage("ic_wifi");
                facility8.setStatus(checkBoxWifiFree.isChecked());

                facility.put("wifi", facility8);

                Facility facility9 = new Facility();
                facility9.setName("Máy giặt");
                facility9.setStatus(checkBoxMayGiat.isChecked());
                facility9.setImage("ic_washing");
                facility.put("matgiat", facility9);

//                facility.put("gac", checkBoxGac.isChecked());
//                facility.put("wc", checkBoxWCRieng.isChecked());
//                facility.put("giuong", checkBoxGiuong.isChecked());
//                facility.put("tuquanao", checkBoxTuQuanAo.isChecked());
//                facility.put("maylanh", checkBoxMayLanh.isChecked());
//                facility.put("tulanh", checkBoxTuLanh.isChecked());
//                facility.put("maygiat", checkBoxMayGiat.isChecked());
//                facility.put("bep", checkBoxBep.isChecked());
//                facility.put("wifi", checkBoxWifiFree.isChecked());
//                Map<String, Object> facilityMap = new HashMap<>();
                roomTypeCRUD.setFacility(facility);

                FirebaseFirestore.getInstance().collection("boardingHouse").document(roomType.getIdBoardingHouse()).collection("roomType")
                        .add(roomTypeCRUD);
                Toast.makeText(getApplicationContext(), "Thêm loại phòng thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class ObjectFacility {
        String name;
        boolean exist;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isExist() {
            return exist;
        }

        public void setExist(boolean exist) {
            this.exist = exist;
        }
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

    private boolean validateElectricityPrice(String electricity) {
        if (electricity.isEmpty()) {
            textInputElectricityPrice.setError("Vui lòng điền giá điện");
            return false;
        } else {
            textInputElectricityPrice.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        }
    }

    private boolean validateWaterPrice(String water) {
        if (water.isEmpty()) {
            textInputWaterPrice.setError("Vui lòng điền giá nước");
            return false;
        } else {
            textInputWaterPrice.setError(null);
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