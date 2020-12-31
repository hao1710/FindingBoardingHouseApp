package com.example.findingboardinghouseapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.Facility;
import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.Model.RoomTypeCRUD;
import com.example.findingboardinghouseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
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
        textInputDescription.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    textInputDescription.getEditText().clearFocus();
                }
                return false;
            }
        });
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
                Map<String, Facility> facility = new HashMap<>();

                Facility fGac = new Facility();
                fGac.setName("Gác");
                fGac.setStatus(checkBoxGac.isChecked());
                fGac.setImage("ic_gac");
                facility.put("gac", fGac);

                Facility fWC = new Facility();
                fWC.setName("WC riêng");
                fWC.setStatus(checkBoxWCRieng.isChecked());
                fWC.setImage("ic_wc");
                facility.put("wc", fWC);

                Facility fWifi = new Facility();
                fWifi.setName("Wifi free");
                fWifi.setImage("ic_wifi");
                fWifi.setStatus(checkBoxWifiFree.isChecked());
                facility.put("wifi", fWifi);

                Facility fKeBep = new Facility();
                fKeBep.setImage("ic_kitchen_shelf");
                fKeBep.setName("Kệ bếp");
                fKeBep.setStatus(checkBoxBep.isChecked());
                facility.put("ke", fKeBep);

                Facility fTuLanh = new Facility();
                fTuLanh.setName("Tủ lạnh");
                fTuLanh.setStatus(checkBoxTuLanh.isChecked());
                fTuLanh.setImage("ic_fridge");
                facility.put("tulanh", fTuLanh);


                Facility fMayLanh = new Facility();
                fMayLanh.setName("Máy lạnh");
                fMayLanh.setStatus(checkBoxMayLanh.isChecked());
                fMayLanh.setImage("ic_air");
                facility.put("maylanh", fMayLanh);

                Facility fMayGiat = new Facility();
                fMayGiat.setName("Máy giặt");
                fMayGiat.setStatus(checkBoxMayGiat.isChecked());
                fMayGiat.setImage("ic_washing");
                facility.put("matgiat", fMayGiat);

                Facility fTuQuanAo = new Facility();
                fTuQuanAo.setName("Tủ quần áo");
                fTuQuanAo.setStatus(checkBoxTuQuanAo.isChecked());
                fTuQuanAo.setImage("ic_wardrobe");
                facility.put("tuquanao", fTuQuanAo);

                Facility fGiuong = new Facility();
                fGiuong.setName("Giường");
                fGiuong.setStatus(checkBoxGiuong.isChecked());
                fGiuong.setImage("ic_bed");
                facility.put("giuong", fGiuong);


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
                        .add(roomTypeCRUD).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Intent intent1 = new Intent();
                        setResult(RESULT_CODE_FROM_CREATE_ROOM_TYPE, intent1);
                        Toast.makeText(getApplicationContext(), "Thêm loại phòng thành công", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });

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