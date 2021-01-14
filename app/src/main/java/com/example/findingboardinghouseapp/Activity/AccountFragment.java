package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.BoardingHouseAdapter;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // code below
    public static final int REQUEST_CODE_FROM_ACCOUNT_FRAGMENT = 34;
    public static final String MY_PREFERENCES = "MyPre";
    public SharedPreferences sharedPreferences;

    Landlord landlord;

    ImageButton ibMenu, ibCreate;
    TextView tvName, tvAddress, tvPhoneNumber, tvEmail;
    TextView tvText;

    RecyclerView rvBoardingHouse;

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        findView(view);

        // sharedPreferences
        sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        Bundle bundle = getArguments();
        assert bundle != null;
        landlord = (Landlord) bundle.getSerializable("landlord");

        // recyclerView
        rvBoardingHouse.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvBoardingHouse.setLayoutManager(linearLayout);

        // do something
        setTextDataLandlord(landlord);

        readDataBoardingHouse();

        ibMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), ibMenu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_in_account_fragment, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.acc_log_out:
                        FirebaseAuth.getInstance().signOut();

                        SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).edit();
                        editor.clear();
                        editor.apply();

                        Fragment fragment = new LogInFragment();
                        FragmentTransaction fragmentTransaction = AccountFragment.this.getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.replace(R.id.main_frameLayout, fragment);
                        fragmentTransaction.commit();

                        return true;


                    case R.id.acc_setting_account:
                        Landlord landlordUpdate = landlord;

                        Bundle bundleUpdate = new Bundle();
                        bundleUpdate.putSerializable("landlordUpdate", landlordUpdate);

                        Intent intentUpdate = new Intent(getContext(), UpdateAccountActivity.class);
                        intentUpdate.putExtras(bundleUpdate);

                        startActivityForResult(intentUpdate, REQUEST_CODE_FROM_ACCOUNT_FRAGMENT);

                        return true;
                }
                return false;
            });
            popupMenu.show();
        });

        ibCreate.setOnClickListener(v -> {
            BoardingHouse boardingHouse = new BoardingHouse();
            boardingHouse.setIdOwnerBoardingHouse(landlord.getIdLandlord());

            Bundle bundleCreate = new Bundle();
            bundleCreate.putSerializable("boardingHouse", boardingHouse);

            Intent intentCreate = new Intent(getContext(), CreateBoardingHouseActivity.class);
            intentCreate.putExtras(bundleCreate);
            startActivityForResult(intentCreate, REQUEST_CODE_FROM_ACCOUNT_FRAGMENT);
        });

        FirebaseFirestore.getInstance().collection("boardingHouse")
                .addSnapshotListener((value, error) -> readDataBoardingHouse());

        return view;
    }


    private void findView(View view) {
        ibMenu = view.findViewById(R.id.acc_ib_menu);
        ibCreate = view.findViewById(R.id.acc_ib_create_inn);
        tvName = view.findViewById(R.id.acc_tv_name);
        tvAddress = view.findViewById(R.id.acc_tv_address);
        tvPhoneNumber = view.findViewById(R.id.acc_tv_phoneNumber);
        tvEmail = view.findViewById(R.id.acc_tv_email);

        tvText = view.findViewById(R.id.acc_tv_text);
        rvBoardingHouse = view.findViewById(R.id.acc_rv_boarding_house);
    }

    private void setTextDataLandlord(Landlord landlord) {
        tvName.setText(landlord.getNameLandlord());
        tvAddress.setText(landlord.getAddressLandlord());
        tvPhoneNumber.setText(landlord.getPhoneNumberLandlord());
        tvEmail.setText(landlord.getEmailLandlord());
    }

    @SuppressLint("SetTextI18n")
    private void readDataBoardingHouse() {
        ArrayList<BoardingHouse> arrayList;
        arrayList = new ArrayList<>();
        BoardingHouseAdapter adapter = new BoardingHouseAdapter(getContext(), arrayList);
        rvBoardingHouse.setAdapter(adapter);

        ArrayList<BoardingHouse> arrayList2 = new ArrayList<>();
        BoardingHouseAdapter adapter2 = new BoardingHouseAdapter(getContext(), arrayList2);

        FirebaseFirestore.getInstance().collection("boardingHouse").whereEqualTo("owner", landlord.getIdLandlord()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()).getDocuments()) {
                            BoardingHouse boardingHouse = new BoardingHouse();
                            boardingHouse.setIdBoardingHouse(documentSnapshot.getId());
                            boardingHouse.setNameBoardingHouse(documentSnapshot.getString("name"));
                            boardingHouse.setAddressBoardingHouse(documentSnapshot.getString("address"));
                            boardingHouse.setDistanceBoardingHouse(documentSnapshot.getDouble("distance"));
                            boardingHouse.setElectricityPriceBoardingHouse(documentSnapshot.getDouble("electricityPrice"));
                            boardingHouse.setWaterPriceBoardingHouse(documentSnapshot.getDouble("waterPrice"));
                            boardingHouse.setDescriptionBoardingHouse(documentSnapshot.getString("description"));
                            boardingHouse.setStatusBoardingHouse(documentSnapshot.getDouble("status"));

                            arrayList.add(boardingHouse);
                        }
                    }
                    adapter2.notifyDataSetChanged();
                    if (arrayList.size() > 0) {
                        tvText.setText("Nhà trọ của bạn");
                    } else {
                        tvText.setText("Hãy thêm nhà trọ của bạn");
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FROM_ACCOUNT_FRAGMENT && resultCode == CreateBoardingHouseActivity.RESULT_CODE_FROM_CREATE_BOARDING_HOUSE && data != null) {
            readDataBoardingHouse();
        }
        if (requestCode == REQUEST_CODE_FROM_ACCOUNT_FRAGMENT && resultCode == UpdateAccountActivity.RESULT_CODE_FROM_UPDATE_ACCOUNT && data != null) {
            landlord.setIdLandlord(sharedPreferences.getString("id", null));
            landlord.setNameLandlord(sharedPreferences.getString("name", null));
            landlord.setAddressLandlord(sharedPreferences.getString("address", null));
            landlord.setPhoneNumberLandlord(sharedPreferences.getString("phoneNumber", null));
            landlord.setEmailLandlord(sharedPreferences.getString("email", null));
            landlord.setPasswordLandlord(sharedPreferences.getString("password", null));
            setTextDataLandlord(landlord);
        }
    }
}