package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
import com.google.firebase.firestore.DocumentChange;
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
    public static final int REQUEST_CODE_FROM_BOARDING_HOUSE = 32;
    public static final int RESULT_CODE_FROM_BOARDING_HOUSE = 31;
    private TextView textViewDescription;
    @SuppressLint("SetTextI18n")
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
        ImageButton imageButton = findViewById(R.id.bh_imageButton_menu);

        textViewDescription = findViewById(R.id.bh_textView_description);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), imageButton);
                popupMenu.getMenuInflater().inflate(R.menu.menu_popup_in_bha, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bh_item_update:
                                return true;
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

            }
        });
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
        //readDataRoomType();

        textViewNameBoardingHouse.setText(boardingHouse.getNameBoardingHouse());
        textViewAddressBoardingHouse.setText(boardingHouse.getAddressBoardingHouse());
        textViewDistanceBoardingHouse.setText("Cách ĐHCT " + boardingHouse.getDistanceBoardingHouse() + " km");
        textViewDescription.setText(boardingHouse.getDescriptionBoardingHouse());
        FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse()).collection("roomType")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
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
        if (requestCode == REQUEST_CODE_FROM_BOARDING_HOUSE && resultCode == CreateRoomTypeActivity.RESULT_CODE_FROM_CREATE_ROOM_TYPE && data != null) {
            readDataRoomType();
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

                                roomTypeArrayList.add(roomType);
                            }
                        }
                        newAdapter.notifyDataSetChanged();
                    }
                });
    }


}