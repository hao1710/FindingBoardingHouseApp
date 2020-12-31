package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class UpdateBoardingHouseActivity extends AppCompatActivity {

    private BoardingHouse boardingHouse;
    private TextInputLayout textInputName, textInputDistrict, textInputVillage, textInputHamlet, textInputElectricityPrice, textInputWaterPrice, textInputDescription, textInputDistance;
    private TextInputEditText textInputEditTextName, textInputEditTextHamlet, textInputEditTextElectricityPrice, textInputEditTextWaterPrice, textInputEditTextDescription, textInputEditTextDistance;
    private TextView textViewAddress;
    private AutoCompleteTextView autoCompleteTextViewDistrict, autoCompleteTextViewVillage;

    private Button buttonUpdate;
    private ImageButton imageButtonPickLocation;
    private DirectionsRoute currentRoute;

    public static int RESULT_CODE_FROM_UPDATE_BOARDING_HOUSE = 38;
    public static int REQUEST_CODE_FROM_UPDATE_BOARDING_HOUSE = 39;
    double latitude;
    double longitude;
    double latitudeCTU = 9.76126166509999;
    double longitudeCTU = 105.60451156571315;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_boarding_house);

        findView();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        boardingHouse = (BoardingHouse) bundle.getSerializable("boardingHouseUpdate");

        initialInfo(boardingHouse);

        buttonUpdate.setOnClickListener(v -> {
            String nameV = textInputEditTextName.getText().toString().trim();
            String address = textViewAddress.getText().toString().trim();
            String districtV = autoCompleteTextViewDistrict.toString().trim();
            String villageV = autoCompleteTextViewVillage.getText().toString().trim();
            String hamletV = textInputEditTextHamlet.getText().toString().trim();
            String ePrice = textInputEditTextElectricityPrice.getText().toString().trim();
            String wPrice = textInputEditTextWaterPrice.getText().toString().trim();
            String descriptionV = textInputEditTextDescription.getText().toString().trim();
            String distanceV = textInputEditTextDistance.getText().toString().trim();
            if (!validateName(nameV) | !validateDistrict(districtV) | !validateVillage(villageV) | !validateHamlet(hamletV)
                    | !validateElectricityPrice(ePrice) | !validateWaterPrice(wPrice) | !validateDescription(descriptionV) | !validateDistance(distanceV)) {
                return;
            }
            boardingHouse.setNameBoardingHouse(nameV);
            boardingHouse.setAddressBoardingHouse(address);
            boardingHouse.setDescriptionBoardingHouse(descriptionV);
            boardingHouse.setWaterPriceBoardingHouse(Double.parseDouble(wPrice));
            boardingHouse.setElectricityPriceBoardingHouse(Double.parseDouble(ePrice));
            boardingHouse.setDistanceBoardingHouse(boardingHouse.getDistanceBoardingHouse());
            Map<String, Object> update = new HashMap<>();
            update.put("address", address);
            update.put("description", descriptionV);
            update.put("electricityPrice", Double.parseDouble(ePrice));
            update.put("name", nameV);
            update.put("waterPrice", Double.parseDouble(wPrice));
            GeoPoint point = new GeoPoint(latitude, longitude);
            update.put("point", point);
            update.put("distance", boardingHouse.getDistanceBoardingHouse());

            FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse()).update(update);
            Toast.makeText(getApplicationContext(), "Cập nhật thông tin nhà trọ thành công", Toast.LENGTH_SHORT).show();

            BoardingHouse boardingHouseResult = boardingHouse;
            Bundle bundleResult = new Bundle();
            bundleResult.putSerializable("boardingHouseResult", boardingHouseResult);
            Intent intentResult = new Intent();
            intentResult.putExtras(bundleResult);
            setResult(RESULT_CODE_FROM_UPDATE_BOARDING_HOUSE, intentResult);
            finish();

        });

        textInputEditTextName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = textInputEditTextHamlet.getText();
                Selection.setSelection(e, textInputEditTextHamlet.getText().length());
            }
            return false;
        });
        textInputEditTextHamlet.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = textInputEditTextElectricityPrice.getText();
                Selection.setSelection(e, textInputEditTextElectricityPrice.getText().length());
            }
            return false;
        });
        textInputEditTextHamlet.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                getAddress();
            }
        });
        textInputEditTextElectricityPrice.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = textInputEditTextWaterPrice.getText();
                Selection.setSelection(e, textInputEditTextWaterPrice.getText().length());
            }
            return false;
        });
        textInputEditTextWaterPrice.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = textInputEditTextDescription.getText();
                Selection.setSelection(e, textInputEditTextDescription.getText().length());
            }
            return false;
        });
        textInputEditTextDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    textInputDescription.getEditText().clearFocus();
                }
                return false;
            }
        });

        imageButtonPickLocation.setOnClickListener(v -> {
            Intent intent1 = new Intent(UpdateBoardingHouseActivity.this, PickLocationActivity.class);
            startActivityForResult(intent1, REQUEST_CODE_FROM_UPDATE_BOARDING_HOUSE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FROM_UPDATE_BOARDING_HOUSE && resultCode == PickLocationActivity.RESULT_CODE_FROM_PICK_LOCATION && data != null) {
            latitude = data.getDoubleExtra("latitude", requestCode);
            longitude = data.getDoubleExtra("longitude", requestCode);

            Point ctu = Point.fromLngLat(longitudeCTU, latitudeCTU);
            Point bh = Point.fromLngLat(longitude, latitude);
            getRoute(ctu, bh);
        }
    }

    private void getRoute(Point origin, Point destination) {
        MapboxDirections client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NotNull Call<DirectionsResponse> call, @NotNull Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Timber.d("Response code: %s", response.code());
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.e("No routes found");
                    return;
                }

                // Get the directions route
                currentRoute = response.body().routes().get(0);
                double tempDistance = currentRoute.distance() / 1000;
                boardingHouse.setDistanceBoardingHouse(Math.ceil(tempDistance * 100) / 100);
                textInputEditTextDistance.setText("Cách ĐHCT " + boardingHouse.getDistanceBoardingHouse() + " km");

            }

            @Override
            public void onFailure(@NotNull Call<DirectionsResponse> call, @NotNull Throwable throwable) {
                Timber.e("Error: %s", throwable.getMessage());
                Toast.makeText(UpdateBoardingHouseActivity.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void findView() {
        textInputName = findViewById(R.id.ubh_textInput_name);
        textInputEditTextName = findViewById(R.id.ubh_textInputEditText_name);

        textViewAddress = findViewById(R.id.ubh_tv_address);

        textInputDistrict = findViewById(R.id.ubh_textInput_district);
        autoCompleteTextViewDistrict = findViewById(R.id.ubh_autoCompleteTextView_district);

        textInputVillage = findViewById(R.id.ubh_textInput_village);
        autoCompleteTextViewVillage = findViewById(R.id.ubh_autoCompleteTextView_village);

        textInputHamlet = findViewById(R.id.ubh_textInput_hamlet);
        textInputEditTextHamlet = findViewById(R.id.ubh_textInputEditText_hamlet);

        textInputElectricityPrice = findViewById(R.id.ubh_textInput_electricityPrice);
        textInputEditTextElectricityPrice = findViewById(R.id.ubh_textInputEditText_electricityPrice);

        textInputWaterPrice = findViewById(R.id.ubh_textInput_waterPrice);
        textInputEditTextWaterPrice = findViewById(R.id.ubh_textInputEditText_waterPrice);

        textInputDescription = findViewById(R.id.ubh_textInput_description);
        textInputEditTextDescription = findViewById(R.id.ubh_textInputEditText_description);

        textInputDistance = findViewById(R.id.ubh_textInput_distance);
        textInputEditTextDistance = findViewById(R.id.ubh_textInputEditText_distance);

        imageButtonPickLocation = findViewById(R.id.ubh_ib_pick_location);
        buttonUpdate = findViewById(R.id.ubh_button_update);
    }


    @SuppressLint("SetTextI18n")
    private void getAddress() {
        String hamletAddress = Objects.requireNonNull(textInputEditTextHamlet.getText()).toString().trim();
        String villageAddress = autoCompleteTextViewVillage.getText().toString().trim();
        String districtAddress = autoCompleteTextViewDistrict.getText().toString().trim();
        String provinceAddress = "Hậu Giang";
        String address = hamletAddress + ", " + villageAddress + ", " + districtAddress + ", " + provinceAddress;
        if (hamletAddress.isEmpty() | villageAddress.isEmpty() | districtAddress.isEmpty()) {
            textViewAddress.setText("Vui lòng chọn địa chỉ");
        } else {
            textViewAddress.setText(address);
        }
    }


    @SuppressLint("SetTextI18n")
    private void initialInfo(BoardingHouse boardingHouse) {
        textInputEditTextName.setText(boardingHouse.getNameBoardingHouse());
        textInputEditTextElectricityPrice.setText(String.valueOf(boardingHouse.getElectricityPriceBoardingHouse()));
        textInputEditTextWaterPrice.setText(String.valueOf(boardingHouse.getWaterPriceBoardingHouse()));
        textInputEditTextDescription.setText(boardingHouse.getDescriptionBoardingHouse());
        textInputEditTextDistance.setText("Cách ĐHCT " + boardingHouse.getDistanceBoardingHouse() + " km");

        String address = boardingHouse.getAddressBoardingHouse();
        int index = address.indexOf(", ");
        String hamletAddress = address.substring(0, index).trim();
        int index2 = address.indexOf(", ", index + 1);
        //String villageAddress = address.substring(index + 2, index2).trim();
        int index3 = address.indexOf(", ", index2 + 1);
        String districtAddress = address.substring(index2 + 2, index3).trim();

        textInputEditTextHamlet.setText(hamletAddress);
        autoCompleteTextViewDistrict.setText(districtAddress);

        String[] district = new String[]{"Thành phố Vị Thanh", "Thị xã Ngã Bảy", "Thị xã Long Mỹ",
                "Huyện Vị Thủy", "Huyện Long Mỹ", "Huyện Phụng Hiệp", "Huyện Châu Thành", "Huyện Châu Thành A"};
        ArrayAdapter<String> adapterDistrict = new ArrayAdapter<>(UpdateBoardingHouseActivity.this,
                R.layout.item_spinner, district);

        autoCompleteTextViewDistrict.setAdapter(adapterDistrict);
        autoCompleteTextViewDistrict.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            selectDistrict(item);
        });
        selectDistrict(districtAddress);
        autoCompleteTextViewVillage.setOnItemClickListener((parent, view, position, id) -> getAddress());
    }


    private void selectDistrict(String district) {
        autoCompleteTextViewVillage.setText("");
        getAddress();
        ArrayAdapter<String> adapter = null;
        switch (district) {
            case "Thành phố Vị Thanh":
                String[] tpVT = new String[]{"Phường 1", "Phường 3", "Phường 4", "Phường 5", "Phường 7",
                        "Xã Vị Tân", "Xã Hỏa Lựu", "Xã Tân Tiến", "Xã Hỏa Tiến"};
                adapter = new ArrayAdapter<>(UpdateBoardingHouseActivity.this,
                        R.layout.item_spinner, tpVT);
                break;
            case "Thị xã Ngã Bảy":
                String[] txNB = new String[]{"Phường Ngã Bảy", "Phường Lái Hiếu", "Phường Hiệp Thành",
                        "Xã Hiệp Lợi", "Xã Đại Thành", "Xã Tân Thành"};
                adapter = new ArrayAdapter<>(UpdateBoardingHouseActivity.this,
                        R.layout.item_spinner, txNB);
                break;
            case "Thị xã Long Mỹ":
                String[] txLM = new String[]{"Phường Thuận An", "Phường Bình Thành", "Phường Vĩnh Tường", "Phường Trà Lồng",
                        "Xã Long Bình", "Xã Long Trị", "Xã Long Trị A", "Xã Tân Phú", "Xã Long Phú"};
                adapter = new ArrayAdapter<>(UpdateBoardingHouseActivity.this,
                        R.layout.item_spinner, txLM);
                break;
            case "Huyện Vị Thủy":
                String[] hVT = new String[]{"Thị trấn Nàng Mau", "Xã Vị Bình", "Xã Vị Đông", "Xã Vị Thanh", "Xã Vị Thắng", "Xã Vị Thủy",
                        "Xã Vị Trung", "Xã Vĩnh Thuận Tây", "Xã Vĩnh Thuận Trung", "Xã Vĩnh Tường"};
                adapter = new ArrayAdapter<>(UpdateBoardingHouseActivity.this,
                        R.layout.item_spinner, hVT);
                break;
            case "Huyện Long Mỹ":
                String[] hLM = new String[]{"Xã Thuận Hoà", "Xã Thuận Hưng", "Xã Vĩnh Thuận Đông", "Xã Vĩnh Viễn", "Xã Vĩnh Viễn A",
                        "Xã Xà Phiên", "Xã Lương Tâm", "Xã Lương Nghĩa"};
                adapter = new ArrayAdapter<>(UpdateBoardingHouseActivity.this,
                        R.layout.item_spinner, hLM);
                break;
            case "Huyện Phụng Hiệp":
                String[] hPH = new String[]{"Thị trấn Cây Dương", "Thị trấn Kinh Cùng", "Thị trấn Búng Tàu",
                        "Xã Phụng Hiệp", "Xã Tân Phước Hưng", "Xã Tân Bình", "Xã Hoà An", "Xã Phương Bình", "Xã Phương Phú",
                        "Xã Hoà Mỹ", "Xã Hiệp Hưng", "Xã Thạnh Hoà", "Xã Bình Thành", "Xã Tân Long", "Xã Long Thạnh"};
                adapter = new ArrayAdapter<>(UpdateBoardingHouseActivity.this,
                        R.layout.item_spinner, hPH);
                break;
            case "Huyện Châu Thành":
                String[] hCT = new String[]{"Thị trấn Ngã Sáu", "Thị trấn Mái Dầm",
                        "Xã Đông Phước", "Xã Đông Phước A", "Xã Phú Hữu", "Xã Phú Tân", "Xã Phú An", "Xã Đông Phú", "Xã Đông Thạnh"};
                adapter = new ArrayAdapter<>(UpdateBoardingHouseActivity.this,
                        R.layout.item_spinner, hCT);
                break;
            case "Huyện Châu Thành A":
                String[] hCTA = new String[]{"Thị trấn Một Ngàn", "Thị trấn Bảy Ngàn", "Thị trấn Cái Tắc", "Thị trấn Rạch Gồi",
                        "Xã Thạnh Xuân", "Xã Tân Phú Thạnh", "Xã Tân Hoà", "Xã Trường Long Tây", "Xã Trường Long A", "Xã Nhơn Nghĩa A"};
                adapter = new ArrayAdapter<>(UpdateBoardingHouseActivity.this,
                        R.layout.item_spinner, hCTA);
                break;
        }
        autoCompleteTextViewVillage.setAdapter(adapter);

    }

    private boolean validateName(String name) {
        if (name.isEmpty()) {
            textInputName.setError("Vui lòng điền tên nhà trọ");
            return false;
        } else {
            textInputName.setError(null);
            return true;
        }
    }

    private boolean validateDistrict(String district) {
        if (district.isEmpty()) {
            textInputDistrict.setError("Vui lòng chọn huyện");
            return false;
        } else {
            textInputDistrict.setError(null);
            return true;
        }
    }

    private boolean validateVillage(String village) {
        if (village.isEmpty()) {
            textInputVillage.setError("Vui lòng chọn xã");
            return false;
        } else {
            textInputVillage.setError(null);
            return true;
        }
    }

    private boolean validateHamlet(String hamlet) {
        if (hamlet.isEmpty()) {
            textInputHamlet.setError("Vui lòng điền ấp");
            return false;
        } else {
            textInputHamlet.setError(null);
            return true;
        }
    }

    private boolean validateElectricityPrice(String ePrice) {
        if (ePrice.isEmpty()) {
            textInputElectricityPrice.setError("Vui lòng điền giá điện");
            return false;
        } else {
            textInputElectricityPrice.setError(null);
            return true;
        }
    }

    private boolean validateWaterPrice(String wPrice) {
        if (wPrice.isEmpty()) {
            textInputWaterPrice.setError("Vui lòng điền giá nước");
            return false;
        } else {
            textInputWaterPrice.setError(null);
            return true;
        }
    }

    private boolean validateDescription(String description) {
        if (description.isEmpty()) {
            textInputDescription.setError("Vui lòng điền mô tả về nhà trọ");
            return false;
        } else {
            textInputDescription.setError(null);
            return true;
        }
    }

    private boolean validateDistance(String distance) {
        if (distance.isEmpty()) {
            textInputDistance.setError("Vui lòng chọn vị trí nhà trọ");
            return false;
        } else {
            textInputDistance.setError(null);
            return true;
        }
    }

}