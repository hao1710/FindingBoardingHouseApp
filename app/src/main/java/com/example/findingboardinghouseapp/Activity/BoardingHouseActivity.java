package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class BoardingHouseActivity extends AppCompatActivity {

    private BoardingHouse boardingHouse;
    private RoomTypeAdapter adapter;
    //    private ArrayList<RoomType> arrayList;
    private FirebaseFirestore firebaseFirestore;

    public static final int REQUEST_CODE_FROM_BOARDING_HOUSE = 32;
    public static final int RESULT_CODE_FROM_BOARDING_HOUSE = 31;

    private ImageButton imageButtonMenu;
    private TextView textViewNameBoardingHouse, textViewAddressBoardingHouse, textViewDistanceBoardingHouse, textViewDescription;
    private RecyclerView recyclerViewRoomType;

    @SuppressLint("SetTextI18n")
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
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
//                        case R.id.bh_item_update:
//                            return true;
                        case R.id.bh_item_create_room_type:
                            RoomType roomType = new RoomType();
                            roomType.setIdBoardingHouse(boardingHouse.getIdBoardingHouse());
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("newRoomType", roomType);
                            Intent intent1 = new Intent(BoardingHouseActivity.this, CreateRoomTypeActivity.class);
                            intent1.putExtras(bundle);
                            startActivityForResult(intent1, REQUEST_CODE_FROM_BOARDING_HOUSE);
                            return true;
                    }
                    return false;
                }
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

        FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse()).collection("roomType")
                .addSnapshotListener((value, error) -> {
                    assert value != null;
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        DocumentSnapshot documentSnapshot = dc.getDocument();
                        switch (dc.getType()) {
                            case ADDED:
                            case REMOVED:
                                readDataRoomType();
                                break;
                            case MODIFIED:
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
        if (requestCode == 0) {
            if (resultCode == CreateRoomActivity.RESULT_CODE_FROM_CREATE_ROOM) {
                readDataRoomType();
            }
        }
        if (requestCode == REQUEST_CODE_FROM_BOARDING_HOUSE && resultCode == CreateRoomTypeActivity.RESULT_CODE_FROM_CREATE_ROOM_TYPE && data != null) {
            readDataRoomType();
        }
    }

    private void readDataRoomType() {
        ArrayList<RoomType> roomTypeArrayList = new ArrayList<>();
        RoomTypeAdapter newAdapter = new RoomTypeAdapter(getApplicationContext(), roomTypeArrayList);
        recyclerViewRoomType.setAdapter(newAdapter);
        firebaseFirestore.collection("boardingHouse").document(boardingHouse.getIdBoardingHouse()).collection("roomType").get()
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

                            roomTypeArrayList.add(roomType);
                        }
                    }
                    newAdapter.notifyDataSetChanged();
                });
    }


}