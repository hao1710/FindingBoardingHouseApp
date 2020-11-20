package com.example.findingboardinghouseapp.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.findingboardinghouseapp.Adapter.RoomRecommendationAdapter;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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

    //code under

    private RoomRecommendationAdapter adapter;
    private ArrayList<Room> arrayListTemp, arrayListRoom, arrayListRoomSearch, arrayListRoomSearchTemp;
    private FirebaseFirestore firebaseFirestore;

    private RecyclerView recyclerViewRoomSearch;
    private Button buttonSearch, buttonRefresh;
    private EditText editTextSearch;
    private Spinner spinnerNumberPeople, spinnerPrice, spinnerDistance;

    private String numberPeople = "0", distance = "4", price = "4";
    private Boolean pressButton = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // initial
        firebaseFirestore = FirebaseFirestore.getInstance();
        arrayListTemp = new ArrayList<>();
        arrayListRoom = new ArrayList<>();
        arrayListRoomSearch = new ArrayList<>();
        arrayListRoomSearchTemp = new ArrayList<>();
        adapter = new RoomRecommendationAdapter(getContext(), arrayListRoomSearch);

        // mapping
        recyclerViewRoomSearch = view.findViewById(R.id.s_recyclerview_room);
        editTextSearch = view.findViewById(R.id.s_editText_search);
        spinnerNumberPeople = view.findViewById(R.id.s_number_people_spinner);
        spinnerDistance = view.findViewById(R.id.s_distance_spinner);
        spinnerPrice = view.findViewById(R.id.s_price_spinner);
        buttonSearch = view.findViewById(R.id.s_btn_search);
        buttonRefresh = view.findViewById(R.id.s_btn_refresh);

        // recyclerView
        recyclerViewRoomSearch.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewRoomSearch.setLayoutManager(gridLayoutManager);
        recyclerViewRoomSearch.setAdapter(adapter);


        //
        editTextSearch.setText("");

        //
        readData(new FirestoreCallback() {
            @Override
            public void onCallbackBoardingHouse(List<Room> list) {

            }

            @Override
            public void onCallbackRoomType(List<Room> list) {
                arrayListTemp.clear();
                for (int i = 0; i < list.size(); i++) {
                    arrayListTemp.add(list.get(i));
                }
                if (arrayListTemp.size() > 0) {
                    arrayListRoom.clear();
                    for (Room room : arrayListTemp) {
                        arrayListRoom.add(room);
                    }
                }
            }
        });

        // action
        allSpinner();

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayListRoomSearch.clear();
                arrayListRoomSearchTemp.clear();
                String input = editTextSearch.getText().toString().toLowerCase();
                if (arrayListRoom.size() > 0) {
                    for (Room room : arrayListRoom) {
                        if (room.getNameBoardingHouse().toLowerCase().contains(input) || room.getAddressBoardingHouse().toLowerCase().contains(input)) {
                            arrayListRoomSearch.add(room);
                        }
                    }

                } else {
                    Toast.makeText(getContext(), "Không tìm thấy nhà trọ phù hợp!", Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
                pressButton = true;
                arrayListRoomSearchTemp = (ArrayList<Room>) arrayListRoomSearch.clone();
            }
        });
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("refresh", "1");
                editTextSearch.setText("");
                Log.i("refresh", "2");
                spinnerNumberPeople.setSelection(0);
                Log.i("refresh", "3");
                spinnerPrice.setSelection(0);
                Log.i("refresh", "4");
                spinnerDistance.setSelection(0);
                Log.i("refresh", "5");
                pressButton = false;
                Log.i("refresh", "6");
                arrayListRoomSearch.clear();
                adapter.notifyDataSetChanged();
                Log.i("refresh", "7");
            }
        });

        return view;
    }


    private void searchRoom(String price, String distance, String numberPeople) {
        if (pressButton == true) {
            ArrayList<Room> arrR = (ArrayList<Room>) arrayListRoomSearchTemp.clone();
            arrayListRoomSearch.clear();
            for (Room room : arrR) {
                if (room.getDistanceBoardingHouse() <= Double.parseDouble(distance) && room.getPriceRoomType() <= Double.parseDouble(price) && room.getNumberPeopleRoomType() >= Integer.parseInt(numberPeople)) {
                    arrayListRoomSearch.add(room);
                }
            }
        } else {

            arrayListRoomSearch.clear();
            for (Room room : arrayListRoom) {
                if (room.getDistanceBoardingHouse() <= Double.parseDouble(distance) && room.getPriceRoomType() <= Double.parseDouble(price) && room.getNumberPeopleRoomType() >= Integer.parseInt(numberPeople)) {
                    arrayListRoomSearch.add(room);
                }
            }


        }
        adapter.notifyDataSetChanged();


    }

    private void allSpinner() {

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
                if (arrayListRoom.size() > 0) {
                    if (parent.getItemAtPosition(position).toString().equals("Số người ở")) {
                        numberPeople = "0";
                    } else {
                        numberPeople = parent.getItemAtPosition(position).toString();
                    }
                    searchRoom(price, distance, numberPeople);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        List<String> listPrice = new ArrayList<>();
        listPrice.add("Giá (triệu)");
        listPrice.add("1");
        listPrice.add("2");
        listPrice.add("3");
        listPrice.add("4");

        ArrayAdapter<String> adapterPriceSpinner = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, listPrice);
        adapterPriceSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrice.setAdapter(adapterPriceSpinner);

        spinnerPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (arrayListRoom.size() > 0) {
                    if (parent.getItemAtPosition(position).toString().equals("Giá (triệu)")) {

                        price = "4";


                    } else {
                        price = parent.getItemAtPosition(position).toString();
                    }
                    searchRoom(price, distance, numberPeople);
                }

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

                if (arrayListRoom.size() > 0) {

                    if (parent.getItemAtPosition(position).toString().equals("Khoảng cách (km)")) {

                        distance = "4";
                    } else {
                        distance = parent.getItemAtPosition(position).toString();
                    }
                    searchRoom(price, distance, numberPeople);


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                Log.i("TAG", "1");
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Room roomBoardingHouse = new Room();
                    roomBoardingHouse.setIdBoardingHouse(documentSnapshot.getId());
                    roomBoardingHouse.setNameBoardingHouse(documentSnapshot.getString("name"));
                    roomBoardingHouse.setAddressBoardingHouse(documentSnapshot.getString("address"));
                    roomBoardingHouse.setDescriptionBoardingHouse(documentSnapshot.getString("description"));
                    roomBoardingHouse.setDistanceBoardingHouse(documentSnapshot.getDouble("distance"));
                    roomBoardingHouse.setIdOwnerBoardingHouse(documentSnapshot.getString("owner"));
                    listBoardingHouse.add(roomBoardingHouse);

                    Log.i("TAG", "boardingHouse " + roomBoardingHouse.getNameBoardingHouse() + " " + roomBoardingHouse.getAddressBoardingHouse());
                }
                firestoreCallback.onCallbackBoardingHouse(listBoardingHouse);
                for (Room room : listBoardingHouse) {
                    Log.i("TAG", "2");
                    firebaseFirestore.collection("boardingHouse").document(room.getIdBoardingHouse()).collection("roomType").get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                        Log.i("MAP", "----------------");
                                        Room roomRoomType = new Room();
                                        roomRoomType.setIdBoardingHouse(room.getIdBoardingHouse());
                                        roomRoomType.setNameBoardingHouse(room.getNameBoardingHouse());
                                        roomRoomType.setAddressBoardingHouse(room.getAddressBoardingHouse());
                                        roomRoomType.setDescriptionBoardingHouse(room.getDescriptionBoardingHouse());
                                        roomRoomType.setDistanceBoardingHouse(room.getDistanceBoardingHouse());
                                        roomRoomType.setIdOwnerBoardingHouse(room.getIdOwnerBoardingHouse());

                                        roomRoomType.setIdRoomType(documentSnapshot.getId());
                                        roomRoomType.setNameRoomType(documentSnapshot.getString("name"));
                                        roomRoomType.setNumberPeopleRoomType(documentSnapshot.getDouble("numberPeople").intValue());
                                        roomRoomType.setPriceRoomType(documentSnapshot.getDouble("price"));
                                        roomRoomType.setAreaRoomType(documentSnapshot.getDouble("area"));
                                        Room roomTest = new Room();
                                        roomTest = room;
                                        Log.i("alo", roomTest.getAddressBoardingHouse());
                                        Map<String, Object> map = documentSnapshot.getData();
                                        Log.i("MAP", "----------------");
                                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                                            if (entry.getKey().equals("room")) {
                                                Map<String, Object> mapRoom = (Map<String, Object>) entry.getValue();
                                                for (Map.Entry<String, Object> field : mapRoom.entrySet()) {
                                                    Log.i("MAP", field.getKey().toString() + ": " + field.getValue().toString());
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

                                                        Log.i("MAP", image.getKey().toString() + ": " + image.getValue().toString());
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


                                        Log.i("TAG", "roomType  " + roomRoomType.getIdBoardingHouse() + " " + room.getAddressBoardingHouse() + " " + " " + roomRoomType.getIdRoomType());
                                    }
                                    firestoreCallback.onCallbackRoomType(listRoomType);
                                }
                            });
                }

            }

        });

    }
}