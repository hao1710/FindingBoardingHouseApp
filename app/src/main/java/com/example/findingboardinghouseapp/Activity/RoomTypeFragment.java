package com.example.findingboardinghouseapp.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.RoomTypeAdminAdapter;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class RoomTypeFragment extends Fragment {

    public RoomTypeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //
    ArrayList<RoomType> roomTypeList;
    RecyclerView rvRoomType;
    TextView tvName, tvEmail, tvPhoneNumber;
    RoomTypeAdminAdapter adapter;
    Landlord landlord;
    BoardingHouse boardingHouse;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_type, container, false);
        Bundle bundle = new Bundle();
        bundle = this.getArguments();
        assert bundle != null;
        boardingHouse = new BoardingHouse();
        boardingHouse = (BoardingHouse) bundle.getSerializable("inn");
        roomTypeList = new ArrayList<>();
        landlord = (Landlord) bundle.getSerializable("landlord");
        // findView
        rvRoomType = view.findViewById(R.id.ad_rv_roomType);
        tvName = view.findViewById(R.id.tv_nameL);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhoneNumber = view.findViewById(R.id.tv_phoneNumber);
        tvName.setMaxLines(1);
        tvName.setEllipsize(TextUtils.TruncateAt.END);

        setText(landlord);


        // recyclerView
        rvRoomType.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvRoomType.setLayoutManager(linearLayoutManager);

        adapter = new RoomTypeAdminAdapter(getContext(), roomTypeList);
        rvRoomType.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("landlord")
                .document(landlord.getIdLandlord())
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        landlord.setNameLandlord(value.getString("name"));
                        landlord.setAddressLandlord(value.getString("address"));
                        landlord.setPhoneNumberLandlord(value.getString("phoneNumber"));
                        setText(landlord);
                    }
                });

//        FirebaseFirestore.getInstance().collection("boardingHouse").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @SuppressLint("LogNotTimber")
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                for (DocumentChange documentChange : value.getDocumentChanges()) {
//                    DocumentSnapshot documentSnapshot = documentChange.getDocument();
//                    if (documentSnapshot.getId().equals(boardingHouse.getIdBoardingHouse())) {
//                        Log.i("HA3", documentChange.getType() + " thang");
//
//                        if (documentChange.getType() == DocumentChange.Type.REMOVED) {
//
//                            Toast.makeText(getContext(), "Nhà trọ đã bị xóa", Toast.LENGTH_SHORT).show();
//                            InnManagementActivity.ibBack.callOnClick();
//                        } else {
//
//                        }
//                    }
//
//                }
////                for (DocumentChange documentChange : value.getDocumentChanges()) {
////                    DocumentSnapshot documentSnapshot = documentChange.getDocument();
////                    if (documentSnapshot.getId().equals(boardingHouse.getIdBoardingHouse())) {
////                        switch (documentChange.getType()) {
////                            case ADDED:
////                            case MODIFIED:
////                                FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
////                                        .collection("roomType").addSnapshotListener((value1, error1) -> {
////                                    roomTypeList.clear();
////                                    if (value != null) {
////                                        for (DocumentSnapshot documentSnapshot1 : value1.getDocuments()) {
////                                            RoomType roomType = new RoomType();
////                                            roomType.setIdRoomType(documentSnapshot1.getId());
////                                            roomType.setIdBoardingHouse(boardingHouse.getIdBoardingHouse());
////                                            roomType.setNameRoomType(documentSnapshot1.getString("name"));
////                                            roomType.setAreaRoomType(documentSnapshot1.getDouble("area"));
////                                            roomType.setPriceRoomType(documentSnapshot1.getDouble("price"));
////                                            roomType.setNumberPeopleRoomType(documentSnapshot1.getDouble("numberPeople").intValue());
////                                            roomTypeList.add(roomType);
////                                        }
////                                        adapter.notifyDataSetChanged();
////                                    }
////                                });
////
////                            case REMOVED:
////                                Toast.makeText(getContext(), "Nhà trọ đã bị xóa", Toast.LENGTH_SHORT).show();
////                                InnManagementActivity.ibBack.callOnClick();
////                                break;
////                        }
////                    }
////                }
//            }
//        });


        FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
                .collection("roomType")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value != null) {
                            roomTypeList.clear();
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                RoomType roomType = new RoomType();
                                roomType.setIdRoomType(documentSnapshot.getId());
                                roomType.setIdBoardingHouse(boardingHouse.getIdBoardingHouse());
                                roomType.setNameRoomType(documentSnapshot.getString("name"));
                                roomType.setAreaRoomType(documentSnapshot.getDouble("area"));
                                roomType.setPriceRoomType(documentSnapshot.getDouble("price"));
                                roomType.setNumberPeopleRoomType(documentSnapshot.getDouble("numberPeople").intValue());
                                roomTypeList.add(roomType);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
//        FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
//                .addSnapshotListener((value, error) -> {
//                    if (error != null) {
//                        Toast.makeText(getContext(), "Nhà trọ đã bị xóa", Toast.LENGTH_SHORT).show();
//                        InnManagementActivity.ibBack.callOnClick();
//                    }
//                    assert value != null;
//                    if (value.exists()) {
//                        FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
//                                .collection("roomType").addSnapshotListener((value1, error1) -> {
//                            if (error1 != null) {
//                                Log.i("HA3", "hangadas");
//                            }
//
//                            if (value1 != null) {
//                                roomTypeList.clear();
//                                Log.i("HA3", "hang");
//                                for (DocumentSnapshot documentSnapshot1 : value1.getDocuments()) {
//                                    RoomType roomType = new RoomType();
//                                    roomType.setIdRoomType(documentSnapshot1.getId());
//                                    roomType.setIdBoardingHouse(boardingHouse.getIdBoardingHouse());
//                                    roomType.setNameRoomType(documentSnapshot1.getString("name"));
//                                    roomType.setAreaRoomType(documentSnapshot1.getDouble("area"));
//                                    roomType.setPriceRoomType(documentSnapshot1.getDouble("price"));
//                                    roomType.setNumberPeopleRoomType(documentSnapshot1.getDouble("numberPeople").intValue());
//                                    roomTypeList.add(roomType);
//                                }
//                                adapter.notifyDataSetChanged();
//                            }
//                        });
//
//                    }
//                });
//        FirebaseFirestore.getInstance().collection("boardingHouse").addSnapshotListener((value, error) -> {
//            for (DocumentChange documentChange : value.getDocumentChanges()) {
//                DocumentSnapshot documentSnapshot = documentChange.getDocument();
//                if (documentSnapshot.getId().equals(boardingHouse.getIdBoardingHouse())) {
//                    Log.i("HA3", documentChange.getType() + " asdas");
//                    if (documentChange.getType() == DocumentChange.Type.REMOVED) {
//                        Toast.makeText(getContext(), "Nhà trọ đã bị xóa", Toast.LENGTH_SHORT).show();
//                        InnManagementActivity.ibBack.callOnClick();
//                    }
//                }
//            }
//        });


        return view;
    }

    private void setText(Landlord landlord) {
        tvName.setText("Tên: " + landlord.getNameLandlord());
        tvEmail.setText(landlord.getEmailLandlord());
        tvPhoneNumber.setText(landlord.getPhoneNumberLandlord());
    }
}