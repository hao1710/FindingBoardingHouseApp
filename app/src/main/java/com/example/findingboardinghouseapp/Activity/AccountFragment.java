package com.example.findingboardinghouseapp.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findingboardinghouseapp.Adapter.BoardingHouseAdapter;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.Model.RoomCRUD;
import com.example.findingboardinghouseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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
    private BoardingHouseAdapter adapter;
    private ArrayList<BoardingHouse> arrayList;
    private Landlord landlord;
    public static final int REQUEST_CODE_SETTING_ACCOUNT = 1;
    public static final int REQUEST_CODE_CREATE_BOARDING_HOUSE = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        Bundle bundle = getArguments();
        assert bundle != null;
        landlord = (Landlord) bundle.getSerializable("landlord");

        // initial
        arrayList = new ArrayList<>();
        adapter = new BoardingHouseAdapter(getContext(), arrayList);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        // mapping
        TextView textViewNameLandlord = view.findViewById(R.id.a_name_landlord);
        textViewNameLandlord.setText(landlord.getNameLandlord());
        RecyclerView recyclerViewBoardingHouse = view.findViewById(R.id.recyclerViewBoardingHouse);
        Button buttonSetting = view.findViewById(R.id.a_btn_setting_account);
        Button buttonCreateBoardingHouse = view.findViewById(R.id.a_button_create_bdh);

        // recyclerView
        recyclerViewBoardingHouse.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewBoardingHouse.setLayoutManager(linearLayout);
        recyclerViewBoardingHouse.setAdapter(adapter);

        // do something
        firebaseFirestore.collection("boardingHouse").whereEqualTo("owner", landlord.getIdLandlord()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()).getDocuments()) {
                            BoardingHouse boardingHouse = new BoardingHouse();
                            boardingHouse.setNameBoardingHouse(documentSnapshot.getString("name"));
                            boardingHouse.setAddressBoardingHouse(documentSnapshot.getString("address"));
                            boardingHouse.setDistanceBoardingHouse(documentSnapshot.getDouble("distance"));
                            boardingHouse.setIdBoardingHouse(documentSnapshot.getId());
                            arrayList.add(boardingHouse);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });

        buttonSetting.setOnClickListener(v -> {
            Bundle bundle1 = new Bundle();
            bundle1.putSerializable("landlord", landlord);

            Intent intent = new Intent(getContext(), AccountSettingActivity.class);
            intent.putExtras(bundle1);
            startActivityForResult(intent, REQUEST_CODE_SETTING_ACCOUNT);
        });

        buttonCreateBoardingHouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoardingHouse boardingHouse = new BoardingHouse();
                boardingHouse.setIdOwnerBoardingHouse(landlord.getIdLandlord());
                Bundle bundle2 = new Bundle();
                bundle2.putSerializable("boardingHouse", boardingHouse);

                Intent intent = new Intent(getContext(), CreateBoardingHouseActivity.class);
                intent.putExtras(bundle2);
                startActivityForResult(intent, REQUEST_CODE_CREATE_BOARDING_HOUSE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_BOARDING_HOUSE) {
            if (resultCode == CreateBoardingHouseActivity.RESULT_CODE_CREATE_BOARDING_HOUSE) {
                Toast.makeText(getContext(), "Create", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == REQUEST_CODE_SETTING_ACCOUNT) {
            if (resultCode == AccountSettingActivity.RESULT_CODE_SETTING_ACCOUNT) {
                Toast.makeText(getContext(), "Setting", Toast.LENGTH_SHORT).show();
            }
        }
    }
}