package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Objects;

public class BoardingHouseActivity extends AppCompatActivity {

    private BoardingHouse boardingHouse;
    private RoomTypeAdapter adapter;
    private FirebaseFirestore firebaseFirestore;

    public static final int REQUEST_CODE_FROM_BOARDING_HOUSE_ACTIVITY = 41;

    private ImageButton imageButtonMenu;
    private TextView textViewNameBoardingHouse, textViewAddressBoardingHouse, textViewDistanceBoardingHouse, textViewDescription;
    private RecyclerView recyclerViewRoomType;
    private TextView tvDSR;
    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding_house);

        Intent intent = getIntent();
        boardingHouse = (BoardingHouse) intent.getSerializableExtra("boardingHouse");

        findView();

        imageButtonMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), imageButtonMenu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_in_bha, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.bh_item_update:
                        BoardingHouse boardingHouseUpdate = boardingHouse;
//
                        Bundle bundleUpdate = new Bundle();
                        bundleUpdate.putSerializable("boardingHouseUpdate", boardingHouseUpdate);
                        Intent intentUpdate = new Intent(BoardingHouseActivity.this, UpdateBoardingHouseActivity.class);
                        intentUpdate.putExtras(bundleUpdate);
                        startActivityForResult(intentUpdate, REQUEST_CODE_FROM_BOARDING_HOUSE_ACTIVITY);
                        return true;
                    case R.id.bh_item_create_room_type:
                        RoomType roomType = new RoomType();
                        roomType.setIdBoardingHouse(boardingHouse.getIdBoardingHouse());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("newRoomType", roomType);
                        Intent intent1 = new Intent(BoardingHouseActivity.this, CreateRoomTypeActivity.class);
                        intent1.putExtras(bundle);
                        startActivityForResult(intent1, REQUEST_CODE_FROM_BOARDING_HOUSE_ACTIVITY);
                        return true;
                }
                return false;
            });
            popupMenu.show();

        });
        firebaseFirestore = FirebaseFirestore.getInstance();

        // recyclerView
        recyclerViewRoomType.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewRoomType.setLayoutManager(linearLayout);
        recyclerViewRoomType.setAdapter(adapter);

        // do something
        setTextBoardingHouse(boardingHouse);

        readDataRoomType();
        FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
                .collection("roomType")
                .addSnapshotListener((value, error) -> {


                    assert value != null;
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        DocumentSnapshot documentSnapshot = dc.getDocument();
                        switch (dc.getType()) {
                            case ADDED:
                            case REMOVED:
                                readDataRoomType();
                                break;
                        }
                    }
                });
    }

    private void findView() {
        imageButtonMenu = findViewById(R.id.bh_imageButton_menu);
        textViewNameBoardingHouse = findViewById(R.id.bh_name_boarding_house);
        textViewAddressBoardingHouse = findViewById(R.id.bh_address_boarding_house);
        textViewDistanceBoardingHouse = findViewById(R.id.bh_distance_boarding_house);
        textViewDescription = findViewById(R.id.bh_textView_description);
        recyclerViewRoomType = findViewById(R.id.recyclerViewRoomType);

        tvDSR = findViewById(R.id.inn_dsr);
    }

    @SuppressLint("SetTextI18n")
    private void setTextBoardingHouse(BoardingHouse boardingHouse) {
        textViewNameBoardingHouse.setText(boardingHouse.getNameBoardingHouse());
        textViewAddressBoardingHouse.setText(boardingHouse.getAddressBoardingHouse());
        textViewDistanceBoardingHouse.setText("Cách ĐHCT " + boardingHouse.getDistanceBoardingHouse() + " km");
        textViewDescription.setMaxLines(2);
        textViewDescription.setEllipsize(TextUtils.TruncateAt.END);
        textViewDescription.setText(boardingHouse.getDescriptionBoardingHouse());
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
        ArrayList<RoomType> roomTypeArrayList = new ArrayList<>();
        Log.i("SIZEZZZ", "size " + roomTypeArrayList.size());
        RoomTypeAdapter newAdapter = new RoomTypeAdapter(getApplicationContext(), roomTypeArrayList);
        recyclerViewRoomType.setAdapter(newAdapter);
        firebaseFirestore.collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
                .collection("roomType").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                            arrayList.clear();
                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()).getDocuments()) {
                            RoomType roomType = new RoomType();
                            roomType.setIdRoomType(documentSnapshot.getId());
                            roomType.setIdBoardingHouse(boardingHouse.getIdBoardingHouse());
                            roomType.setNameRoomType(documentSnapshot.getString("name"));
                            roomType.setAreaRoomType(documentSnapshot.getDouble("area"));
                            roomType.setPriceRoomType(documentSnapshot.getDouble("price"));
                            roomType.setNumberPeopleRoomType(Objects.requireNonNull(documentSnapshot.getDouble("numberPeople")).intValue());
                            roomType.setDescriptionRoomType(documentSnapshot.getString("description"));
                            roomTypeArrayList.add(roomType);
                        }
                    }
                    newAdapter.notifyDataSetChanged();
                    Log.i("SIZEZZZ", "size IN " + roomTypeArrayList.size());
                    if (roomTypeArrayList.size() != 0) {
                        tvDSR.setText("Danh sách loại phòng và phòng trọ");

                    } else {
                        tvDSR.setText("Hãy thêm loại phòng và phòng");
                    }
                });
    }
}