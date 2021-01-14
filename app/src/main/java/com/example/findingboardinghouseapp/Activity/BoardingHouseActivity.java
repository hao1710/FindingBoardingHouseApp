package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.RoomTypeAdapter;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class BoardingHouseActivity extends AppCompatActivity {

    private BoardingHouse boardingHouse;
    private FirebaseFirestore firebaseFirestore;

    public static final int REQUEST_CODE_FROM_BOARDING_HOUSE_ACTIVITY = 41;

    ImageButton ibBack, ibMenu;
    TextView tvName, tvAddress, tvDistance, tvDescription;
    public static TextView tvText;
    RecyclerView rvRoomType;
    ImageButton ibCreateRoomType;

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding_house);

        Intent intent = getIntent();
        boardingHouse = (BoardingHouse) intent.getSerializableExtra("boardingHouse");

        findView();

        ibMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), ibMenu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_in_bha, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.bh_item_update) {
                    BoardingHouse boardingHouseUpdate = boardingHouse;

                    Bundle bundleUpdate = new Bundle();
                    bundleUpdate.putSerializable("boardingHouseUpdate", boardingHouseUpdate);
                    Intent intentUpdate = new Intent(BoardingHouseActivity.this, UpdateBoardingHouseActivity.class);
                    intentUpdate.putExtras(bundleUpdate);
                    startActivityForResult(intentUpdate, REQUEST_CODE_FROM_BOARDING_HOUSE_ACTIVITY);
                    return true;
                }
                return false;
            });
            popupMenu.show();

        });

        ibCreateRoomType.setOnClickListener(v -> {
            RoomType roomType = new RoomType();
            roomType.setIdBoardingHouse(boardingHouse.getIdBoardingHouse());
            Bundle bundle = new Bundle();
            bundle.putSerializable("newRoomType", roomType);
            Intent intentRoomType = new Intent(BoardingHouseActivity.this, CreateRoomTypeActivity.class);
            intentRoomType.putExtras(bundle);
            startActivityForResult(intentRoomType, REQUEST_CODE_FROM_BOARDING_HOUSE_ACTIVITY);
        });
        firebaseFirestore = FirebaseFirestore.getInstance();

        // recyclerView
        rvRoomType.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rvRoomType.setLayoutManager(linearLayout);

        // do something
        setTextBoardingHouse(boardingHouse);

        readDataRoomType();

        ibBack.setOnClickListener(v -> finish());
    }

    private void findView() {
        ibBack = findViewById(R.id.bh_ib_back);
        ibMenu = findViewById(R.id.bh_imageButton_menu);

        ibCreateRoomType = findViewById(R.id.bh_ib_create_roomType);

        tvName = findViewById(R.id.bh_tv_name);
        tvAddress = findViewById(R.id.bh_tv_address);
        tvDistance = findViewById(R.id.bh_tv_distance);
        tvDescription = findViewById(R.id.bh_tv_description);

        rvRoomType = findViewById(R.id.bh_rv_roomType);

        tvText = findViewById(R.id.bh_tv_text);
    }

    @SuppressLint("SetTextI18n")
    private void setTextBoardingHouse(BoardingHouse boardingHouse) {
        tvName.setMaxLines(1);
        tvName.setEllipsize(TextUtils.TruncateAt.END);
        tvName.setText(boardingHouse.getNameBoardingHouse());
        tvAddress.setText(boardingHouse.getAddressBoardingHouse());
        tvDistance.setText("Cách ĐHCT " + boardingHouse.getDistanceBoardingHouse() + " km");
        tvDescription.setMaxLines(2);
        tvDescription.setEllipsize(TextUtils.TruncateAt.END);
        tvDescription.setText(boardingHouse.getDescriptionBoardingHouse());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FROM_BOARDING_HOUSE_ACTIVITY && resultCode == UpdateBoardingHouseActivity.RESULT_CODE_FROM_UPDATE_BOARDING_HOUSE && data != null) {
            Bundle bundleResult = data.getExtras();
            boardingHouse = (BoardingHouse) bundleResult.getSerializable("boardingHouseResult");
            setTextBoardingHouse(boardingHouse);
        }
        if (requestCode == REQUEST_CODE_FROM_BOARDING_HOUSE_ACTIVITY && resultCode == CreateRoomTypeActivity.RESULT_CODE_FROM_CREATE_ROOM_TYPE && data != null) {
            readDataRoomType();
        }
        if (requestCode == REQUEST_CODE_FROM_BOARDING_HOUSE_ACTIVITY && resultCode == CreateRoomActivity.RESULT_CODE_FROM_CREATE_ROOM && data != null) {
            readDataRoomType();
        }
        if (requestCode == REQUEST_CODE_FROM_BOARDING_HOUSE_ACTIVITY && resultCode == UpdateRoomTypeActivity.RESULT_CODE_FROM_UPDATE_ROOM_TYPE_ACTIVITY && data != null) {
            readDataRoomType();
        }
    }

    @SuppressLint("SetTextI18n")
    private void readDataRoomType() {
        ArrayList<RoomType> roomTypeList = new ArrayList<>();
        RoomTypeAdapter newAdapter = new RoomTypeAdapter(getApplicationContext(), roomTypeList);
        rvRoomType.setAdapter(newAdapter);
        firebaseFirestore.collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
                .collection("roomType").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()).getDocuments()) {
                            RoomType roomType = new RoomType();
                            roomType.setIdRoomType(documentSnapshot.getId());
                            roomType.setIdBoardingHouse(boardingHouse.getIdBoardingHouse());
                            roomType.setNameRoomType(documentSnapshot.getString("name"));
                            roomType.setAreaRoomType(documentSnapshot.getDouble("area"));
                            roomType.setPriceRoomType(documentSnapshot.getDouble("price"));
                            roomType.setNumberPeopleRoomType(Objects.requireNonNull(documentSnapshot.getDouble("numberPeople")).intValue());
                            roomType.setDescriptionRoomType(documentSnapshot.getString("description"));
                            roomTypeList.add(roomType);
                        }
                    }
                    newAdapter.notifyDataSetChanged();

                    if(roomTypeList.size() == 0){
                        tvText.setText("Hãy thêm loại phòng và phòng");
                    } else {
                        tvText.setText("Danh sách loại phòng và phòng");
                    }
                });
    }


    @SuppressLint("LogNotTimber")
    @Override
    protected void onStart() {
        Log.i("LifeCycleINN", "onStart");
        super.onStart();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onResume() {
        Log.i("LifeCycleINN", "onResume");
        super.onResume();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onPause() {
        Log.i("LifeCycleINN", "onPause");
        super.onPause();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onStop() {
        Log.i("LifeCycleINN", "onStop");
        super.onStop();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onRestart() {
        Log.i("LifeCycleINN", "onRestart");
        super.onRestart();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onDestroy() {
        Log.i("LifeCycleINN", "onDestroy");
        super.onDestroy();
    }
}