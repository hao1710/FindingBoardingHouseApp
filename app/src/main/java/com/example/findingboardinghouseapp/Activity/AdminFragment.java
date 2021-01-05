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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.LandlordAdapter;
import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class AdminFragment extends Fragment {

    public AdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // code below
    public static final String MY_PREFERENCES = "MyPre";
    public SharedPreferences sharedPreferences;

    RecyclerView rvLandlord;
    ImageButton ibMenu;
    TextView tvLandlord, tvInn;

    public static int REQUEST_CODE_FROM_ADMIN_FRAGMENT = 32;

    @SuppressLint({"NonConstantResourceId", "LogNotTimber"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        findView(view);

        sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        Bundle bundle = getArguments();
        assert bundle != null;

        // recyclerView
        rvLandlord.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvLandlord.setLayoutManager(linearLayout);


        FirebaseFirestore.getInstance().collection("landlord").addSnapshotListener((value, error) -> {
            tvLandlord.setText(String.valueOf(value.size()));
            readData();
        });
        FirebaseFirestore.getInstance().collection("boardingHouse").addSnapshotListener((value, error) -> {
            tvInn.setText(String.valueOf(value.size()));
            readData();
        });

        ibMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), ibMenu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_in_admin_fragment, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.a_item_log_out) {
                    SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).edit();
                    editor.clear();
                    editor.apply();

                    FirebaseAuth.getInstance().signOut();

                    Fragment fragment = new LogInFragment();
                    FragmentTransaction fragmentTransaction = ((FragmentActivity) Objects.requireNonNull(getContext())).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.main_frameLayout, fragment);
                    fragmentTransaction.commit();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
        return view;
    }

    private void readData() {
        ArrayList<Landlord> landlordList = new ArrayList<>();
        LandlordAdapter landlordAdapter = new LandlordAdapter(getContext(), landlordList);
        rvLandlord.setAdapter(landlordAdapter);

        FirebaseFirestore.getInstance().collection("landlord").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            Landlord landlord = new Landlord();
                            landlord.setIdLandlord(documentSnapshot.getId());
                            landlord.setNameLandlord(documentSnapshot.getString("name"));
                            landlord.setEmailLandlord(documentSnapshot.getString("email"));

                            landlordList.add(landlord);
                        }
                        landlordAdapter.notifyDataSetChanged();
                    }
                });
    }

    @SuppressLint("LogNotTimber")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FROM_ADMIN_FRAGMENT && resultCode == InnManagementActivity.RESULT_CODE_FROM_INN_MANAGEMENT && data != null) {
            readData();
        }
    }

    private void findView(View view) {
        ibMenu = view.findViewById(R.id.admin_ib_menu);
        tvLandlord = view.findViewById(R.id.admin_tv_numberLandlord);
        tvInn = view.findViewById(R.id.admin_tv_numberInn);
        rvLandlord = view.findViewById(R.id.admin_rv);
    }
}