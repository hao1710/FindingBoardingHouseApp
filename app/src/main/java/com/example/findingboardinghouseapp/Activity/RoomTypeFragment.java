package com.example.findingboardinghouseapp.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.RoomTypeAdminAdapter;
import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.R;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_room_type, container, false);
        Bundle bundle = new Bundle();
        bundle = this.getArguments();
        assert bundle != null;
        roomTypeList = (ArrayList<RoomType>) bundle.getSerializable("roomType");

        landlord = (Landlord) bundle.getSerializable("landlord");
        // findView
        rvRoomType = view.findViewById(R.id.ad_rv_roomType);
        tvName = view.findViewById(R.id.tv_nameL);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhoneNumber = view.findViewById(R.id.tv_phoneNumber);
        tvName.setText("Tên: " + landlord.getNameLandlord());
        tvEmail.setText(landlord.getEmailLandlord());
        tvPhoneNumber.setText(landlord.getPhoneNumberLandlord());

        // recyclerView
        rvRoomType.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvRoomType.setLayoutManager(linearLayoutManager);

        adapter = new RoomTypeAdminAdapter(getContext(), roomTypeList);
        rvRoomType.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }
}