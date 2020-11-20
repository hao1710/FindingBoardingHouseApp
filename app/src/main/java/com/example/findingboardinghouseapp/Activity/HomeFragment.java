package com.example.findingboardinghouseapp.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.findingboardinghouseapp.Adapter.RoomRecommendationAdapter;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // My code under

    private RoomRecommendationAdapter adapter;
    private ArrayList<Room> arrayList;
    private ArrayList<Room> arrayListRoomRecommendation;

    private FirebaseFirestore firebaseFirestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // initial
        arrayList = new ArrayList<>();
        arrayListRoomRecommendation = new ArrayList<>();
        adapter = new RoomRecommendationAdapter(getContext(), arrayListRoomRecommendation);
        firebaseFirestore = FirebaseFirestore.getInstance();

        // mapping
        RecyclerView recyclerViewRoomRecommendation = view.findViewById(R.id.recyclerViewRoomRecommendation);

        // recyclerView
        recyclerViewRoomRecommendation.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewRoomRecommendation.setLayoutManager(gridLayoutManager);
        recyclerViewRoomRecommendation.setAdapter(adapter);

        realTimeUpdate();

        firebaseFirestore.collection("boardingHouse").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                realTimeUpdate();
            }
        });

        firebaseFirestore.collectionGroup("roomType").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                realTimeUpdate();
            }
        });

        // update status of room
//        firebaseFirestore.collection("boardingHouse").document("JYYwAzho2pZ09NCTg8EJ").collection("roomType").document("QVfTthtQrdM8IjXj7OKo")
//                .update("room.11", false);
//        firebaseFirestore.collection("boardingHouse").document("JYYwAzho2pZ09NCTg8EJ").collection("roomType").document("QVfTthtQrdM8IjXj7OKo")
//                .update("room.image.1", "for fun");

        return view;
    }

    private void realTimeUpdate() {
        readData(new FirestoreCallback() {
            @Override
            public void onCallbackBoardingHouse(List<Room> list) {

            }

            @Override
            public void onCallbackRoomType(List<Room> list) {
                arrayList.clear();
                arrayList.addAll(list);
                if (arrayList.size() > 0) {
                    arrayListRoomRecommendation.clear();
                    arrayListRoomRecommendation.addAll(arrayList);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private interface FirestoreCallback {
        void onCallbackBoardingHouse(List<Room> list);

        void onCallbackRoomType(List<Room> list);
    }

    private void readData(FirestoreCallback firestoreCallback) {
        List<Room> listBoardingHouse = new ArrayList<>();
        List<Room> listRoomType = new ArrayList<>();

        firebaseFirestore.collection("boardingHouse").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Room roomBoardingHouse = new Room();
                    roomBoardingHouse.setIdBoardingHouse(documentSnapshot.getId());
                    roomBoardingHouse.setNameBoardingHouse(documentSnapshot.getString("name"));
                    roomBoardingHouse.setAddressBoardingHouse(documentSnapshot.getString("address"));
                    roomBoardingHouse.setDescriptionBoardingHouse(documentSnapshot.getString("description"));
                    roomBoardingHouse.setDistanceBoardingHouse(documentSnapshot.getDouble("distance"));
                    roomBoardingHouse.setIdOwnerBoardingHouse(documentSnapshot.getString("owner"));
                    listBoardingHouse.add(roomBoardingHouse);
                }
                firestoreCallback.onCallbackBoardingHouse(listBoardingHouse);
                for (Room room : listBoardingHouse) {
                    firebaseFirestore.collection("boardingHouse").document(room.getIdBoardingHouse()).collection("roomType").get()
                            .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots1.getDocuments()) {

                                    Room roomRoomType = new Room();
                                    roomRoomType = room;
//                                        roomRoomType.setIdBoardingHouse(room.getIdBoardingHouse());
//                                        roomRoomType.setNameBoardingHouse(room.getNameBoardingHouse());
//                                        roomRoomType.setAddressBoardingHouse(room.getAddressBoardingHouse());
//                                        roomRoomType.setDescriptionBoardingHouse(room.getDescriptionBoardingHouse());
//                                        roomRoomType.setDistanceBoardingHouse(room.getDistanceBoardingHouse());
//                                        roomRoomType.setIdOwnerBoardingHouse(room.getIdOwnerBoardingHouse());

                                    roomRoomType.setIdRoomType(documentSnapshot.getId());
                                    roomRoomType.setNameRoomType(documentSnapshot.getString("name"));
                                    roomRoomType.setNumberPeopleRoomType(documentSnapshot.getDouble("numberPeople").intValue());
                                    roomRoomType.setPriceRoomType(documentSnapshot.getDouble("price"));
                                    roomRoomType.setAreaRoomType(documentSnapshot.getDouble("area"));
                                    Map<String, Object> map = documentSnapshot.getData();
                                    Timber.i("----------------");
                                    assert map != null;
                                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                                        if (entry.getKey().equals("room")) {
                                            Map<String, Object> mapRoom = (Map<String, Object>) entry.getValue();
                                            for (Map.Entry<String, Object> field : mapRoom.entrySet()) {
                                                Timber.i(field.getKey().toString() + ": " + field.getValue().toString());
                                                Room roomRoom = new Room();
                                                roomRoom.setIdBoardingHouse(roomRoomType.getIdBoardingHouse());
                                                roomRoom.setNameBoardingHouse(roomRoomType.getNameBoardingHouse());
                                                roomRoom.setAddressBoardingHouse(roomRoomType.getAddressBoardingHouse());
                                                roomRoom.setDescriptionBoardingHouse(roomRoomType.getDescriptionBoardingHouse());
                                                roomRoom.setDistanceBoardingHouse(roomRoomType.getDistanceBoardingHouse());
                                                roomRoom.setIdOwnerBoardingHouse(roomRoomType.getIdOwnerBoardingHouse());

                                                roomRoom.setIdRoomType(roomRoomType.getIdRoomType());
                                                roomRoom.setNameRoomType(roomRoomType.getNameRoomType());
                                                roomRoom.setNumberPeopleRoomType(roomRoomType.getNumberPeopleRoomType());
                                                roomRoom.setPriceRoomType(roomRoomType.getPriceRoomType());
                                                roomRoom.setAreaRoomType(roomRoomType.getAreaRoomType());
                                                Map<String, Object> mapField = (Map<String, Object>) field.getValue();
                                                for (Map.Entry<String, Object> image : mapField.entrySet()) {

//                                                    Log.i("MAP", image.getKey().toString() + ": " + image.getValue().toString());
                                                    if (image.getKey().equals("image")) {
                                                        roomRoom.setImageRoom(image.getValue().toString());
                                                    }
                                                    if (image.getKey().equals("description")) {
                                                        roomRoom.setDescriptionRoom(image.getValue().toString());
                                                    }
                                                    if (image.getKey().equals("status")) {
                                                        if (image.getValue().toString().equals("false")) {
                                                            listRoomType.add(roomRoom);
                                                            Log.i("MAP", "ADDING");
                                                        }
                                                    }
                                                    Log.i("MAP", "ADDED");
                                                }
                                            }
                                        }
                                    }
                                }
                                firestoreCallback.onCallbackRoomType(listRoomType);
                            });
                }
            }
        });
    }
}

