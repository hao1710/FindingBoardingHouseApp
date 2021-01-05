package com.example.findingboardinghouseapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.Facility;
import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.Model.RoomTypeCRUD;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateRoomTypeActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_FROM_CREATE_ROOM_TYPE = 34;
    public static final int RESULT_CODE_FROM_CREATE_ROOM_TYPE = 33;

    ImageButton ibBack;
    TextInputLayout tilName, tilArea, tilPrice, tilNumberPeople, tilDescription;
    TextInputEditText edtName, edtArea, edtPrice, edtNumberPeople, edtDescription;

    CheckBox checkBoxGac, checkBoxWCRieng, checkBoxGiuong, checkBoxTuQuanAo, checkBoxMayLanh, checkBoxTuLanh, checkBoxMayGiat, checkBoxBep, checkBoxWifiFree;
    Button buttonCreate;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room_type);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        RoomType roomType = (RoomType) bundle.getSerializable("newRoomType");

        findView();

        edtDescription.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edtDescription.clearFocus();
            }
            return false;
        });
        buttonCreate.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String area = edtArea.getText().toString().trim();
            String price = edtPrice.getText().toString().trim();
            String numberPeople = edtNumberPeople.getText().toString().trim();
            String description = edtDescription.getText().toString().trim();
            if (!validateName(name) | !validateArea(area) | !validatePrice(price) | !validateNumberPeople(numberPeople) | !validateDescription(description)) {
                return;
            }
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Vui lòng đợi");
            progressDialog.show();
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

            roomTypeCRUD.setFacility(facility);

            FirebaseFirestore.getInstance().collection("boardingHouse").document(roomType.getIdBoardingHouse()).collection("roomType")
                    .add(roomTypeCRUD).addOnCompleteListener(task -> {
                Intent intent1 = new Intent();
                setResult(RESULT_CODE_FROM_CREATE_ROOM_TYPE, intent1);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Thêm loại phòng thành công", Toast.LENGTH_SHORT).show();
                finish();
            });
        });

        ibBack.setOnClickListener(v -> finish());

    }

    private void findView() {
        ibBack = findViewById(R.id.crt_ib_back);

        tilName = findViewById(R.id.crt_til_name);
        tilArea = findViewById(R.id.crt_til_area);
        tilPrice = findViewById(R.id.crt_til_price);
        tilNumberPeople = findViewById(R.id.crt_til_numberPeople);
        tilDescription = findViewById(R.id.crt_til_description);

        edtName = findViewById(R.id.crt_edt_name);
        edtArea = findViewById(R.id.crt_edt_area);
        edtPrice = findViewById(R.id.crt_edt_price);
        edtNumberPeople = findViewById(R.id.crt_edt_numberPeople);
        edtDescription = findViewById(R.id.crt_edt_description);

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
            tilName.setError("Vui lòng điền tên loại phòng");
            return false;
        } else {
            tilName.setError(null);
            return true;
        }
    }

    private boolean validateArea(String area) {
        if (area.isEmpty()) {
            tilArea.setError("Vui lòng điền diện tích loại phòng");
            return false;
        } else {
            tilArea.setError(null);
            return true;
        }
    }

    private boolean validatePrice(String price) {
        if (price.isEmpty()) {
            tilPrice.setError("Vui lòng điền giá loại phòng");
            return false;
        } else {
            tilPrice.setError(null);
            return true;
        }
    }


    private boolean validateNumberPeople(String numberPeople) {
        if (numberPeople.isEmpty()) {
            tilNumberPeople.setError("Vui lòng điền số người ở tối đa");
            return false;
        } else {
            tilNumberPeople.setError(null);
            return true;
        }
    }

    private boolean validateDescription(String description) {
        if (description.isEmpty()) {
            tilDescription.setError("Vui lòng điền mô tả phòng");
            return false;
        } else {
            tilDescription.setError(null);
            return true;
        }
    }
}