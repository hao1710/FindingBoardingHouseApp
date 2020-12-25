package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.BoardingHouseCRUD;
import com.example.findingboardinghouseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class CreateBoardingHouseActivity extends AppCompatActivity {
    public static final int RESULT_CODE_FROM_CREATE_BOARDING_HOUSE = 98;
    public static final int REQUEST_CODE_FROM_CREATE_BOARDING_HOUSE = 23;

    double latitude;
    double longitude;
    double latitudeCTU = 9.76126166509999;
    double longitudeCTU = 105.60451156571315;
    double distance;

    private TextInputLayout textInputName, textInputDistrict, textInputVillage, textInputHamlet, textInputElectricityPrice, textInputWaterPrice, textInputDescription, textInputDistance;
    private TextInputEditText textInputEditTextHamlet, textInputEditTextDistance;
    private AutoCompleteTextView autoCompleteTextViewDistrict, autoCompleteTextViewVillage;
    private ImageButton imageButtonPickLocation;
    private Button buttonCreateBoardingHouse;
    private TextView textViewAddress;

    private BoardingHouse boardingHouse;
    private static MapboxDirections client;
    private DirectionsRoute currentRoute;

    private String address;
    private String hamletAddress, villageAddress, districtAddress, provinceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_boarding_house);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        boardingHouse = (BoardingHouse) bundle.getSerializable("boardingHouse");

        // findView
        findView();

        initialSpinner();
        Objects.requireNonNull(textInputElectricityPrice.getEditText()).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Editable e = Objects.requireNonNull(textInputWaterPrice.getEditText()).getText();
                    Selection.setSelection(e, textInputWaterPrice.getEditText().getText().length());
                }
                return false;
            }
        });
        Objects.requireNonNull(textInputDescription.getEditText()).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    textInputDescription.getEditText().clearFocus();
                }
                return false;
            }
        });
        buttonCreateBoardingHouse.setOnClickListener(v -> {
            String nameV = Objects.requireNonNull(textInputName.getEditText()).getText().toString().trim();
            String districtV = Objects.requireNonNull(textInputDistrict.getEditText()).getText().toString().trim();
            String villageV = Objects.requireNonNull(textInputVillage.getEditText()).getText().toString().trim();
            String hamletV = Objects.requireNonNull(textInputHamlet.getEditText()).getText().toString().trim();
            String ePrice = Objects.requireNonNull(textInputElectricityPrice.getEditText()).getText().toString().trim();
            String wPrice = Objects.requireNonNull(textInputWaterPrice.getEditText()).getText().toString().trim();
            String descriptionV = Objects.requireNonNull(textInputDescription.getEditText()).getText().toString().trim();
            String distanceV = Objects.requireNonNull(textInputDistance.getEditText()).getText().toString().trim();
            if (!validateName(nameV) | !validateDistrict(districtV) | !validateVillage(villageV) | !validateHamlet(hamletV)
                    | !validateElectricityPrice(ePrice) | !validateWaterPrice(wPrice) | !validateDescription(descriptionV) | !validateDistance(distanceV)) {
                return;
            }
            BoardingHouseCRUD boardingHouseCRUD = new BoardingHouseCRUD();
            boardingHouseCRUD.setName(nameV);
            boardingHouseCRUD.setAddress(address);
            boardingHouseCRUD.setDistance(distance);
            boardingHouseCRUD.setElectricityPrice(Double.parseDouble(ePrice));
            boardingHouseCRUD.setWaterPrice(Double.parseDouble(wPrice));
            boardingHouseCRUD.setStatus(false);
            GeoPoint point = new GeoPoint(latitude, longitude);
            boardingHouseCRUD.setOwner(boardingHouse.getIdOwnerBoardingHouse());
            boardingHouseCRUD.setPoint(point);
            boardingHouseCRUD.setDescription(textInputDescription.getEditText().getText().toString().trim());
            FirebaseFirestore.getInstance().collection("boardingHouse").add(boardingHouseCRUD).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    Toast.makeText(getApplicationContext(), "Thêm nhà trọ thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_CODE_FROM_CREATE_BOARDING_HOUSE, intent);
                    finish();
                }
            });

        });
        imageButtonPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), PickLocationActivity.class);
                startActivityForResult(intent1, REQUEST_CODE_FROM_CREATE_BOARDING_HOUSE);
            }
        });

        textInputEditTextHamlet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getAddress();
                }
            }
        });
    }

    private void findView() {
        textInputName = findViewById(R.id.cbh_textInput_name);
        textViewAddress = findViewById(R.id.cbh_textView_address);
        textInputDistrict = findViewById(R.id.cbh_textInput_district);
        textInputVillage = findViewById(R.id.cbh_textInput_village);
        textInputHamlet = findViewById(R.id.cbh_textInput_hamlet);
        textInputElectricityPrice = findViewById(R.id.cbh_textInput_electricityPrice);
        textInputWaterPrice = findViewById(R.id.cbh_textInput_waterPrice);
        textInputDescription = findViewById(R.id.cbh_textInput_description);
        textInputDistance = findViewById(R.id.cbh_textInput_distance);

        autoCompleteTextViewDistrict = findViewById(R.id.cbh_autoCompleteTextView_district);
        autoCompleteTextViewVillage = findViewById(R.id.cbh_autoCompleteTextView_village);
        textInputEditTextHamlet = findViewById(R.id.cbh_textInputEditText_hamlet);
        textInputEditTextDistance = findViewById(R.id.cbh_textInputEditText_distance);

        imageButtonPickLocation = findViewById(R.id.cbh_imageButton_pick_location);
        buttonCreateBoardingHouse = findViewById(R.id.cbh_button_create);
    }

    @SuppressLint("SetTextI18n")
    private void getAddress() {
        hamletAddress = Objects.requireNonNull(textInputEditTextHamlet.getText()).toString().trim();
        villageAddress = autoCompleteTextViewVillage.getText().toString().trim();
        districtAddress = autoCompleteTextViewDistrict.getText().toString().trim();
        provinceAddress = "Hậu Giang";
        address = hamletAddress + ", " + villageAddress + ", " + districtAddress + ", " + provinceAddress;
        if (hamletAddress.isEmpty() | villageAddress.isEmpty() | districtAddress.isEmpty()) {
            textViewAddress.setText("Vui lòng chọn địa chỉ");
        } else {
            textViewAddress.setText(address);
        }

    }

    private void initialSpinner() {
        String[] district = new String[]{"Thành phố Vị Thanh", "Thị xã Ngã Bảy", "Thị xã Long Mỹ",
                "Huyện Vị Thủy", "Huyện Long Mỹ", "Huyện Phụng Hiệp", "Huyện Châu Thành", "Huyện Châu Thành A"};
        ArrayAdapter<String> adapterDistrict = new ArrayAdapter<>(CreateBoardingHouseActivity.this,
                R.layout.item_spinner, district);

        autoCompleteTextViewDistrict.setAdapter(adapterDistrict);
        autoCompleteTextViewDistrict.setOnItemClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();

            switch (item) {
                case "Thành phố Vị Thanh":
                    getAddress();
                    autoCompleteTextViewVillage.setText("");
                    String[] tpVT = new String[]{"Phường 1", "Phường 3", "Phường 4", "Phường 5", "Phường 7",
                            "Xã Vị Tân", "Xã Hỏa Lựu", "Xã Tân Tiến", "Xã Hỏa Tiến"};
                    ArrayAdapter<String> adapterTPVT = new ArrayAdapter<>(CreateBoardingHouseActivity.this,
                            R.layout.item_spinner, tpVT);
                    autoCompleteTextViewVillage.setAdapter(adapterTPVT);
                    break;
                case "Thị xã Ngã Bảy":
                    getAddress();
                    autoCompleteTextViewVillage.setText("");
                    String[] txNB = new String[]{"Phường Ngã Bảy", "Phường Lái Hiếu", "Phường Hiệp Thành",
                            "Xã Hiệp Lợi", "Xã Đại Thành", "Xã Tân Thành"};
                    ArrayAdapter<String> adapterTXNB = new ArrayAdapter<>(CreateBoardingHouseActivity.this,
                            R.layout.item_spinner, txNB);
                    autoCompleteTextViewVillage.setAdapter(adapterTXNB);
                    break;
                case "Thị xã Long Mỹ":
                    getAddress();
                    autoCompleteTextViewVillage.setText("");
                    String[] txLM = new String[]{"Phường Thuận An", "Phường Bình Thành", "Phường Vĩnh Tường", "Phường Trà Lồng",
                            "Xã Long Bình", "Xã Long Trị", "Xã Long Trị A", "Xã Tân Phú", "Xã Long Phú"};
                    ArrayAdapter<String> adapterTXLM = new ArrayAdapter<>(CreateBoardingHouseActivity.this,
                            R.layout.item_spinner, txLM);
                    autoCompleteTextViewVillage.setAdapter(adapterTXLM);
                    break;
                case "Huyện Vị Thủy":
                    getAddress();
                    autoCompleteTextViewVillage.setText("");
                    String[] hVT = new String[]{"Thị trấn Nàng Mau", "Xã Vị Bình", "Xã Vị Đông", "Xã Vị Thanh", "Xã Vị Thắng", "Xã Vị Thủy",
                            "Xã Vị Trung", "Xã Vĩnh Thuận Tây", "Xã Vĩnh Thuận Trung", "Xã Vĩnh Tường"};
                    ArrayAdapter<String> adapterHVT = new ArrayAdapter<>(CreateBoardingHouseActivity.this,
                            R.layout.item_spinner, hVT);
                    autoCompleteTextViewVillage.setAdapter(adapterHVT);
                    break;
                case "Huyện Long Mỹ":
                    getAddress();
                    autoCompleteTextViewVillage.setText("");
                    String[] hLM = new String[]{"Xã Thuận Hoà", "Xã Thuận Hưng", "Xã Vĩnh Thuận Đông", "Xã Vĩnh Viễn", "Xã Vĩnh Viễn A",
                            "Xã Xà Phiên", "Xã Lương Tâm", "Xã Lương Nghĩa"};
                    ArrayAdapter<String> adapterHLM = new ArrayAdapter<>(CreateBoardingHouseActivity.this,
                            R.layout.item_spinner, hLM);
                    autoCompleteTextViewVillage.setAdapter(adapterHLM);
                    break;
                case "Huyện Phụng Hiệp":
                    getAddress();
                    autoCompleteTextViewVillage.setText("");
                    String[] hPH = new String[]{"Thị trấn Cây Dương", "Thị trấn Kinh Cùng", "Thị trấn Búng Tàu",
                            "Xã Phụng Hiệp", "Xã Tân Phước Hưng", "Xã Tân Bình", "Xã Hoà An", "Xã Phương Bình", "Xã Phương Phú",
                            "Xã Hoà Mỹ", "Xã Hiệp Hưng", "Xã Thạnh Hoà", "Xã Bình Thành", "Xã Tân Long", "Xã Long Thạnh"};
                    ArrayAdapter<String> adapterHPH = new ArrayAdapter<>(CreateBoardingHouseActivity.this,
                            R.layout.item_spinner, hPH);
                    autoCompleteTextViewVillage.setAdapter(adapterHPH);
                    break;
                case "Huyện Châu Thành":
                    getAddress();
                    autoCompleteTextViewVillage.setText("");
                    String[] hCT = new String[]{"Thị trấn Ngã Sáu", "Thị trấn Mái Dầm",
                            "Xã Đông Phước", "Xã Đông Phước A", "Xã Phú Hữu", "Xã Phú Tân", "Xã Phú An", "Xã Đông Phú", "Xã Đông Thạnh"};
                    ArrayAdapter<String> adapterHCT = new ArrayAdapter<>(CreateBoardingHouseActivity.this,
                            R.layout.item_spinner, hCT);
                    autoCompleteTextViewVillage.setAdapter(adapterHCT);
                    break;
                case "Huyện Châu Thành A":
                    getAddress();
                    autoCompleteTextViewVillage.setText("");
                    String[] hCTA = new String[]{"Thị trấn Một Ngàn", "Thị trấn Bảy Ngàn", "Thị trấn Cái Tắc", "Thị trấn Rạch Gồi",
                            "Xã Thạnh Xuân", "Xã Tân Phú Thạnh", "Xã Tân Hoà", "Xã Trường Long Tây", "Xã Trường Long A", "Xã Nhơn Nghĩa A"};
                    ArrayAdapter<String> adapterHCTA = new ArrayAdapter<>(CreateBoardingHouseActivity.this,
                            R.layout.item_spinner, hCTA);
                    autoCompleteTextViewVillage.setAdapter(adapterHCTA);
                    break;
            }
        });

        autoCompleteTextViewVillage.setOnItemClickListener((parent, view, position, id) -> getAddress());

    }

    private boolean validateName(String name) {
        if (name.isEmpty()) {
            textInputName.setError("Vui lòng điền tên nhà trọ");
            return false;
        } else {
            textInputName.setError(null);
//            textInputEmail.setEnabled(false);
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
//            textInputEmail.setEnabled(false);
            return true;
        }
    }

    private boolean validateDistance(String distance) {
        if (distance.isEmpty()) {
            textInputDistance.setError("Vui lòng chọn vị trí nhà trọ");
            return false;
        } else {
            textInputDistance.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        }
    }


    private void getRoute(MapboxMap mapboxMap, Point origin, Point destination) {
        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.access_token))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(@NotNull Call<DirectionsResponse> call, @NotNull Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Timber.d("Response code: " + response.code());
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
                distance = Math.ceil(tempDistance * 100) / 100;
                textInputEditTextDistance.setText("Cách ĐHCT " + distance + " km");

            }

            @Override
            public void onFailure(@NotNull Call<DirectionsResponse> call, @NotNull Throwable throwable) {
                Timber.e("Error: %s", throwable.getMessage());
                Toast.makeText(CreateBoardingHouseActivity.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FROM_CREATE_BOARDING_HOUSE && resultCode == PickLocationActivity.RESULT_CODE_FROM_PICK_LOCATION && data != null) {
            latitude = data.getDoubleExtra("latitude", requestCode);
            longitude = data.getDoubleExtra("longitude", requestCode);

            Point ctu = Point.fromLngLat(longitudeCTU, latitudeCTU);
            Point bh = Point.fromLngLat(longitude, latitude);
            getRoute(null, ctu, bh);
        }
    }
}