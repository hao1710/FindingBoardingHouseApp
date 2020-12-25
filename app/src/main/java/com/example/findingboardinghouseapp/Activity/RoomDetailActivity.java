package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.findingboardinghouseapp.Adapter.CommentAdapter;
import com.example.findingboardinghouseapp.Adapter.FacilityAdapter;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RoomDetailActivity extends AppCompatActivity {
    private ImageView imageViewRoom;
    private TextView textViewDescriptionRoom, textViewNameBoardingHouse;
    private TextView textViewAreaRoom, textViewPriceRoom, textViewNumberPeople;
    private TextView textViewViewMap;
    private TextView textViewElectricityPrice, textViewWaterPrice;
    private TextView textViewAddressBoardingHouse, textViewDescriptionBoardingHouse, textViewPhoneNumber;
    private RecyclerView recyclerViewFacility, recyclerViewComment;
    private ExpandableLinearLayout expandLayoutComment;
    private TextView textViewComment;
    private EditText editTextName, editTextContent;
    private Button buttonComment, buttonCancel;

    private Room room;

    protected CommentAdapter adapter;
    protected ArrayList<Comment> arrayList;
    protected FirebaseFirestore firebaseFirestore;

    public ArrayList<Facility> arrayListFacility;
    public FacilityAdapter facilityAdapter;
    private ArrayList<BoardingHouse> listBoardingHouse;
    ImageSlider imageSlider;
    HashSet<BoardingHouse> hashSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        findView();


        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra("room");
        setText2();
        listBoardingHouse = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("boardingHouse").whereEqualTo("status", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        BoardingHouse boardingHouse = new BoardingHouse();
                        boardingHouse.setIdBoardingHouse(documentSnapshot.getId());
                        boardingHouse.setLatitude(Objects.requireNonNull(documentSnapshot.getGeoPoint("point")).getLatitude());
                        boardingHouse.setLongitude(Objects.requireNonNull(documentSnapshot.getGeoPoint("point")).getLongitude());
                        boardingHouse.setNameBoardingHouse(documentSnapshot.getString("name"));
                        listBoardingHouse.add(boardingHouse);
                    }
                    hashSet = new HashSet<>(listBoardingHouse);
                    listBoardingHouse.clear();
                    listBoardingHouse.addAll(hashSet);
                }

            }
        });
        //image
        ArrayList<SlideModel> imageList = new ArrayList<>();
        for (int i = 0; i < room.getImageRoom().size(); i++) {

            imageList.add(new SlideModel(room.getImageRoom().get(i), ScaleTypes.CENTER_CROP));
        }
        imageSlider.setImageList(imageList);

        imageSlider.setItemClickListener(i ->
                {
                    new StfalconImageViewer.Builder<>(RoomDetailActivity.this, Collections.singletonList(imageList), (imageView, image) -> Picasso.get().load(imageList.get(i).getImageUrl())
                            .placeholder(R.drawable.load_image_room)
                            .error(R.drawable.ic_app)
                            .into(imageView)).withBackgroundColor(Color.WHITE).show();
                }
        );
        // underline textView
        String create = "Xem map";
        SpannableString spannableString = new SpannableString(create);
        spannableString.setSpan(new UnderlineSpan(), 0, create.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewViewMap.setText(spannableString);

        expandLayoutComment.collapse();
        firebaseFirestore = FirebaseFirestore.getInstance();

        textViewViewMap.setOnClickListener(v -> {
            Intent intent1 = new Intent(v.getContext(), MapActivity.class);
            intent1.putExtra("name", room.getNameBoardingHouse());
            intent1.putExtra("latitude", room.getLatitude());
            intent1.putExtra("longitude", room.getLongitude());
            intent1.putExtra("list", listBoardingHouse);
            startActivity(intent1);
        });
        buttonCancel.setOnClickListener(v -> {
            editTextName.setText("");
            editTextContent.setText("");
            expandLayoutComment.collapse();
        });

        textViewComment.setOnClickListener(v -> expandLayoutComment.toggle());

        buttonComment.setOnClickListener(v -> {
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
                expandLayoutComment.collapse();
                getComment(list -> {
                    if (list.size() > 0) {
                        arrayList.clear();
                        arrayList.addAll(list);
                    }
                    adapter.notifyDataSetChanged();
                });
            }
        });


//        Picasso.get().load(room.getImageRoom())
//                .fit().centerCrop()
//                .placeholder(R.drawable.load_image_room)
//                .error(R.drawable.ic_app)
//                .into(imageViewRoom);

