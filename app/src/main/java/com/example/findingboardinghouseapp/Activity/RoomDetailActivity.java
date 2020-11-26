package com.example.findingboardinghouseapp.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.CommentAdapter;
import com.example.findingboardinghouseapp.Adapter.FacilityAdapter;
import com.example.findingboardinghouseapp.Model.Comment;
import com.example.findingboardinghouseapp.Model.CommentCRUD;
import com.example.findingboardinghouseapp.Model.Facility;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomDetailActivity extends AppCompatActivity {
    private TextView textViewDescriptionRoom, textViewNameBoardingHouse, textViewAddressBoardingHouse, textViewNumberPeople, textViewDescriptionBoardingHouse, textViewPrice, textViewArea;
    protected ImageView imageView;
    protected RecyclerView recyclerViewComment, recyclerViewFacility;
    protected CommentAdapter adapter;
    protected ArrayList<Comment> arrayList;
    protected FirebaseFirestore firebaseFirestore;
    protected Room room;
    public ArrayList<Facility> arrayListFacility;
    public FacilityAdapter facilityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        mapping();

        // underline textView
        TextView textViewViewMap = findViewById(R.id.rd_textView_view_map);
        String create = "Xem map";
        SpannableString spannableString = new SpannableString(create);
        spannableString.setSpan(new UnderlineSpan(), 0, create.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewViewMap.setText(spannableString);

        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra("room");
        textViewDescriptionRoom.setText(room.getDescriptionRoomType());
        textViewNameBoardingHouse.setText(room.getNameBoardingHouse());
        textViewAddressBoardingHouse.setText("Địa chỉ: " + room.getAddressBoardingHouse());
        textViewPrice.setText(room.getPriceRoomType() + " triệu đồng");
        ImageButton imageButtonCreateComment = findViewById(R.id.rd_imageButton_create_comment);
        textViewArea.setText(room.getAreaRoomType() + " m2");
        textViewNumberPeople.setText(room.getNumberPeopleRoomType() + " người");
        textViewDescriptionBoardingHouse.setText(room.getDescriptionBoardingHouse());
        ExpandableLinearLayout expandableLinearLayout = findViewById(R.id.rd_expand_layout_comment);
        Button buttonCancel = findViewById(R.id.rd_button_cancel_comment);
        EditText editTextName = findViewById(R.id.rd_editText_name);
        EditText editTextContent = findViewById(R.id.rd_editText_content);
        textViewViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(v.getContext(), MapActivity.class);
                intent1.putExtra("name", room.getNameBoardingHouse());
                intent1.putExtra("latitude", room.getLatitude());
                intent1.putExtra("longitude", room.getLongitude());
                startActivity(intent1);
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextName.setText("");
                editTextContent.setText("");
                expandableLinearLayout.collapse();
            }
        });


        imageButtonCreateComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                expandableLinearLayout.expand();
            }
        });

        Button buttonCreateComment = findViewById(R.id.rd_button_create_comment);
        buttonCreateComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String content = editTextContent.getText().toString().trim();
                if (name.equals("") || content.equals("")) {
                    Toast.makeText(v.getContext(), "Vui lòng đủ tên và nội dung", Toast.LENGTH_SHORT).show();
                } else {
                    CommentCRUD commentCRUD = new CommentCRUD();
                    commentCRUD.setBoardingHouse(room.getIdBoardingHouse());
                    commentCRUD.setContent(content);
                    commentCRUD.setName(name);
                    FirebaseFirestore.getInstance().collection("comment").add(commentCRUD);

                    editTextName.setText("");
                    editTextContent.setText("");
                    expandableLinearLayout.collapse();
                    getComment(new FirestoreCallBack() {
                        @Override
                        public void onCallback(List<Comment> list) {
                            if (list.size() > 0) {
                                arrayList.clear();
                                for (Comment comment : list) {
                                    arrayList.add(comment);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });


//        new StfalconImageViewer.Builder<>(getApplicationContext(), room.getImageRoom(), new ImageLoader<String>() {
//            @Override
//            public void loadImage(ImageView imageView, String image) {
//                Picasso.with(getApplicationContext()).load(room.getImageRoom())
//                        .fit().centerCrop()
//                        .placeholder(R.drawable.load_image_room)
//                        .error(R.drawable.ic_app)
//                        .into(imageView);
//            }
//        }).show();
        Picasso.with(getApplicationContext()).load(room.getImageRoom())
                .fit().centerCrop()
                .placeholder(R.drawable.load_image_room)
                .error(R.drawable.ic_app)
                .into(imageView);
//    new StfalconImageViewer.Builder<String>(this, Collections.singletonList(room.getImageRoom()), new ImageLoader<String>() {
//        @Override
//        public void loadImage(ImageView imageView, String image) {
//            Picasso.with(getApplicationContext()).load(room.getImageRoom())
//                    .placeholder(R.drawable.load_image_room)
//                    .error(R.drawable.ic_app)
//                    .into(imageView);
//        }
//    }).show();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new StfalconImageViewer.Builder<String>(v.getContext(), Collections.singletonList(room.getImageRoom()), new ImageLoader<String>() {
                    @Override
                    public void loadImage(ImageView imageView, String image) {
                        Picasso.with(getApplicationContext()).load(room.getImageRoom())
                                .placeholder(R.drawable.load_image_room)
                                .error(R.drawable.ic_app)
                                .into(imageView);
                    }
                }).withBackgroundColor(Color.WHITE).show();
            }
        });
        arrayList = new ArrayList<>();
        adapter = new CommentAdapter(getApplicationContext(), arrayList);
        recyclerViewComment = findViewById(R.id.recyclerViewComment);
        recyclerViewComment.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewComment.setLayoutManager(linearLayout);
        recyclerViewComment.setAdapter(adapter);

        arrayListFacility = new ArrayList<>();
        facilityAdapter = new FacilityAdapter(getApplicationContext(), arrayListFacility);
        recyclerViewFacility = findViewById(R.id.rd_recyclerView_facility);
        recyclerViewFacility.setHasFixedSize(true);
        LinearLayoutManager layoutFacility = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 5);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewFacility.setLayoutManager(gridLayoutManager);
        recyclerViewFacility.setAdapter(facilityAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        getFacility(new FacilityCallback() {
            @Override
            public void onCallback(List<Facility> list) {
                if (list.size() > 0) {
                    arrayListFacility.clear();
                    for (Facility facility : list) {
                        arrayListFacility.add(facility);
                    }
                }
                facilityAdapter.notifyDataSetChanged();
            }
        });
        getComment(new FirestoreCallBack() {
            @Override
            public void onCallback(List<Comment> list) {
                if (list.size() > 0) {
                    arrayList.clear();
                    for (Comment comment : list) {
                        arrayList.add(comment);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    protected interface FirestoreCallBack {
        void onCallback(List<Comment> list);
    }

    protected interface FacilityCallback {
        void onCallback(List<Facility> list);
    }

    private void getFacility(FacilityCallback callback) {
        List<Facility> list = new ArrayList<>();
        firebaseFirestore.collection("boardingHouse").document(room.getIdBoardingHouse()).collection("roomType").document(room.getIdRoomType())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    Map<String, Object> data = new HashMap<>();
                    data = documentSnapshot.getData();
                    for (Map.Entry<String, Object> data1 : data.entrySet()) {
                        if (data1.getKey().equals("facility")) {
                            Map<String, Object> allFacility = (Map<String, Object>) data1.getValue();
                            for (Map.Entry<String, Object> eachFacility : allFacility.entrySet()) {
                                Map<String, Object> entry = (Map<String, Object>) eachFacility.getValue();
                                Facility facility = new Facility();
                                for (Map.Entry<String, Object> d : entry.entrySet()) {

                                    if (d.getKey().equals("image")) {
                                        facility.setImage(d.getValue().toString());
                                    }
                                    if (d.getKey().equals("name")) {
                                        Log.i("FAcmnr", d.getValue().toString());
                                        facility.setName(d.getValue().toString());
                                    }
                                    if (d.getKey().equals("status")) {
                                        if (d.getValue().toString().equals("true")) {
                                            list.add(facility);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
                callback.onCallback(list);
            }
        });
    }

    protected void getComment(FirestoreCallBack firestoreCallBack) {
        List<Comment> list = new ArrayList<>();
        firebaseFirestore.collection("comment").whereEqualTo("boardingHouse", room.getIdBoardingHouse()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                Comment comment = new Comment();
                                comment.setIdComment(documentSnapshot.getId());
                                comment.setNameTenant(documentSnapshot.getString("name"));
                                comment.setContentComment(documentSnapshot.getString("content"));
                                comment.setIdBoardingHouse(documentSnapshot.getString("boardingHouse"));

                                list.add(comment);


                            }
                        }
                        firestoreCallBack.onCallback(list);
                    }
                });
    }

    private void mapping() {
        imageView = findViewById(R.id.rd_image_room);
        textViewDescriptionRoom = findViewById(R.id.rd_description_room);
        textViewNameBoardingHouse = findViewById(R.id.rd_name_boarding_house);
        textViewAddressBoardingHouse = findViewById(R.id.rd_address_boarding_house);
        textViewArea = findViewById(R.id.rd_area_room);
        textViewPrice = findViewById(R.id.rd_price_room);
        textViewNumberPeople = findViewById(R.id.rd_number_people);
        textViewDescriptionBoardingHouse = findViewById(R.id.rd_textView_description_bh);


    }
}