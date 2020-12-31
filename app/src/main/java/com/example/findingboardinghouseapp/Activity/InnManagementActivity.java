package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.findingboardinghouseapp.Adapter.CommentAdapter;
import com.example.findingboardinghouseapp.Adapter.TabLayoutAdapter;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.Comment;
import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class InnManagementActivity extends AppCompatActivity {
    public static final int RESULT_CODE_FROM_INN_MANAGEMENT = 31;

    private BoardingHouse boardingHouse;
    TextView tvName;
    ImageButton ibBack;
    TabLayout tabLayout;
    ViewPager viewPager;

    private FirebaseFirestore firebaseFirestore;
    private CommentAdapter adapter;
    private ArrayList<Comment> arrayList;
    private ArrayList<RoomType> roomTypeArrayList;

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

        arrayList = new ArrayList<>();
        roomTypeArrayList = new ArrayList<>();

        tvName.setText(boardingHouse.getNameBoardingHouse());

        firebaseFirestore = FirebaseFirestore.getInstance();

        //
        innFragment = new InnFragment();
        roomTypeFragment = new RoomTypeFragment();

        // underline textView
//        String create = "Xem map";
//        SpannableString spannableString = new SpannableString(create);
//        spannableString.setSpan(new UnderlineSpan(), 0, create.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tvViewMap.setText(spannableString);
//        if (aSwitch.isChecked()) {
//            button.setText("Vô hiệu hóa");
//        } else {
//            button.setText("Xác nhận");
//        }
//        button.setOnClickListener(v -> {
//            boolean status = aSwitch.isChecked();
//            AlertDialog.Builder builder = new AlertDialog.Builder(InnManagementActivity.this);
//            if (status) {
//                builder.setMessage("Bạn muốn vô hiệu hóa nhà trọ này?")
//                        .setPositiveButton("Thực hiện", (dialog, id) -> {
//                            // FIRE ZE MISSILES!
//
//                            FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
//                                    .update("status", false);
//                            aSwitch.setChecked(false);
//                            button.setText("Xác nhận");
//                            Toast.makeText(InnManagementActivity.this, "Vô hiệu hóa thành công", Toast.LENGTH_SHORT).show();
//                        })
//                        .setNegativeButton("Hủy", (dialog, id) -> {
//                            // User cancelled the dialog
//                            dialog.dismiss();
//                        });
//                // Create the AlertDialog object and return it
//            } else {
//                builder.setMessage("Bạn muốn xác nhận nhà trọ này?")
//                        .setPositiveButton("Thực hiện", (dialog, id) -> {
//                            // FIRE ZE MISSILES!
//
//                            FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
//                                    .update("status", true);
//                            aSwitch.setChecked(true);
//                            button.setText("Vô hiệu hóa");
//                            Toast.makeText(InnManagementActivity.this, "Xác nhận thành công", Toast.LENGTH_SHORT).show();
//                        })
//                        .setNegativeButton("Hủy", (dialog, id) -> {
//                            // User cancelled the dialog
//                            dialog.dismiss();
//                        });
//                // Create the AlertDialog object and return it
//            }
//            builder.create();
//            builder.show();
//        });

        landlordZ = new Landlord();
        ibBack.setOnClickListener(v -> {
            Intent intentResult = new Intent();
            setResult(RESULT_CODE_FROM_INN_MANAGEMENT, intentResult);
            finish();
        });

//        adapter = new CommentAdapter(getApplicationContext(), arrayList);
//
//        rvComment.setHasFixedSize(true);
//        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
//        rvComment.setLayoutManager(linearLayout);
//        rvComment.setAdapter(adapter);
//

        getInfo(new CallBack() {
            @Override
            public void onCallbackL(Landlord landlord) {
                landlordZ = landlord;
            }

            @Override
            public void onCB(List<RoomType> list) {
                roomTypeArrayList.clear();
                roomTypeArrayList.addAll(list);
                setupView(viewPager);
            }
        });

        //setupView(viewPager);
//        getComment(list -> {
//            if (list.size() > 0) {
//                arrayList.clear();
//                arrayList.addAll(list);
//            }

//        });

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
        bundle.putSerializable("roomType", roomTypeArrayList);

        innFragment.setArguments(bundle);
        roomTypeFragment.setArguments(bundle);
        tabLayoutAdapter.addFrag(innFragment, "Thông tin");
        tabLayoutAdapter.addFrag(roomTypeFragment, "Loại phòng");
        viewPager.setAdapter(tabLayoutAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }


    protected interface CallBack {
        void onCallbackL(Landlord landlord);

        void onCB(List<RoomType> list);
    }

    private void getInfo(CallBack callBack) {
        List<RoomType> listRoomType = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("landlord").document(boardingHouse.getIdOwnerBoardingHouse()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
                    landlordZ.setNameLandlord(documentSnapshot.getString("name"));
                    landlordZ.setEmailLandlord(documentSnapshot.getString("email"));
                    landlordZ.setPhoneNumberLandlord(documentSnapshot.getString("phoneNumber"));
                }
                callBack.onCallbackL(landlordZ);
                FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
                        .collection("roomType").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                RoomType roomType = new RoomType();
                                roomType.setIdRoomType(documentSnapshot.getId());
                                roomType.setIdBoardingHouse(boardingHouse.getIdBoardingHouse());
                                roomType.setNameRoomType(documentSnapshot.getString("name"));
                                roomType.setAreaRoomType(documentSnapshot.getDouble("area"));
                                roomType.setPriceRoomType(documentSnapshot.getDouble("price"));
                                roomType.setNumberPeopleRoomType(documentSnapshot.getDouble("numberPeople").intValue());
                                listRoomType.add(roomType);
                            }
                        }
                        callBack.onCB(listRoomType);
                    }
                });
            }
        });
    }
}