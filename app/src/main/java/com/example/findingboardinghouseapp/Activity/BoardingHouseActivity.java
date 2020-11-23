package com.example.findingboardinghouseapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.RoomTypeAdapter;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BoardingHouseActivity extends AppCompatActivity {

    private BoardingHouse boardingHouse;
    private RoomTypeAdapter adapter;
//    private ArrayList<RoomType> arrayList;
    private FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerViewRoomType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boarding_house);

        Intent intent = getIntent();
        boardingHouse = (BoardingHouse) intent.getSerializableExtra("boardingHouse");

        // mapping
        recyclerViewRoomType = findViewById(R.id.recyclerViewRoomType);
        TextView textViewNameBoardingHouse = findViewById(R.id.bh_name_boarding_house);
        TextView textViewAddressBoardingHouse = findViewById(R.id.bh_address_boarding_house);
        TextView textViewDistanceBoardingHouse = findViewById(R.id.bh_distance_boarding_house);

        // initial
//        arrayList = new ArrayList<>();
//        adapter = new RoomTypeAdapter(getApplicationContext(), arrayList);
        firebaseFirestore = FirebaseFirestore.getInstance();

        // recyclerView
        recyclerViewRoomType.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewRoomType.setLayoutManager(linearLayout);
        recyclerViewRoomType.setAdapter(adapter);

        // do something
        readDataRoomType();

        textViewNameBoardingHouse.setText(boardingHouse.getNameBoardingHouse());
        textViewAddressBoardingHouse.setText("Địa chỉ: " + boardingHouse.getAddressBoardingHouse());
        textViewDistanceBoardingHouse.setText("Cách ĐHCT " + boardingHouse.getDistanceBoardingHouse() + " km");
        FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse()).collection("roomType")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        readDataRoomType();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == CreateRoomActivity.RESULT_CODE_FROM_CREATE_ROOM) {
                readDataRoomType();
            }
        }
    }

    private void readDataRoomType() {
        ArrayList<RoomType> roomTypeArrayList = new ArrayList<>();
        RoomTypeAdapter newAdapter = new RoomTypeAdapter(getApplicationContext(), roomTypeArrayList);
        recyclerViewRoomType.setAdapter(newAdapter);
        firebaseFirestore.collection("boardingHouse").document(boardingHouse.getIdBoardingHouse()).collection("roomType").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
//                            arrayList.clear();
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                RoomType roomType = new RoomType();
                                roomType.setIdRoomType(documentSnapshot.getId());
                                roomType.setIdBoardingHouse(boardingHouse.getIdBoardingHouse());
                                roomType.setNameRoomType(documentSnapshot.getString("name"));
                                roomType.setAreaRoomType(documentSnapshot.getDouble("area"));
                                roomType.setPriceRoomType(documentSnapshot.getDouble("price"));
                                roomType.setNumberPeopleRoomType(documentSnapshot.getDouble("numberPeople").intValue());
                                roomType.setExpanded(false);
                                roomTypeArrayList.add(roomType);
                            }
                        }
                        newAdapter.notifyDataSetChanged();
                    }
                });
    }


}