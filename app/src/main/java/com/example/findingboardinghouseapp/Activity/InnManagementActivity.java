package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.findingboardinghouseapp.Adapter.TabLayoutAdapter;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class InnManagementActivity extends AppCompatActivity {
    public static final int RESULT_CODE_FROM_INN_MANAGEMENT = 31;

    private BoardingHouse boardingHouse;
    TextView tvName;
    static ImageButton ibBack;
    TabLayout tabLayout;
    ViewPager viewPager;

    InnFragment innFragment;
    RoomTypeFragment roomTypeFragment;
    Landlord landlordZ;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inn_management);

        Intent intent = getIntent();
        boardingHouse = (BoardingHouse) intent.getSerializableExtra("boardingHouse");

        ibBack = findViewById(R.id.ib_back);
        tvName = findViewById(R.id.inn_tv_name);
        tabLayout = findViewById(R.id.inn_tabLayout);
        viewPager = findViewById(R.id.inn_viewPager);

        tvName.setMaxLines(1);
        tvName.setEllipsize(TextUtils.TruncateAt.END);
        tvName.setText(boardingHouse.getNameBoardingHouse());

        innFragment = new InnFragment();
        roomTypeFragment = new RoomTypeFragment();

        landlordZ = new Landlord();
        ibBack.setOnClickListener(v -> {
            Intent intentResult = new Intent();
            setResult(RESULT_CODE_FROM_INN_MANAGEMENT, intentResult);
            finish();
        });

        getInfo(landlord -> {
            landlordZ = landlord;
            setupView(viewPager);
        });

        FirebaseFirestore.getInstance().collection("boardingHouse")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            DocumentSnapshot documentSnapshot = documentChange.getDocument();
                            if (documentSnapshot.getId().equals(boardingHouse.getIdBoardingHouse())) {
                                if (documentChange.getType() == DocumentChange.Type.REMOVED) {
                                    Toast.makeText(getApplicationContext(), "Nhà trọ đã bị xóa", Toast.LENGTH_SHORT).show();
                                    Intent intentResult = new Intent();
                                    setResult(RESULT_CODE_FROM_INN_MANAGEMENT, intentResult);
                                    finish();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intentResult = new Intent();
        setResult(RESULT_CODE_FROM_INN_MANAGEMENT, intentResult);
        finish();
        super.onBackPressed();
    }


    private void setupView(ViewPager viewPager) {
        TabLayoutAdapter tabLayoutAdapter = new TabLayoutAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putSerializable("inn", boardingHouse);
        bundle.putSerializable("landlord", landlordZ);

        innFragment.setArguments(bundle);
        roomTypeFragment.setArguments(bundle);

        tabLayoutAdapter.addFrag(innFragment, "Thông tin");
        tabLayoutAdapter.addFrag(roomTypeFragment, "Loại phòng");

        viewPager.setAdapter(tabLayoutAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    protected interface CallBack {
        void onCallbackL(Landlord landlord);
    }

    private void getInfo(CallBack callBack) {
        FirebaseFirestore.getInstance().collection("landlord")
                .document(boardingHouse.getIdOwnerBoardingHouse())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        assert documentSnapshot != null;
                        landlordZ.setIdLandlord(documentSnapshot.getId());
                        landlordZ.setNameLandlord(documentSnapshot.getString("name"));
                        landlordZ.setEmailLandlord(documentSnapshot.getString("email"));
                        landlordZ.setPhoneNumberLandlord(documentSnapshot.getString("phoneNumber"));
                    }
                    callBack.onCallbackL(landlordZ);
                });
    }


    @SuppressLint("LogNotTimber")
    @Override
    protected void onStart() {
        Log.i("LifeCycleINNM", "onStart");
        super.onStart();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onResume() {
        Log.i("LifeCycleINNM", "onResume");
        super.onResume();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onPause() {
        Log.i("LifeCycleINNM", "onPause");
        super.onPause();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onStop() {
        Log.i("LifeCycleINNM", "onStop");
        super.onStop();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onRestart() {
        Log.i("LifeCycleINNM", "onRestart");
        super.onRestart();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onDestroy() {
        Log.i("LifeCycleINNM", "onDestroy");
        super.onDestroy();
    }
}