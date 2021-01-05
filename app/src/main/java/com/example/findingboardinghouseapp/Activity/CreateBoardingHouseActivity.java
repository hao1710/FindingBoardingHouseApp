package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

    ImageButton ibBack;
    TextInputLayout tilName, tilDistrict, tilVillage, tilHamlet, tilEPrice, tilWPrice, tilDescription, tilDistance;
    TextInputEditText edtName, edtHamlet, edtEPrice, edtWPrice, edtDescription, edtDistance;
    AutoCompleteTextView autoCompleteTextViewDistrict, autoCompleteTextViewVillage;
    ImageButton ibPickLocation;
    Button buttonCreateBoardingHouse;
    TextView tvAddress;

    private BoardingHouse boardingHouse;
    private static MapboxDirections client;
    private DirectionsRoute currentRoute;

    private String address;

    ProgressDialog progressDialog;

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

        setOnEditorAction();

        buttonCreateBoardingHouse.setOnClickListener(v -> {
            String nameV = edtName.getText().toString().trim();
            String districtV = Objects.requireNonNull(tilDistrict.getEditText()).getText().toString().trim();
            String villageV = Objects.requireNonNull(tilVillage.getEditText()).getText().toString().trim();
            String hamletV = edtHamlet.getText().toString().trim();
            String ePrice = edtEPrice.getText().toString().trim();
            String wPrice = edtWPrice.getText().toString().trim();
            String descriptionV = edtDescription.getText().toString().trim();
            String distanceV = edtDistance.getText().toString().trim();
            if (!validateName(nameV) | !validateDistrict(districtV) | !validateVillage(villageV) | !validateHamlet(hamletV)
                    | !validateElectricityPrice(ePrice) | !validateWaterPrice(wPrice) | !validateDescription(descriptionV) | !validateDistance(distanceV)) {
                return;
            }
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Vui lòng đợi");
            progressDialog.show();
            BoardingHouseCRUD boardingHouseCRUD = new BoardingHouseCRUD();
            boardingHouseCRUD.setName(nameV);
            boardingHouseCRUD.setAddress(address);
            boardingHouseCRUD.setDistance(distance);
            boardingHouseCRUD.setElectricityPrice(Double.parseDouble(ePrice));
            boardingHouseCRUD.setWaterPrice(Double.parseDouble(wPrice));
            boardingHouseCRUD.setStatus(-1);
            GeoPoint point = new GeoPoint(latitude, longitude);
            boardingHouseCRUD.setOwner(boardingHouse.getIdOwnerBoardingHouse());
            boardingHouseCRUD.setPoint(point);
            boardingHouseCRUD.setDescription(edtDescription.getText().toString().trim());
            FirebaseFirestore.getInstance().collection("boardingHouse").add(boardingHouseCRUD).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Thêm nhà trọ thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_CODE_FROM_CREATE_BOARDING_HOUSE, intent);
                    finish();
                }
            });

        });

        ibPickLocation.setOnClickListener(v -> {
            Intent intentPick = new Intent(getApplicationContext(), PickLocationActivity.class);
            startActivityForResult(intentPick, REQUEST_CODE_FROM_CREATE_BOARDING_HOUSE);
        });

        edtHamlet.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                getAddress();
            }
        });

        ibBack.setOnClickListener(v -> finish());
    }

    private void setOnEditorAction() {
        edtName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = edtHamlet.getText();
                Selection.setSelection(e, edtHamlet.getText().length());
            }
            return false;
        });
        edtHamlet.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = edtEPrice.getText();
                Selection.setSelection(e, edtEPrice.getText().length());
            }
            return false;
        });
        edtEPrice.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = edtWPrice.getText();
                Selection.setSelection(e, edtWPrice.getText().length());
            }
            return false;
        });
        edtWPrice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Editable e = edtDescription.getText();
                    Selection.setSelection(e, edtDescription.getText().length());
                }
                return false;
            }
        });
        edtDescription.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edtDescription.clearFocus();
            }
            return false;
        });
    }

    private void findView() {
        ibBack = findViewById(R.id.cbh_ib_back);

        tilName = findViewById(R.id.cbh_til_name);
        tilDistrict = findViewById(R.id.cbh_til_district);
        tilVillage = findViewById(R.id.cbh_til_village);
        tilHamlet = findViewById(R.id.cbh_til_hamlet);
        tilEPrice = findViewById(R.id.cbh_til_ePrice);
        tilWPrice = findViewById(R.id.cbh_til_wPrice);
        tilDescription = findViewById(R.id.cbh_til_description);
        tilDistance = findViewById(R.id.cbh_til_distance);

        edtName = findViewById(R.id.cbh_edt_name);
        edtHamlet = findViewById(R.id.cbh_edt_hamlet);
        edtEPrice = findViewById(R.id.cbh_edt_ePrice);
        edtWPrice = findViewById(R.id.cbh_edt_wPrice);
        edtDescription = findViewById(R.id.cbh_edt_description);
        edtDistance = findViewById(R.id.cbh_edt_distance);

        autoCompleteTextViewDistrict = findViewById(R.id.cbh_auto_district);
        autoCompleteTextViewVillage = findViewById(R.id.cbh_auto_village);

        tvAddress = findViewById(R.id.cbh_textView_address);

        ibPickLocation = findViewById(R.id.cbh_ib_pick_location);
        buttonCreateBoardingHouse = findViewById(R.id.cbh_button_create);
    }

    @SuppressLint("SetTextI18n")
    private void getAddress() {
        String hamletAddress = edtHamlet.getText().toString().trim();
        String villageAddress = autoCompleteTextViewVillage.getText().toString().trim();
        String districtAddress = autoCompleteTextViewDistrict.getText().toString().trim();
        String provinceAddress = "Hậu Giang";
        address = hamletAddress + ", " + villageAddress + ", " + districtAddress + ", " + provinceAddress;
        if (hamletAddress.isEmpty() | villageAddress.isEmpty() | districtAddress.isEmpty()) {
            tvAddress.setText("Vui lòng chọn địa chỉ");
        } else {
            tvAddress.setText(address);
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
            tilName.setError("Vui lòng điền tên nhà trọ");
            return false;
        } else {
            tilName.setError(null);
            return true;
        }
    }

    private boolean validateDistrict(String district) {
        if (district.isEmpty()) {
            tilDistrict.setError("Vui lòng chọn huyện");
            return false;
        } else {
            tilDistrict.setError(null);
            return true;
        }
    }

    private boolean validateVillage(String village) {
        if (village.isEmpty()) {
            tilVillage.setError("Vui lòng chọn xã");
            return false;
        } else {
            tilVillage.setError(null);
            return true;
        }
    }

    private boolean validateHamlet(String hamlet) {
        if (hamlet.isEmpty()) {
            tilHamlet.setError("Vui lòng điền ấp");
            return false;
        } else {
            tilHamlet.setError(null);
            return true;
        }
    }

    private boolean validateElectricityPrice(String ePrice) {
        if (ePrice.isEmpty()) {
            tilEPrice.setError("Vui lòng điền giá điện");
            return false;
        } else {
            tilEPrice.setError(null);
            return true;
        }
    }

    private boolean validateWaterPrice(String wPrice) {
        if (wPrice.isEmpty()) {
            tilWPrice.setError("Vui lòng điền giá nước");
            return false;
        } else {
            tilWPrice.setError(null);
            return true;
        }
    }

    private boolean validateDescription(String description) {
        if (description.isEmpty()) {
            tilDescription.setError("Vui lòng điền mô tả về nhà trọ");
            return false;
        } else {
            tilDescription.setError(null);
            return true;
        }
    }

    private boolean validateDistance(String distance) {
        if (distance.isEmpty()) {
            tilDistance.setError("Vui lòng chọn vị trí nhà trọ");
            return false;
        } else {
            tilDistance.setError(null);
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
                edtDistance.setText("Cách ĐHCT " + distance + " km");

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