//        imageViewRoom.setOnClickListener(v -> new StfalconImageViewer.Builder<>(v.getContext(), Collections.singletonList(room.getImageRoom()), (imageView, image) -> Picasso.get().load(room.getImageRoom())
//                .placeholder(R.drawable.load_image_room)
//                .error(R.drawable.ic_app)
//                .into(imageView)).withBackgroundColor(Color.WHITE).show());

        arrayList = new ArrayList<>();
        adapter = new CommentAdapter(getApplicationContext(), arrayList);

        recyclerViewComment.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewComment.setLayoutManager(linearLayout);
        recyclerViewComment.setAdapter(adapter);

        arrayListFacility = new ArrayList<>();
        facilityAdapter = new FacilityAdapter(getApplicationContext(), arrayListFacility);

        recyclerViewFacility.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 5);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewFacility.setLayoutManager(gridLayoutManager);
        recyclerViewFacility.setAdapter(facilityAdapter);

        getNameLandlord(room1 -> {
            room.setNameOwnerBoardingHouse(room1.getNameOwnerBoardingHouse());
            room.setPhoneNumberOwnerBoardingHouse(room1.getPhoneNumberOwnerBoardingHouse());
            setText();
        });

        getFacility(list -> {
            if (list.size() > 0) {
                arrayListFacility.clear();
                arrayListFacility.addAll(list);
            }
            facilityAdapter.notifyDataSetChanged();
        });
        getComment(list -> {
            if (list.size() > 0) {
                arrayList.clear();
                arrayList.addAll(list);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @SuppressLint("SetTextI18n")
    private void setText() {

        textViewPhoneNumber.setText("Chủ trọ: " + room.getNameOwnerBoardingHouse() + " - SĐT: " + room.getPhoneNumberOwnerBoardingHouse());

    }
    private void setText2(){
        textViewDescriptionRoom.setText(room.getDescriptionRoomType());
        textViewNameBoardingHouse.setText(room.getNameBoardingHouse());

//        textViewAreaRoom.setText(Html.fromHtml(room.getAreaRoomType()+ "m<sup>2</sup>"));
        textViewAreaRoom.setText(room.getDistanceBoardingHouse() + " m\u00b2");
        textViewPriceRoom.setText(room.getPriceRoomType() + " triệu đồng");
        textViewNumberPeople.setText(room.getNumberPeopleRoomType() + " người");

        textViewElectricityPrice.setText(room.getElectricityPriceBoardingHouse() + " ngàn đồng");
        textViewWaterPrice.setText(room.getWaterPriceBoardingHouse() + " ngàn đồng");

        textViewAddressBoardingHouse.setText("Địa chỉ: " + room.getAddressBoardingHouse());
        textViewDescriptionBoardingHouse.setText(room.getDescriptionBoardingHouse());
    }
    protected interface FirestoreCallBack {
        void onCallback(List<Comment> list);
    }

    protected interface FacilityCallback {
        void onCallback(List<Facility> list);
    }

    protected interface FNameCallback {
        void onCallback(Room room);
    }

    private void getNameLandlord(FNameCallback fNameCallback) {
        Room roomN = new Room();
        firebaseFirestore.collection("landlord").document(room.getIdOwnerBoardingHouse()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                roomN.setNameOwnerBoardingHouse(documentSnapshot.getString("name"));
                roomN.setPhoneNumberOwnerBoardingHouse(documentSnapshot.getString("phoneNumber"));
            }
            fNameCallback.onCallback(roomN);
        });
    }

    @SuppressWarnings("unchecked")
    private void getFacility(FacilityCallback callback) {
        List<Facility> list = new ArrayList<>();
        firebaseFirestore.collection("boardingHouse").document(room.getIdBoardingHouse()).collection("roomType").document(room.getIdRoomType())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                Map<String, Object> data;
                assert documentSnapshot != null;
                data = documentSnapshot.getData();
                assert data != null;
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
        });
    }

    private void getComment(FirestoreCallBack firestoreCallBack) {
        List<Comment> list = new ArrayList<>();
        firebaseFirestore.collection("comment").whereEqualTo("boardingHouse", room.getIdBoardingHouse()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()).getDocuments()) {
                    Comment comment = new Comment();
                    comment.setIdComment(documentSnapshot.getId());
                    comment.setNameTenant(documentSnapshot.getString("name"));
                    comment.setContentComment(documentSnapshot.getString("content"));
                    comment.setIdBoardingHouse(documentSnapshot.getString("boardingHouse"));
                    list.add(comment);
                }
            }
            firestoreCallBack.onCallback(list);
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void findView() {
        imageViewRoom = findViewById(R.id.rd_image_room);
        imageSlider = findViewById(R.id.rd_imageSlider);
        textViewDescriptionRoom = findViewById(R.id.rd_description_room);
        textViewNameBoardingHouse = findViewById(R.id.rd_name_boarding_house);

        textViewAreaRoom = findViewById(R.id.rd_area_room);
        textViewPriceRoom = findViewById(R.id.rd_price_room);
        textViewNumberPeople = findViewById(R.id.rd_number_people);

        textViewElectricityPrice = findViewById(R.id.rd_electricityPrice);
        textViewWaterPrice = findViewById(R.id.rd_waterPrice);

        textViewAddressBoardingHouse = findViewById(R.id.rd_address_boarding_house);
        textViewViewMap = findViewById(R.id.rd_textView_view_map);
        textViewDescriptionBoardingHouse = findViewById(R.id.rd_textView_description_bh);
        textViewPhoneNumber = findViewById(R.id.rd_textView_phoneNumber);

        recyclerViewFacility = findViewById(R.id.rd_recyclerView_facility);
        recyclerViewComment = findViewById(R.id.recyclerViewComment);

        expandLayoutComment = findViewById(R.id.rd_expand_layout_comment);

        textViewComment = findViewById(R.id.rd_textView_comment);
        editTextName = findViewById(R.id.rd_editText_name);
        editTextContent = findViewById(R.id.rd_editText_content);
        buttonComment = findViewById(R.id.rd_button_create_comment);
        buttonCancel = findViewById(R.id.rd_button_cancel_comment);
    }
}