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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RoomTypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomTypeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RoomTypeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoomTypeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoomTypeFragment newInstance(String param1, String param2) {
        RoomTypeFragment fragment = new RoomTypeFragment();
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

    //
    ArrayList<RoomType> arrayList;
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
        arrayList = (ArrayList<RoomType>) bundle.getSerializable("roomType");

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

        adapter = new RoomTypeAdminAdapter(getContext(), arrayList);
        rvRoomType.setAdapter(adapter);
        return view;
    }

}