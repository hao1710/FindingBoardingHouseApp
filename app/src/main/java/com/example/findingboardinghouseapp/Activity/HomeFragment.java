package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.RoomRecommendationAdapter;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private EditText editTextSearch;
    private Button buttonRefresh;
    private Spinner spinnerPrice, spinnerDistance, spinnerNumberPeople, spinnerFilter;
    private RecyclerView recyclerViewRoomRecommendation;

    private RoomRecommendationAdapter adapter, adapterSearch;
    private ArrayList<Room> arrayListRoomRecommendation, arrayListRoomSearch, arrayListRoomSearchTemp;

    private double numberPeople, distance, price, filterValue;
    private FirebaseFirestore firebaseFirestore;
    private Boolean pressButton = false, pressSpinner = false;
    TextView tvNumberRoom;

    private ImageView imageViewNumberPeople, imageViewPrice, imageViewDistance, ivFilter;

    @SuppressLint({"SetTextI18n", "NewApi"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        findView(view);


        firebaseFirestore = FirebaseFirestore.getInstance();

        initialSpinner();


        arrayListRoomRecommendation = new ArrayList<>();
        arrayListRoomSearch = new ArrayList<>();
        arrayListRoomSearchTemp = new ArrayList<>();
        adapter = new RoomRecommendationAdapter(getContext(), arrayListRoomRecommendation);
        adapterSearch = new RoomRecommendationAdapter(getContext(), arrayListRoomSearch);

        // recyclerView

        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewRoomRecommendation.setLayoutManager(linearLayout);

        firebaseFirestore.collection("boardingHouse").addSnapshotListener((value, error) -> {
            if (arrayListRoomSearch.size() == 0) {
                readData(list -> {
                    ArrayList<Room> arrayList = new ArrayList<>(list);
                    recyclerViewRoomRecommendation.setAdapter(adapter);
                    if (arrayList.size() > 0) {
                        arrayListRoomRecommendation.clear();
                        arrayListRoomRecommendation.addAll(arrayList);
                    }
                    adapter.notifyDataSetChanged();
                    tvNumberRoom.setText(list.size() + " phòng");
                });
            } else {
                readData(list -> {
                    ArrayList<Room> arrayList = new ArrayList<>(list);
                    if (arrayList.size() > 0) {
                        arrayListRoomRecommendation.clear();
                        arrayListRoomRecommendation.addAll(arrayList);
                    }
                    tvNumberRoom.setText(list.size() + " phòng");
                });
            }
        });

        firebaseFirestore.collectionGroup("roomType").addSnapshotListener((value, error) -> {
            if (arrayListRoomSearch.size() == 0) {
                readData(list -> {
                    ArrayList<Room> arrayList = new ArrayList<>(list);
                    recyclerViewRoomRecommendation.setAdapter(adapter);
                    if (arrayList.size() > 0) {
                        arrayListRoomRecommendation.clear();
                        arrayListRoomRecommendation.addAll(arrayList);
                    }
                    adapter.notifyDataSetChanged();
                    tvNumberRoom.setText(list.size() + " phòng");
                });
            } else {
                readData(list -> {
                    ArrayList<Room> arrayList = new ArrayList<>(list);
                    if (arrayList.size() > 0) {
                        arrayListRoomRecommendation.clear();
                        arrayListRoomRecommendation.addAll(arrayList);
                    }
                    tvNumberRoom.setText(list.size() + " phòng");
                });
            }
        });

        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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
                    tvNumberRoom.setText(arrayListRoomSearch.size() + " phòng");
                } else {
                    Toast.makeText(getContext(), "Không còn phòng trống", Toast.LENGTH_SHORT).show();
                }
                editTextSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                return true;
            }
            return false;
        });
        buttonRefresh.setOnClickListener(v -> {
            recyclerViewRoomRecommendation.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            editTextSearch.setText("");
            arrayListRoomSearch.clear();
            arrayListRoomSearchTemp.clear();
            spinnerDistance.setSelection(0);
            spinnerPrice.setSelection(0);
            spinnerNumberPeople.setSelection(0);
            spinnerFilter.setSelection(0);
            tvNumberRoom.setText(arrayListRoomRecommendation.size()+" phòng");
            pressButton = false;
            pressSpinner = false;
        });

        return view;
    }

    private void findView(View view) {

        tvNumberRoom = view.findViewById(R.id.numberRoom);
        editTextSearch = view.findViewById(R.id.h_editText_search);
        buttonRefresh = view.findViewById(R.id.h_button_refresh);

        spinnerPrice = view.findViewById(R.id.h_spinner_price);
        spinnerDistance = view.findViewById(R.id.h_spinner_distance);
        spinnerNumberPeople = view.findViewById(R.id.h_spinner_number_people);
        spinnerFilter = view.findViewById(R.id.h_spinner_filter);

        imageViewDistance = view.findViewById(R.id.h_imageView_spinner_distance);
        imageViewNumberPeople = view.findViewById(R.id.h_imageView_spinner_number_people);
        imageViewPrice = view.findViewById(R.id.h_imageView_spinner_price);
        ivFilter = view.findViewById(R.id.h_iv_spinner_filter);

        recyclerViewRoomRecommendation = view.findViewById(R.id.recyclerViewRoomRecommendation);
    }

    @SuppressLint("SetTextI18n")
    private void searchRoom(double price, double distance, double numberPeople) {
        arrayListRoomSearch.clear();
        recyclerViewRoomRecommendation.setAdapter(adapterSearch);
        if (pressButton) {
            ArrayList<Room> arrR = (ArrayList<Room>) arrayListRoomSearchTemp.clone();
            for (Room room : arrR) {
                if (room.getDistanceBoardingHouse() <= distance && room.getPriceRoomType() < price && room.getNumberPeopleRoomType() >= numberPeople) {
                    arrayListRoomSearch.add(room);
                }
            }
        } else {
            for (Room room : arrayListRoomRecommendation) {
                if (room.getDistanceBoardingHouse() <= distance && room.getPriceRoomType() < price && room.getNumberPeopleRoomType() >= numberPeople) {
                    arrayListRoomSearch.add(room);
                }
            }
        }
        adapterSearch.notifyDataSetChanged();
        tvNumberRoom.setText(arrayListRoomSearch.size() + " phòng");
        if (price != 99 | numberPeople != 0 | distance != 99) {
            pressSpinner = true;
        } else {
            pressSpinner = false;
        }

    }

    @SuppressLint({"LogNotTimber", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortRoom(double filterValue) {
//        arrayListRoomSearch.clear();
//        recyclerViewRoomRecommendation.setAdapter(adapterSearch);
        int value = (int) filterValue;
        Log.i("DatatypeNo", "press " + pressSpinner + " " + pressButton);
        if (!pressButton && !pressSpinner) {
            switch (value) {
                case 0:
                    Collections.sort(arrayListRoomRecommendation, Comparator.comparing(Room::getPriceRoomType));
                    Log.i("DatatypeNo", "key " + value);
                    break;
                case 1:
                    Collections.sort(arrayListRoomRecommendation, Comparator.comparing(Room::getDistanceBoardingHouse));
                    Log.i("DatatypeNo", "key " + value);
                    break;
                case 2:
                    Collections.sort(arrayListRoomRecommendation, Comparator.comparing(Room::getNumberPeopleRoomType));
                    Log.i("DatatypeNo", "key " + value);
                    break;
            }
            Log.i("Datatype2", arrayListRoomRecommendation.toArray().toString());
//            arrayListRoomSearch.addAll(arrayListRoomRecommendation);
            adapter.notifyDataSetChanged();
            tvNumberRoom.setText(arrayListRoomRecommendation.size() + " phòng");


        } else {
//            ArrayList<Room> arrR = (ArrayList<Room>) arrayListRoomSearchTemp.clone();

            switch (value) {
                case 0:
                    Collections.sort(arrayListRoomSearch, Comparator.comparing(Room::getPriceRoomType));
                    Log.i("DatatypePress", "key " + value);
                    break;
                case 1:
                    Collections.sort(arrayListRoomSearch, Comparator.comparing(Room::getDistanceBoardingHouse));
                    Log.i("DatatypePress", "key " + value);
                    break;
                case 2:
                    Collections.sort(arrayListRoomSearch, Comparator.comparing(Room::getNumberPeopleRoomType));
                    Log.i("DatatypePress", "key " + value);
                    break;
            }
            adapterSearch.notifyDataSetChanged();
            tvNumberRoom.setText(arrayListRoomSearch.size() + " phòng");
        }
//        adapterSearch.notifyDataSetChanged();

    }

    private void initialSpinner() {

        ArrayList<ObjectSpinner> listFilter = new ArrayList<>();
        listFilter.add(new ObjectSpinner(0, "Xếp theo giá"));
        listFilter.add(new ObjectSpinner(1, "Xếp theo khoảng cách"));
        listFilter.add(new ObjectSpinner(2, "Xếp theo số người ở"));

        ArrayAdapter<ObjectSpinner> adapterFilter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listFilter);
        adapterFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapterFilter);
        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerFilter.performClick();
            }
        });
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ObjectSpinner objectSpinner = (ObjectSpinner) parent.getItemAtPosition(position);
                filterValue = objectSpinner.key;

                sortRoom(filterValue);
                Log.i("Datatypekey", "key " + filterValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<ObjectSpinner> listNumberPeople = new ArrayList<>();
        listNumberPeople.add(new ObjectSpinner(0, "Số người ở"));
        listNumberPeople.add(new ObjectSpinner(1, "1 người ở"));
        listNumberPeople.add(new ObjectSpinner(2, "2 người ở"));
        listNumberPeople.add(new ObjectSpinner(3, "3 người ở"));
        listNumberPeople.add(new ObjectSpinner(4, "4 người ở"));
        listNumberPeople.add(new ObjectSpinner(5, "5 người ở"));
//        listNumberPeople.add(new ObjectSpinner(5, "Hơn 4 người ở"));

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
        listPrice.add(new ObjectSpinner(1.5, "Dưới 1.5 triệu"));
        listPrice.add(new ObjectSpinner(2, "Dưới 2 triệu"));
        listPrice.add(new ObjectSpinner(2.5, "Dưới 2.5 triệu"));
        listPrice.add(new ObjectSpinner(3, "Dưới 3 triệu"));
        listPrice.add(new ObjectSpinner(3.5, "Dưới 3.5 triệu"));
        listPrice.add(new ObjectSpinner(4, "Dưới 4 triệu"));
//        listPrice.add(new ObjectSpinner(99, "Trên 4 triệu"));
        ArrayAdapter<ObjectSpinner> adapterPriceSpinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listPrice);
        adapterPriceSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrice.setAdapter(adapterPriceSpinner);
        imageViewPrice.setOnClickListener(v -> spinnerPrice.performClick());
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
//        listDistance.add(new ObjectSpinner(99, "Ngoài 4 km"));

        ArrayAdapter<ObjectSpinner> adapterDistanceSpinner = new ArrayAdapter<ObjectSpinner>(getContext(), android.R.layout.simple_spinner_item, listDistance);
        adapterDistanceSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDistance.setAdapter(adapterDistanceSpinner);
        imageViewDistance.setOnClickListener(v -> spinnerDistance.performClick());
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

    static class ObjectSpinner {
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
        public @NotNull String toString() {
            return value;
        }
    }

    private interface FirestoreCallback {
        void onCallbackRoomType(List<Room> list);
    }

    @Override
    public void onStart() {
        super.onStart();
        pressSpinner = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("LogNotTimber")
    @SuppressWarnings("unchecked")
    private void readData(FirestoreCallback firestoreCallback) {
        List<Room> listBoardingHouse = new ArrayList<>();
        List<Room> listRoom = new ArrayList<>();

        firebaseFirestore.collection("boardingHouse").orderBy("distance", Query.Direction.ASCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                Room roomBoardingHouse = new Room();
                roomBoardingHouse.setIdBoardingHouse(documentSnapshot.getId());
                roomBoardingHouse.setNameBoardingHouse(documentSnapshot.getString("name"));
                roomBoardingHouse.setAddressBoardingHouse(documentSnapshot.getString("address"));
                roomBoardingHouse.setDescriptionBoardingHouse(documentSnapshot.getString("description"));
                roomBoardingHouse.setDistanceBoardingHouse(documentSnapshot.getDouble("distance"));
                roomBoardingHouse.setElectricityPriceBoardingHouse(documentSnapshot.getDouble("electricityPrice"));
                roomBoardingHouse.setIdOwnerBoardingHouse(documentSnapshot.getString("owner"));
                roomBoardingHouse.setLatitude(Objects.requireNonNull(documentSnapshot.getGeoPoint("point")).getLatitude());
                roomBoardingHouse.setLongitude(Objects.requireNonNull(documentSnapshot.getGeoPoint("point")).getLongitude());
                roomBoardingHouse.setWaterPriceBoardingHouse(documentSnapshot.getDouble("waterPrice"));
                double status = documentSnapshot.getDouble("status");
                if (status % 2 == 0) {
                    roomBoardingHouse.setStatusBoardingHouse(status);
                    listBoardingHouse.add(roomBoardingHouse);
                }
            }
            for (Room room : listBoardingHouse) {
                firebaseFirestore.collection("boardingHouse").document(room.getIdBoardingHouse()).collection("roomType").get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots1.getDocuments()) {
                        Room roomRoomType = new Room();
                        roomRoomType = room;

                        roomRoomType.setIdRoomType(documentSnapshot.getId());
                        roomRoomType.setNameRoomType(documentSnapshot.getString("name"));
                        roomRoomType.setNumberPeopleRoomType(Objects.requireNonNull(documentSnapshot.getDouble("numberPeople")).intValue());
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
                                    roomRoom.setElectricityPriceBoardingHouse(roomRoomType.getElectricityPriceBoardingHouse());
                                    roomRoom.setIdOwnerBoardingHouse(roomRoomType.getIdOwnerBoardingHouse());
                                    roomRoom.setLatitude(roomRoomType.getLatitude());
                                    roomRoom.setLongitude(roomRoomType.getLongitude());
                                    roomRoom.setWaterPriceBoardingHouse(roomRoomType.getWaterPriceBoardingHouse());

                                    roomRoom.setIdRoomType(roomRoomType.getIdRoomType());
                                    roomRoom.setNameRoomType(roomRoomType.getNameRoomType());
                                    roomRoom.setNumberPeopleRoomType(roomRoomType.getNumberPeopleRoomType());
                                    roomRoom.setPriceRoomType(roomRoomType.getPriceRoomType());
                                    roomRoom.setAreaRoomType(roomRoomType.getAreaRoomType());
                                    roomRoom.setDescriptionRoomType(roomRoomType.getDescriptionRoomType());

                                    Map<String, Object> mapField = (Map<String, Object>) field.getValue();
                                    for (Map.Entry<String, Object> roomField : mapField.entrySet()) {
                                        if (roomField.getKey().equals("image")) {
                                            ArrayList<String> arrayList = (ArrayList<String>) roomField.getValue();

                                            Log.i("MAPIAMGE", roomField.getValue().toString());
                                            //                                            Map<String, Object> stringImg = (Map<String, Object>) roomField.getValue();
//                                            for (Map.Entry<String, Object> im : stringImg.entrySet()) {
//                                                arrayList.add(im.getValue().toString());
//                                            }

                                            roomRoom.setImageRoom(arrayList);
                                        }
                                        if (roomField.getKey().equals("status")) {
                                            if (roomField.getValue().toString().equals("false")) {
                                                listRoom.add(roomRoom);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Collections.sort(listRoom, Comparator.comparing(Room::getPriceRoomType));
                    firestoreCallback.onCallbackRoomType(listRoom);
                });
            }
        });
    }
}

