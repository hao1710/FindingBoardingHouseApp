package com.example.findingboardinghouseapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.BoardingHouseCRUD;
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
import com.mapbox.mapboxsdk.maps.MapboxMap;

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

    private TextInputLayout textInputName, textInputAddress, textInputDescription, textInputDistanceParent;
    private TextInputEditText textInputDistance;
    private static MapboxDirections client;
    private DirectionsRoute currentRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_boarding_house);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        BoardingHouse boardingHouse = (BoardingHouse) bundle.getSerializable("boardingHouse");

        // mapping
        Button buttonCreateBoardingHouse = findViewById(R.id.cbh_button_create);
        Button buttonPickLocation = findViewById(R.id.cbh_button_pick_location);
        textInputName = findViewById(R.id.cbh_textInput_name);
        textInputAddress = findViewById(R.id.cbh_textInput_address);
        textInputDescription = findViewById(R.id.cbh_textInput_description);
        textInputDistanceParent = findViewById(R.id.cbh_textInput_distance_parent);
        textInputDistance = findViewById(R.id.cbh_textInput_distance);


        buttonCreateBoardingHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = textInputName.getEditText().getText().toString().trim();
                String address = textInputAddress.getEditText().getText().toString().trim();
                String description = textInputDescription.getEditText().getText().toString().trim();
                String distanceIn = textInputDistance.getText().toString().trim();
                if (!validateDescription(description) | !validateName(name) | !validateAddress(address) | !validateDistance(distanceIn)) {
                    return;
                }
                BoardingHouseCRUD boardingHouseCRUD = new BoardingHouseCRUD();
                boardingHouseCRUD.setName(name);
                boardingHouseCRUD.setAddress(address);
                boardingHouseCRUD.setDistance(distance);
                GeoPoint point = new GeoPoint(latitude, longitude);
                boardingHouseCRUD.setOwner(boardingHouse.getIdOwnerBoardingHouse());
                boardingHouseCRUD.setPoint(point);
                boardingHouseCRUD.setDescription(textInputDescription.getEditText().getText().toString().trim());
                FirebaseFirestore.getInstance().collection("boardingHouse").add(boardingHouseCRUD);
                Toast.makeText(getApplicationContext(), "Thêm nhà trọ thành công", Toast.LENGTH_SHORT).show();
            }
        });
        buttonPickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), PickLocationActivity.class);
                startActivityForResult(intent1, REQUEST_CODE_FROM_CREATE_BOARDING_HOUSE);
            }
        });
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

    private boolean validateAddress(String address) {
        if (address.isEmpty()) {
            textInputAddress.setError("Vui lòng điền địa chỉ nhà trọ");
            return false;
        } else {
            textInputAddress.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        }
    }

    private boolean validateDistance(String distance) {
        if (distance.isEmpty()) {
            textInputDistanceParent.setError("Vui lòng chọn vị trí nhà trọ");
            return false;
        } else {
            textInputDistanceParent.setError(null);
//            textInputEmail.setEnabled(false);
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
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
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
                textInputDistance.setText("Cách ĐHCT " + distance + " km");

            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Timber.e("Error: " + throwable.getMessage());
                Toast.makeText(CreateBoardingHouseActivity.this, "Error: " + throwable.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CODE_FROM_CREATE_BOARDING_HOUSE, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FROM_CREATE_BOARDING_HOUSE && resultCode == PickLocationActivity.RESULT_CODE_FROM_PICK_LOCATION && data != null) {
            latitude = data.getDoubleExtra("latitude", requestCode);
            longitude = data.getDoubleExtra("longitude", requestCode);
            Log.i("DHCT", String.valueOf(latitude) + " " + longitude);
//            calDistance(latitude, longitude);
            Point ctu = Point.fromLngLat(longitudeCTU, latitudeCTU);
            Point bh = Point.fromLngLat(longitude, latitude);
            getRoute(null, ctu, bh);
        }
    }
}