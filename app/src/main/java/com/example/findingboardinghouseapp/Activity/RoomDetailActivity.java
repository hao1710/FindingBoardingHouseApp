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
import java.util.Map;
import java.util.Objects;

public class RoomDetailActivity extends AppCompatActivity {
    ImageView imageViewRoom;
    ImageSlider imageSlider;

    TextView tvDescriptionRoom, tvName;
    TextView tvArea, tvPrice, tvNumberPeople;
    TextView tvEPrice, tvWPrice;
    TextView tvAddress, tvViewMap, tvDescriptionInn, tvLandlord;

    RecyclerView rvFacility, rvComment;

    TextView tvComment;
    ExpandableLinearLayout expandComment;
    EditText edtName, edtContent;
    Button buttonComment, buttonCancel;


    private Room room;

    private ArrayList<Comment> listComment;
    private CommentAdapter adapterComment;

    private ArrayList<Facility> listFacility;
    private FacilityAdapter adapterFacility;

    private ArrayList<BoardingHouse> listInn;

    HashSet<BoardingHouse> hashSet;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        findView();

        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra("room");

        setTextRoom();
        FirebaseFirestore.getInstance().collection("landlord").document(room.getIdOwnerBoardingHouse())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                assert documentSnapshot != null;
                tvLandlord.setText("Chủ trọ: " + documentSnapshot.getString("name") + " - " + documentSnapshot.getString("phoneNumber"));
            }
        });

        // underline textView
        String create = "Xem map";
        SpannableString spannableString = new SpannableString(create);
        spannableString.setSpan(new UnderlineSpan(), 0, create.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvViewMap.setText(spannableString);

        // for view map
        listInn = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("boardingHouse").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()).getDocuments()) {
                        BoardingHouse boardingHouse = new BoardingHouse();
                        boardingHouse.setIdBoardingHouse(documentSnapshot.getId());
                        boardingHouse.setLatitude(Objects.requireNonNull(documentSnapshot.getGeoPoint("point")).getLatitude());
                        boardingHouse.setLongitude(Objects.requireNonNull(documentSnapshot.getGeoPoint("point")).getLongitude());
                        boardingHouse.setNameBoardingHouse(documentSnapshot.getString("name"));
                        listInn.add(boardingHouse);
                    }
                }
            }
        });

        // image
        ArrayList<SlideModel> listImage = new ArrayList<>();
        for (int i = 0; i < room.getImageRoom().size(); i++) {
            listImage.add(new SlideModel(room.getImageRoom().get(i), ScaleTypes.CENTER_CROP));
        }
        imageSlider.setImageList(listImage);

        imageSlider.setItemClickListener(i -> {
                    new StfalconImageViewer.Builder<>(RoomDetailActivity.this, Collections.singletonList(listImage), (imageView, image) -> Picasso.get().load(listImage.get(i).getImageUrl())
                            .placeholder(R.drawable.load_image_room)
                            .error(R.drawable.ic_app)
                            .into(imageView)).withBackgroundColor(Color.WHITE).show();
                }
        );


        expandComment.collapse();

        tvViewMap.setOnClickListener(v -> {
            Intent intent1 = new Intent(v.getContext(), MapActivity.class);
            intent1.putExtra("name", room.getNameBoardingHouse());
            intent1.putExtra("latitude", room.getLatitude());
            intent1.putExtra("longitude", room.getLongitude());
            intent1.putExtra("list", listInn);
            startActivity(intent1);
        });
        buttonCancel.setOnClickListener(v -> {
            edtName.setText("");
            edtContent.setText("");
            expandComment.collapse();
        });

        tvComment.setOnClickListener(v -> expandComment.toggle());

        buttonComment.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String content = edtContent.getText().toString().trim();
            if (name.equals("") || content.equals("")) {
                Toast.makeText(v.getContext(), "Vui lòng đủ tên và nội dung", Toast.LENGTH_SHORT).show();
            } else {
                CommentCRUD commentCRUD = new CommentCRUD();
                commentCRUD.setBoardingHouse(room.getIdBoardingHouse());
                commentCRUD.setContent(content);
                commentCRUD.setName(name);
                FirebaseFirestore.getInstance().collection("comment").add(commentCRUD);

                edtName.setText("");
                edtContent.setText("");
                expandComment.collapse();
            }
        });

        listComment = new ArrayList<>();
        adapterComment = new CommentAdapter(getApplicationContext(), listComment);

        rvComment.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rvComment.setLayoutManager(linearLayout);
        rvComment.setAdapter(adapterComment);

        FirebaseFirestore.getInstance().collection("comment").whereEqualTo("boardingHouse", room.getIdBoardingHouse())
                .addSnapshotListener((value, error) -> {
                    assert value != null;
                    if (value.size() >= 0) {
                        listComment.clear();
                        for (DocumentSnapshot documentSnapshot : value) {
                            Comment comment = new Comment();
                            comment.setIdComment(documentSnapshot.getId());
                            comment.setNameTenant(documentSnapshot.getString("name"));
                            comment.setContentComment(documentSnapshot.getString("content"));
                            comment.setIdBoardingHouse(documentSnapshot.getString("boardingHouse"));
                            listComment.add(comment);
                        }
                        adapterComment.notifyDataSetChanged();
                    }

                });


        listFacility = new ArrayList<>();
        adapterFacility = new FacilityAdapter(getApplicationContext(), listFacility);

        rvFacility.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 5);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvFacility.setLayoutManager(gridLayoutManager);
        rvFacility.setAdapter(adapterFacility);

        FirebaseFirestore.getInstance().collection("boardingHouse").document(room.getIdBoardingHouse())
                .collection("roomType").document(room.getIdRoomType()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
                                            listFacility.add(facility);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    adapterFacility.notifyDataSetChanged();
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void setTextRoom() {
        tvDescriptionRoom.setText(room.getDescriptionRoomType());
        tvName.setText(room.getNameBoardingHouse());

        tvArea.setText(room.getAreaRoomType() + " m\u00b2");
        tvPrice.setText(room.getPriceRoomType() + " triệu đồng");
        tvNumberPeople.setText(room.getNumberPeopleRoomType() + " người");

        tvEPrice.setText(room.getElectricityPriceBoardingHouse() + " nghìn đồng");
        tvWPrice.setText(room.getWaterPriceBoardingHouse() + " nghìn đồng");

        tvAddress.setText("Địa chỉ: " + room.getAddressBoardingHouse());
        tvDescriptionInn.setText(room.getDescriptionBoardingHouse());
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void findView() {
        imageViewRoom = findViewById(R.id.rd_iv_room);
        imageSlider = findViewById(R.id.rd_imageSlider);

        tvDescriptionRoom = findViewById(R.id.rd_tv_description_room);
        tvName = findViewById(R.id.rd_tv_name);

        tvArea = findViewById(R.id.rd_tv_area);
        tvPrice = findViewById(R.id.rd_tv_price);
        tvNumberPeople = findViewById(R.id.rd_tv_number_people);

        tvEPrice = findViewById(R.id.rd_tv_ePrice);
        tvWPrice = findViewById(R.id.rd_tv_wPrice);

        tvAddress = findViewById(R.id.rd_tv_address);
        tvViewMap = findViewById(R.id.rd_tv_view_map);
        tvDescriptionInn = findViewById(R.id.rd_tv_description_inn);
        tvLandlord = findViewById(R.id.rd_tv_landlord);

        rvFacility = findViewById(R.id.rd_rv_facility);
        rvComment = findViewById(R.id.rd_rv_comment);

        tvComment = findViewById(R.id.rd_tv_comment);
        expandComment = findViewById(R.id.rd_expand_comment);

        edtName = findViewById(R.id.rd_edt_name);
        edtContent = findViewById(R.id.rd_edt_content);
        buttonComment = findViewById(R.id.rd_button_create_comment);
        buttonCancel = findViewById(R.id.rd_button_cancel_comment);
    }
}