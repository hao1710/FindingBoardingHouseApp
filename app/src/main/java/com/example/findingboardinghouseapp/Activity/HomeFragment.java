package com.example.findingboardinghouseapp.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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

    private RoomRecommendationAdapter adapter, adapterSearch;
    private ArrayList<Room> arrayListRoomRecommendation, arrayListRoomSearch, arrayListRoomSearchTemp;
    private Spinner spinnerNumberPeople, spinnerPrice, spinnerDistance;
    private double numberPeople, distance, price;
    private FirebaseFirestore firebaseFirestore;
    private Boolean pressButton = false;
    private EditText editTextSearch;
    RecyclerView recyclerViewRoomRecommendation;
    private ImageView imageViewNumberPeople, imageViewPrice, imageViewDistance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        // mapping
        recyclerViewRoomRecommendation = view.findViewById(R.id.recyclerViewRoomRecommendation);
        ExpandableRelativeLayout expandableRelativeLayout = view.findViewById(R.id.h_expandable_layout);
//        Button buttonSearch = view.findViewById(R.id.h_button_search);
        ScrollView scrollView = view.findViewById(R.id.h_scrollView);
        editTextSearch = view.findViewById(R.id.h_editText_search);
        spinnerPrice = view.findViewById(R.id.h_spinner_price);
        spinnerDistance = view.findViewById(R.id.h_spinner_distance);
        spinnerNumberPeople = view.findViewById(R.id.h_spinner_number_people);
        imageViewDistance = view.findViewById(R.id.h_imageView_spinner_distance);
        imageViewNumberPeople = view.findViewById(R.id.h_imageView_spinner_number_people);
        imageViewPrice = view.findViewById(R.id.h_imageView_spinner_price);
        ImageButton imageButtonSearch = view.findViewById(R.id.h_imageButton_search);
        ImageButton imageButtonRefresh = view.findViewById(R.id.h_imageButton_refresh);
        // initial
        arrayListRoomRecommendation = new ArrayList<>();
        arrayListRoomSearch = new ArrayList<>();
        arrayListRoomSearchTemp = new ArrayList<>();
        adapter = new RoomRecommendationAdapter(getContext(), arrayListRoomRecommendation);
        adapterSearch = new RoomRecommendationAdapter(getContext(), arrayListRoomSearch);

        // recyclerView
        recyclerViewRoomRecommendation.setHasFixedSize(true);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
