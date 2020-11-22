package com.example.findingboardinghouseapp.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.RoomRecommendationAdapter;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.R;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
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
    private Spinner spinnerNumberPeople, spinnerPrice, spinnerDistance;
    private String numberPeople = "0", distance = "4", price = "4";
    private FirebaseFirestore firebaseFirestore;
    private Boolean pressButton = false;
    private EditText editTextSearch;

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
        ExpandableRelativeLayout expandableRelativeLayout = view.findViewById(R.id.h_expandable_layout);
        Button buttonSearch = view.findViewById(R.id.h_button_search);
        ScrollView scrollView = view.findViewById(R.id.h_scrollView);
        editTextSearch = view.findViewById(R.id.h_editText_search);
        spinnerPrice = view.findViewById(R.id.h_spinner_price);
        spinnerDistance = view.findViewById(R.id.h_spinner_distance);
        spinnerNumberPeople = view.findViewById(R.id.h_spinner_number_people);
        Button buttonRefresh = view.findViewById(R.id.h_button_refresh);

        // recyclerView
        recyclerViewRoomRecommendation.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewRoomRecommendation.setLayoutManager(gridLayoutManager);
        recyclerViewRoomRecommendation.setAdapter(adapter);

        firebaseFirestore.collection("boardingHouse").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                readData(list -> {
                    arrayList.clear();
                    arrayList.addAll(list);
                    if (arrayList.size() > 0) {
                        arrayListRoomRecommendation.clear();
                        arrayListRoomRecommendation.addAll(arrayList);
                    }
                    adapter.notifyDataSetChanged();
                });
            }
        });

        firebaseFirestore.collectionGroup("roomType").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                readData(list -> {
                    arrayList.clear();
                    arrayList.addAll(list);
                    if (arrayList.size() > 0) {
                        arrayListRoomRecommendation.clear();
                        arrayListRoomRecommendation.addAll(arrayList);
                    }
                    adapter.notifyDataSetChanged();
                });
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableRelativeLayout.expand();
//                expandableLinearLayout.toggle();
            }
        });
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableRelativeLayout.collapse();
            }
        });
        // update status of room
//        firebaseFirestore.collection("boardingHouse").document("JYYwAzho2pZ09NCTg8EJ").collection("roomType").document("QVfTthtQrdM8IjXj7OKo")
//                .update("room.11", false);
//        firebaseFirestore.collection("boardingHouse").document("JYYwAzho2pZ09NCTg8EJ").collection("roomType").document("QVfTthtQrdM8IjXj7OKo")
//                .update("room.image.1", "for fun");

        initialSpinner();
        return view;
    }

    private void initialSpinner() {

        List<String> listNumberPeople = new ArrayList<>();
        listNumberPeople.add("Số người ở");
        listNumberPeople.add("1");
        listNumberPeople.add("2");
        listNumberPeople.add("3");
        listNumberPeople.add("4");

//        String listNumberPeople[] = {
//                "Số người","1", "2", "3", "4"
//        };
        ArrayAdapter<String> adapterNumberPeopleSpinner = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listNumberPeople);
        adapterNumberPeopleSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumberPeople.setAdapter(adapterNumberPeopleSpinner);


        spinnerNumberPeople.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (arrayListRoom.size() > 0) {
//                    if (parent.getItemAtPosition(position).toString().equals("Số người ở")) {
//                        numberPeople = "0";
//                    } else {
//                        numberPeople = parent.getItemAtPosition(position).toString();
//                    }
//                    searchRoom(price, distance, numberPeople);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayList<ObjectSpinner> listPrice = new ArrayList<>();
        listPrice.add(new ObjectSpinner("4", "Giá (triệu)"));
        listPrice.add(new ObjectSpinner("1", "1 triệu"));
        listPrice.add(new ObjectSpinner("2", "2 triệu"));
        listPrice.add(new ObjectSpinner("3", "3 triệu"));
        listPrice.add(new ObjectSpinner("4", "4 triệu"));

        ArrayAdapter<ObjectSpinner> adapterPriceSpinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listPrice);
        adapterPriceSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrice.setAdapter(adapterPriceSpinner);

        spinnerPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ObjectSpinner objectSpinner = (ObjectSpinner) parent.getItemAtPosition(position);
                Toast.makeText(getContext(), objectSpinner.key, Toast.LENGTH_SHORT).show();
//                if (arrayListRoom.size() > 0) {
//                    if (parent.getItemAtPosition(position).toString().equals("Giá (triệu)")) {
//
//                        price = "4";
//
//
//                    } else {
//                        price = parent.getItemAtPosition(position).toString();
//                    }
//                    searchRoom(price, distance, numberPeople);
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> listDistance = new ArrayList<>();
        listDistance.add("Khoảng cách (km)");
        listDistance.add("1");
        listDistance.add("2");
        listDistance.add("3");
        listDistance.add("4");

        ArrayAdapter<String> adapterDistanceSpinner = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listDistance);
        adapterDistanceSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistance.setAdapter(adapterDistanceSpinner);

        spinnerDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                if (arrayListRoom.size() > 0) {
//
//                    if (parent.getItemAtPosition(position).toString().equals("Khoảng cách (km)")) {
//
//                        distance = "4";
//                    } else {
//                        distance = parent.getItemAtPosition(position).toString();
//                    }
//                    searchRoom(price, distance, numberPeople);
//
//
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    class ObjectSpinner {
        String key;
        String value;

        public ObjectSpinner(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    private interface FirestoreCallback {
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

