package com.example.findingboardinghouseapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.Facility;
import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateRoomTypeActivity extends AppCompatActivity {
    private RoomType roomType;
    TextInputEditText edtName, edtArea, edtPrice, edtNumberPeople, edtDescription;
    TextInputLayout textInputName, textInputArea, textInputPrice, textInputNumberPeople, textInputDescription;
    ImageButton ibBack;
    CheckBox cbGac, cbWC, cbWifi, cbKebep, cbTulanh, cbMaylanh, cbMaygiat, cbTuquanao, cbGiuong;
    Button buttonUpdate;
    public static int RESULT_CODE_FROM_UPDATE_ROOM_TYPE_ACTIVITY = 31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_room_type);

        findView();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        roomType = (RoomType) bundle.getSerializable("roomTypeUpdate");

        initialData(roomType);
        getFacility(list -> {
            if (list.size() > 0) {
                for (Facility facility : list) {
                    if (facility.getName().equals("Gác")) {
                        cbGac.setChecked(facility.isStatus());
                    }
                    if (facility.getName().equals("Giường")) {
                        cbGiuong.setChecked(facility.isStatus());
                    }
                    if (facility.getName().equals("Kệ bếp")) {
                        cbKebep.setChecked(facility.isStatus());
                    }
                    if (facility.getName().equals("Máy giặt")) {
                        cbMaygiat.setChecked(facility.isStatus());
                    }
                    if (facility.getName().equals("Máy lạnh")) {
                        cbMaylanh.setChecked(facility.isStatus());
                    }
                    if (facility.getName().equals("Tủ lạnh")) {
                        cbTulanh.setChecked(facility.isStatus());
                    }
                    if (facility.getName().equals("Tủ quần áo")) {
                        cbTuquanao.setChecked(facility.isStatus());
                    }
                    if (facility.getName().equals("WC riêng")) {
                        cbWC.setChecked(facility.isStatus());
                    }
                    if (facility.getName().equals("Wifi free")) {
                        cbWifi.setChecked(facility.isStatus());
                    }
                }
            }
        });

        keyboardAction();

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentResult = new Intent();
                setResult(RESULT_CODE_FROM_UPDATE_ROOM_TYPE_ACTIVITY, intentResult);
                finish();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                String area = edtArea.getText().toString().trim();
                String price = edtPrice.getText().toString().trim();
                String numberPeople = edtNumberPeople.getText().toString().trim();
                String description = edtDescription.getText().toString().trim();
                if (!validateName(name) | !validateArea(area) | !validatePrice(price) | !validateNumberPeople(numberPeople) | !validateDescription(description)) {
                    return;
                }
                // RoomTypeCRUD roomTypeCRUD = new RoomTypeCRUD();
                Map<String, Object> update = new HashMap<>();
                update.put("area", Double.parseDouble(area));
                update.put("description", description);
                update.put("name", name);
                update.put("numberPeople", Double.parseDouble(numberPeople));
                update.put("price", Double.parseDouble(price));
//                roomTypeCRUD.setPrice(Double.parseDouble(price));

