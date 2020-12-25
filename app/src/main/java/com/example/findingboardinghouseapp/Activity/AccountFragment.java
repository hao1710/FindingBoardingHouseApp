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

    //
    public static final int REQUEST_CODE_FROM_ACCOUNT_FRAGMENT = 34;
    public static final String MY_PREFERENCES = "MyPre";
    public SharedPreferences sharedPreferences;

    private ImageButton imageButtonMenu;
    private TextView textViewName, textViewAddress, textViewPhoneNumber, textViewEmail;
    private Landlord landlord;
    private RecyclerView rvBoardingHouse;
    private TextView tvEnableInn;


    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        findView(view);

        // sharedPreferences
        sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        //
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

        imageButtonMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), imageButtonMenu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_in_af, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.a_item_log_out:
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
                    case R.id.a_item_create_boarding_house:
                        BoardingHouse boardingHouse = new BoardingHouse();
                        boardingHouse.setIdOwnerBoardingHouse(landlord.getIdLandlord());
                        Bundle bundle2 = new Bundle();
                        bundle2.putSerializable("boardingHouse", boardingHouse);
                        Intent intent = new Intent(getContext(), CreateBoardingHouseActivity.class);
                        intent.putExtras(bundle2);
                        startActivityForResult(intent, REQUEST_CODE_FROM_ACCOUNT_FRAGMENT);
                        return true;
                    case R.id.a_item_setting_account:
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

        FirebaseFirestore.getInstance().collection("boardingHouse").addSnapshotListener((value, error) -> readDataBoardingHouse());
        return view;
    }


    private void findView(View view) {
        imageButtonMenu = view.findViewById(R.id.a_imageButton_menu);

        textViewName = view.findViewById(R.id.a_textView_name);
        textViewAddress = view.findViewById(R.id.a_textView_address);
        textViewPhoneNumber = view.findViewById(R.id.a_textView_phoneNumber);
        textViewEmail = view.findViewById(R.id.a_textView_email);
        //textViewPassword = view.findViewById(R.id.a_textView_password);

        tvEnableInn = view.findViewById(R.id.account_tv_enableInn);


        rvBoardingHouse = view.findViewById(R.id.recyclerViewBoardingHouse);
    }

    private void setTextDataLandlord(Landlord landlord) {
        textViewName.setText(landlord.getNameLandlord());
        textViewAddress.setText(landlord.getAddressLandlord());
        textViewPhoneNumber.setText(landlord.getPhoneNumberLandlord());
        textViewEmail.setText(landlord.getEmailLandlord());
//        textViewPassword.setText(landlord.getPasswordLandlord());
    }

    private void readDataBoardingHouse() {
        ArrayList<BoardingHouse> arrayList = new ArrayList();
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
                            boardingHouse.setStatusBoardingHouse(documentSnapshot.getBoolean("status"));

                            arrayList.add(boardingHouse);


                        }
                    }
                    adapter2.notifyDataSetChanged();
                    if (arrayList.size() > 0) {
                        tvEnableInn.setText("Khu trọ của bạn");
                       //tvEnableInn.setBackgroundColor(AccountFragment.this.getResources().getColor(R.color.colorTitle));
                    } else {
                        tvEnableInn.setText(null);
                       // tvEnableInn.setBackgroundColor(AccountFragment.this.getResources().getColor(R.color.white));
                    }
                    adapter.notifyDataSetChanged();
                });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FROM_ACCOUNT_FRAGMENT && resultCode == CreateBoardingHouseActivity.RESULT_CODE_FROM_CREATE_BOARDING_HOUSE) {
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