//        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
//        recyclerViewRoomRecommendation.setLayoutManager(gridLayoutManager);

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewRoomRecommendation.setLayoutManager(linearLayout);

        firebaseFirestore.collection("boardingHouse").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (arrayListRoomSearch.size() == 0) {

                    readData(list -> {
                        ArrayList<Room> arrayList = new ArrayList<>();
                        arrayList.addAll(list);
                        recyclerViewRoomRecommendation.setAdapter(adapter);
                        if (arrayList.size() > 0) {
                            arrayListRoomRecommendation.clear();
                            arrayListRoomRecommendation.addAll(arrayList);
                        }
                        adapter.notifyDataSetChanged();
                    });
                } else {
                    readData(list -> {
                        ArrayList<Room> arrayList = new ArrayList<>();
                        arrayList.addAll(list);

                        if (arrayList.size() > 0) {
                            arrayListRoomRecommendation.clear();
                            arrayListRoomRecommendation.addAll(arrayList);
                        }
                    });
                }
            }
        });

        firebaseFirestore.collectionGroup("roomType").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (arrayListRoomSearch.size() == 0) {

                    readData(list -> {
                        ArrayList<Room> arrayList = new ArrayList<>();
                        arrayList.addAll(list);
                        recyclerViewRoomRecommendation.setAdapter(adapter);
                        if (arrayList.size() > 0) {
                            arrayListRoomRecommendation.clear();
                            arrayListRoomRecommendation.addAll(arrayList);
                        }
                        adapter.notifyDataSetChanged();
                    });
                } else {
                    readData(list -> {
                        ArrayList<Room> arrayList = new ArrayList<>();
                        arrayList.addAll(list);

                        if (arrayList.size() > 0) {
                            arrayListRoomRecommendation.clear();
                            arrayListRoomRecommendation.addAll(arrayList);
                        }
                    });
                }

            }
        });
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayListRoomRecommendation.size() > 0) {
                    arrayListRoomSearch.clear();
                    recyclerViewRoomRecommendation.setAdapter(adapterSearch);
                    String input = editTextSearch.getText().toString().toLowerCase().trim();

                    for (Room room : arrayListRoomRecommendation) {
                        if (room.getAddressBoardingHouse().toLowerCase().contains(input) || room.getNameBoardingHouse().toLowerCase().contains(input)) {
                            arrayListRoomSearch.add(room);
                        }
                    }
                    if (arrayListRoomSearch.size() == 0) {
                        Toast.makeText(getContext(), "Không tìm thấy nhà trọ", Toast.LENGTH_SHORT).show();
                    }
                    pressButton = true;
                    adapterSearch.notifyDataSetChanged();
//                    expandableRelativeLayout.expand();
                    arrayListRoomSearchTemp.clear();
                    arrayListRoomSearchTemp.addAll(arrayListRoomSearch);

                } else {
                    Toast.makeText(getContext(), "Không còn phòng trống", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewRoomRecommendation.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                editTextSearch.setText("");
                arrayListRoomSearch.clear();
                arrayListRoomSearchTemp.clear();
                spinnerDistance.setSelection(0);
                spinnerPrice.setSelection(0);
                spinnerNumberPeople.setSelection(0);
//                expandableRelativeLayout.collapse();
                pressButton = false;
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

    private void searchRoom(double price, double distance, double numberPeople) {
        arrayListRoomSearch.clear();
        recyclerViewRoomRecommendation.setAdapter(adapterSearch);
        if (pressButton) {
            ArrayList<Room> arrR = (ArrayList<Room>) arrayListRoomSearchTemp.clone();
            for (Room room : arrR) {
                if (room.getDistanceBoardingHouse() <= distance && room.getPriceRoomType() <= price && room.getNumberPeopleRoomType() >= numberPeople) {
                    arrayListRoomSearch.add(room);
                }
            }
        } else {
            for (Room room : arrayListRoomRecommendation) {
                if (room.getDistanceBoardingHouse() <= distance && room.getPriceRoomType() <= price && room.getNumberPeopleRoomType() >= numberPeople) {
                    arrayListRoomSearch.add(room);
                }
            }
        }
        adapterSearch.notifyDataSetChanged();


    }

    private void initialSpinner() {

        ArrayList<ObjectSpinner> listNumberPeople = new ArrayList<>();
        listNumberPeople.add(new ObjectSpinner(0, "Số người ở"));
        listNumberPeople.add(new ObjectSpinner(1, "1 người ở"));
        listNumberPeople.add(new ObjectSpinner(2, "2 người ở"));
        listNumberPeople.add(new ObjectSpinner(3, "3 người ở"));
        listNumberPeople.add(new ObjectSpinner(4, "4 người ở"));
        listNumberPeople.add(new ObjectSpinner(5, "Hơn 4 người ở"));

        ArrayAdapter<ObjectSpinner> adapterNumberPeopleSpinner = new ArrayAdapter<ObjectSpinner>(getContext(), android.R.layout.simple_spinner_item, listNumberPeople);
        adapterNumberPeopleSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumberPeople.setAdapter(adapterNumberPeopleSpinner);
        imageViewNumberPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerNumberPeople.performClick();

            }
        });
        spinnerNumberPeople.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ObjectSpinner objectSpinner = (ObjectSpinner) parent.getItemAtPosition(position);
                numberPeople = objectSpinner.key;
                searchRoom(price, distance, numberPeople);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<ObjectSpinner> listPrice = new ArrayList<>();
        listPrice.add(new ObjectSpinner(99, "Giá (triệu)"));
        listPrice.add(new ObjectSpinner(1, "Dưới 1 triệu"));
        listPrice.add(new ObjectSpinner(2, "Dưới 2 triệu"));
        listPrice.add(new ObjectSpinner(3, "Dưới 3 triệu"));
        listPrice.add(new ObjectSpinner(4, "Dưới 4 triệu"));
        listPrice.add(new ObjectSpinner(99, "Trên 4 triệu"));
        ArrayAdapter<ObjectSpinner> adapterPriceSpinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listPrice);
        adapterPriceSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrice.setAdapter(adapterPriceSpinner);
        imageViewPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerPrice.performClick();
            }
        });
        spinnerPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ObjectSpinner objectSpinner = (ObjectSpinner) parent.getItemAtPosition(position);

                price = objectSpinner.key;
                searchRoom(price, distance, numberPeople);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<ObjectSpinner> listDistance = new ArrayList<>();
        listDistance.add(new ObjectSpinner(99, "Khoảng cách (km)"));
        listDistance.add(new ObjectSpinner(1, "Trong 1 km"));
        listDistance.add(new ObjectSpinner(2, "Trong 2 km"));
        listDistance.add(new ObjectSpinner(3, "Trong 3 km"));
        listDistance.add(new ObjectSpinner(4, "Trong 4 km"));
        listDistance.add(new ObjectSpinner(99, "Ngoài 4 km"));

        ArrayAdapter<ObjectSpinner> adapterDistanceSpinner = new ArrayAdapter<ObjectSpinner>(getContext(), android.R.layout.simple_spinner_item, listDistance);
        adapterDistanceSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistance.setAdapter(adapterDistanceSpinner);
        imageViewDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDistance.performClick();
            }
        });
        spinnerDistance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ObjectSpinner objectSpinner = (ObjectSpinner) parent.getItemAtPosition(position);
                distance = objectSpinner.key;
                searchRoom(price, distance, numberPeople);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    class ObjectSpinner {
        double key;
        String value;

        public ObjectSpinner(double key, String value) {
            this.key = key;
            this.value = value;
        }

        public double getKey() {
            return key;
        }

        public void setKey(double key) {
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
        List<Room> listRoom = new ArrayList<>();

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
                    roomBoardingHouse.setLatitude(documentSnapshot.getGeoPoint("point").getLatitude());
                    roomBoardingHouse.setLongitude(documentSnapshot.getGeoPoint("point").getLongitude());
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
                                    roomRoomType.setDescriptionRoomType(documentSnapshot.getString("description"));

                                    Map<String, Object> map = documentSnapshot.getData();

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
                                                roomRoom.setLatitude(roomRoomType.getLatitude());
                                                roomRoom.setLongitude(roomRoomType.getLongitude());

                                                roomRoom.setIdRoomType(roomRoomType.getIdRoomType());
                                                roomRoom.setNameRoomType(roomRoomType.getNameRoomType());
                                                roomRoom.setNumberPeopleRoomType(roomRoomType.getNumberPeopleRoomType());
                                                roomRoom.setPriceRoomType(roomRoomType.getPriceRoomType());
                                                roomRoom.setAreaRoomType(roomRoomType.getAreaRoomType());
                                                roomRoom.setDescriptionRoomType(roomRoomType.getDescriptionRoomType());
                                                Map<String, Object> mapField = (Map<String, Object>) field.getValue();
                                                for (Map.Entry<String, Object> image : mapField.entrySet()) {

//                                                    Log.i("MAP", image.getKey().toString() + ": " + image.getValue().toString());
                                                    if (image.getKey().equals("image")) {
                                                        roomRoom.setImageRoom(image.getValue().toString());
                                                    }

                                                    if (image.getKey().equals("status")) {
                                                        if (image.getValue().toString().equals("false")) {
                                                            listRoom.add(roomRoom);
                                                        }
                                                    }

                                                }
                                            }
                                        }
                                    }
                                }
                                firestoreCallback.onCallbackRoomType(listRoom);
                            });
                }
            }
        });
    }
}