//                roomTypeCRUD.setNumberPeople(Double.parseDouble(numberPeople));
//                roomTypeCRUD.setDescription(description);
//                roomTypeCRUD.setName(name);
//                roomTypeCRUD.setArea(Double.parseDouble(area));

                Map<String, Facility> facility = new HashMap<>();

                Facility fGac = new Facility();
                fGac.setName("Gác");
                fGac.setStatus(cbGac.isChecked());
                fGac.setImage("ic_gac");
                facility.put("gac", fGac);

                Facility fWC = new Facility();
                fWC.setName("WC riêng");
                fWC.setStatus(cbWC.isChecked());
                fWC.setImage("ic_wc");
                facility.put("wc", fWC);

                Facility fWifi = new Facility();
                fWifi.setName("Wifi free");
                fWifi.setImage("ic_wifi");
                fWifi.setStatus(cbWifi.isChecked());
                facility.put("wifi", fWifi);

                Facility fKeBep = new Facility();
                fKeBep.setImage("ic_kitchen_shelf");
                fKeBep.setName("Kệ bếp");
                fKeBep.setStatus(cbKebep.isChecked());
                facility.put("ke", fKeBep);

                Facility fTuLanh = new Facility();
                fTuLanh.setName("Tủ lạnh");
                fTuLanh.setStatus(cbTulanh.isChecked());
                fTuLanh.setImage("ic_fridge");
                facility.put("tulanh", fTuLanh);


                Facility fMayLanh = new Facility();
                fMayLanh.setName("Máy lạnh");
                fMayLanh.setStatus(cbMaylanh.isChecked());
                fMayLanh.setImage("ic_air");
                facility.put("maylanh", fMayLanh);

                Facility fMayGiat = new Facility();
                fMayGiat.setName("Máy giặt");
                fMayGiat.setStatus(cbMaygiat.isChecked());
                fMayGiat.setImage("ic_washing");
                facility.put("matgiat", fMayGiat);

                Facility fTuQuanAo = new Facility();
                fTuQuanAo.setName("Tủ quần áo");
                fTuQuanAo.setStatus(cbTuquanao.isChecked());
                fTuQuanAo.setImage("ic_wardrobe");
                facility.put("tuquanao", fTuQuanAo);

                Facility fGiuong = new Facility();
                fGiuong.setName("Giường");
                fGiuong.setStatus(cbGiuong.isChecked());
                fGiuong.setImage("ic_bed");
                facility.put("giuong", fGiuong);

                update.put("facility", facility);
                // roomTypeCRUD.setFacility(facility);

                FirebaseFirestore.getInstance().collection("boardingHouse").document(roomType.getIdBoardingHouse())
                        .collection("roomType").document(roomType.getIdRoomType()).update(update);
                Toast.makeText(getApplicationContext(), "Cập nhật thông tin loại phòng thành công", Toast.LENGTH_SHORT).show();
                Intent intentResult = new Intent();
                setResult(RESULT_CODE_FROM_UPDATE_ROOM_TYPE_ACTIVITY, intentResult);
                finish();
            }
        });
    }

    private void keyboardAction() {
        edtName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = edtArea.getText();
                Selection.setSelection(e, edtArea.getText().length());
            }
            return false;
        });
        edtArea.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = edtPrice.getText();
                Selection.setSelection(e, edtPrice.getText().length());
            }
            return false;
        });
        edtPrice.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = edtNumberPeople.getText();
                Selection.setSelection(e, edtNumberPeople.getText().length());
            }
            return false;
        });
        edtNumberPeople.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = edtDescription.getText();
                Selection.setSelection(e, edtDescription.getText().length());
            }
            return false;
        });
        edtDescription.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edtDescription.clearFocus();
            }
            return false;
        });
    }

    private interface FacilityCallback {
        void onCallback(List<Facility> list);
    }

    private void getFacility(FacilityCallback callback) {
        List<Facility> list = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("boardingHouse").document(roomType.getIdBoardingHouse())
                .collection("roomType").document(roomType.getIdRoomType())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Map<String, Object> data = documentSnapshot.getData();
                    for (Map.Entry<String, Object> rtField : data.entrySet()) {
                        if (rtField.getKey().equals("facility")) {
                            Map<String, Object> allFacility = (Map<String, Object>) rtField.getValue();
                            for (Map.Entry<String, Object> eachFacility : allFacility.entrySet()) {
                                Map<String, Object> entry = (Map<String, Object>) eachFacility.getValue();
                                Facility facility = new Facility();
                                for (Map.Entry<String, Object> f : entry.entrySet()) {
                                    if (f.getKey().equals("name")) {
                                        facility.setName(f.getValue().toString());
                                    }
                                    if (f.getKey().equals("status")) {
                                        if (f.getValue().toString().equals("true")) {
                                            facility.setStatus(true);
                                        } else {
                                            facility.setStatus(false);
                                        }
                                    }
                                    list.add(facility);
                                }
                            }
                        }
                    }
                }
                callback.onCallback(list);
            }
        });

    }

    private void findView() {
        ibBack = findViewById(R.id.urt_ib_back);

        edtName = findViewById(R.id.urt_textInputEditText_name);
        edtArea = findViewById(R.id.urt_textInputEditText_area);
        edtPrice = findViewById(R.id.urt_textInputEditText_price);
        edtNumberPeople = findViewById(R.id.urt_textInputEditText_numberPeople);
        edtDescription = findViewById(R.id.urt_textInputEditText_description);

        textInputName = findViewById(R.id.urt_textInput_name);
        textInputArea = findViewById(R.id.urt_textInput_area);
        textInputNumberPeople = findViewById(R.id.urt_textInput_numberPeople);
        textInputDescription = findViewById(R.id.urt_textInput_description);
        textInputPrice = findViewById(R.id.urt_textInput_price);

        cbGac = findViewById(R.id.urt_checkBox_gac);
        cbWC = findViewById(R.id.urt_checkBox_wcrieng);
        cbWifi = findViewById(R.id.urt_checkBox_wififree);
        cbKebep = findViewById(R.id.urt_checkBox_bep);
        cbTulanh = findViewById(R.id.urt_checkBox_tulanh);
        cbMaylanh = findViewById(R.id.urt_checkBox_maylanh);
        cbMaygiat = findViewById(R.id.urt_checkBox_maygiat);
        cbTuquanao = findViewById(R.id.urt_checkBox_tuquanao);
        cbGiuong = findViewById(R.id.urt_checkBox_giuong);

        buttonUpdate = findViewById(R.id.urt_button_update);
    }

    private void initialData(RoomType roomType) {
        edtName.setText(roomType.getNameRoomType());
        edtArea.setText(String.valueOf(roomType.getAreaRoomType()));
        edtPrice.setText(String.valueOf(roomType.getPriceRoomType()));
        edtNumberPeople.setText(String.valueOf(roomType.getNumberPeopleRoomType()));
        edtDescription.setText(roomType.getDescriptionRoomType());
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