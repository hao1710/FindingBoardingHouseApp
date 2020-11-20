package com.example.findingboardinghouseapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.findingboardinghouseapp.Adapter.CommentAdapter;
import com.example.findingboardinghouseapp.Model.Comment;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RoomDetailActivity extends AppCompatActivity {
    protected TextView textViewDescriptionRoom, textViewAddressBoardingHouse, textViewDescriptionBoardingHouse, textViewPriceRoom, textViewAreaRoom, textViewNameBoardingHouse;
    protected ImageView imageView;
    protected RecyclerView recyclerViewComment;
    protected CommentAdapter adapter;
    protected ArrayList<Comment> arrayList;
    protected FirebaseFirestore firebaseFirestore;
    protected Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_detail);

        mapping();

        Intent intent = getIntent();
        room = (Room) intent.getSerializableExtra("room");

        textViewPriceRoom.setText(String.valueOf(room.getPriceRoomType()));
        textViewDescriptionBoardingHouse.setText(room.getDescriptionBoardingHouse());
        textViewAreaRoom.setText(String.valueOf(room.getAreaRoomType()));
        textViewAddressBoardingHouse.setText(room.getAddressBoardingHouse());
        textViewNameBoardingHouse.setText(room.getNameBoardingHouse());
        textViewDescriptionRoom.setText(room.getDescriptionRoom());
        Picasso.with(getApplicationContext()).load(room.getImageRoom())
                .placeholder(R.drawable.load_image_room)
                .error(R.drawable.ic_app)
                .into(imageView);


        arrayList = new ArrayList<>();
        adapter = new CommentAdapter(getApplicationContext(), arrayList);
        recyclerViewComment = findViewById(R.id.recyclerViewComment);
        recyclerViewComment.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewComment.setLayoutManager(linearLayout);
        recyclerViewComment.setAdapter(adapter);

        firebaseFirestore = FirebaseFirestore.getInstance();

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
        textViewNameBoardingHouse = findViewById(R.id.rd_name_boarding_house);
        textViewAddressBoardingHouse = findViewById(R.id.rd_address_boarding_house);
        textViewAreaRoom = findViewById(R.id.rd_area_room);
        textViewDescriptionBoardingHouse = findViewById(R.id.rd_description_boarding_house);
        textViewPriceRoom = findViewById(R.id.rd_price_room);
        textViewDescriptionRoom = findViewById(R.id.rd_description_room);
        imageView = findViewById(R.id.rd_image_room);
    }
}