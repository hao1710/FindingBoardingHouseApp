package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.findingboardinghouseapp.Model.Admin;
import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
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

    // code under
    private ImageButton ibMenu;
    private TextView tvLandlord, tvInn;

    public static final String MY_PREFERENCES = "MyPre";
    public SharedPreferences sharedPreferences;
    private Admin admin;
    private RecyclerView rvLandlord;
    private LandlordAdapter adapter;

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        findView(view);
        FirebaseFirestore.getInstance().collection("landlord").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                tvLandlord.setText(String.valueOf(task.getResult().size()));
            }
        });
        FirebaseFirestore.getInstance().collection("boardingHouse").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                tvInn.setText(String.valueOf(task.getResult().size()));
            }
        });

        sharedPreferences = this.getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);


        Bundle bundle = getArguments();
        assert bundle != null;
        admin = (Admin) bundle.getSerializable("admin");

        // setText


        // recyclerView
        rvLandlord.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvLandlord.setLayoutManager(linearLayout);
        rvLandlord.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("landlord").addSnapshotListener((value, error) -> {
            for (DocumentChange dc : value.getDocumentChanges()) {
                switch (dc.getType()) {
                    case ADDED:
                    case REMOVED:
                    case MODIFIED:
                        readDataLandlord();
                        break;
                }
            }
        });

        ibMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), ibMenu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_in_admin_fragment, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.a_item_log_out:
                        SharedPreferences.Editor editor = this.getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).edit();
                        editor.clear();
                        editor.apply();

                        FirebaseAuth.getInstance().signOut();

                        Fragment fragment = new LogInFragment();
                        FragmentTransaction fragmentTransaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
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

    @SuppressLint("LogNotTimber")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.i("DATATYPE", requestCode + " - " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5 && resultCode == InnManagementActivity.RESULT_CODE_FROM_INN_MANAGEMENT && data != null) {
            readDataLandlord();
            Log.i("DATATYPE", "no");
        }
    }

    @SuppressLint("LogNotTimber")
    private void readDataLandlord() {
        ArrayList<Landlord> landlordArrayList = new ArrayList<>();
        LandlordAdapter newAdapter = new LandlordAdapter(getContext(), landlordArrayList);
        rvLandlord.setAdapter(newAdapter);
        Log.i("DATATYPE", "yes");
        FirebaseFirestore.getInstance().collection("landlord").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                            arrayList.clear();
                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()).getDocuments()) {
                            Landlord landlord = new Landlord();
                            landlord.setIdLandlord(documentSnapshot.getId());
                            landlord.setNameLandlord(documentSnapshot.getString("name"));
                            landlord.setEmailLandlord(documentSnapshot.getString("email"));

                            landlordArrayList.add(landlord);
                        }

                    }

                    newAdapter.notifyDataSetChanged();
                });
    }

    private void findView(View view) {
        ibMenu = view.findViewById(R.id.admin_ib_menu);
        tvLandlord = view.findViewById(R.id.admin_tv_numberLandlord);
        tvInn = view.findViewById(R.id.admin_tv_numberInn);
        rvLandlord = view.findViewById(R.id.admin_rv);
    }
}