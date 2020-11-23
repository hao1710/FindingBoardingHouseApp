package com.example.findingboardinghouseapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Activity.CreateRoomActivity;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.MyViewHolder> {
    private final Context context;
    private ArrayList<RoomType> arrayList;
    RecyclerView recyclerViewRoom;
    FirebaseFirestore firebaseFirestore;

    public RoomTypeAdapter(Context context, ArrayList<RoomType> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RoomTypeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_type, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomTypeAdapter.MyViewHolder holder, int position) {
        RoomType roomType = arrayList.get(position);

        holder.textViewNameRoomType.setText("Loại phòng: " + roomType.getNameRoomType());
        holder.textViewArea.setText("Diện tích: " + roomType.getAreaRoomType() + " m2");
        holder.textViewPrice.setText("Giá: " + roomType.getPriceRoomType() + " tr");
        holder.textViewNumberPeople.setText(roomType.getNumberPeopleRoomType() + " người");

        boolean isExpanded = roomType.isExpanded();
        holder.expandable.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        ArrayList<Room> arrayListRoom;
        RoomAdapter adapter;

        // initial
        arrayListRoom = new ArrayList<>();
        adapter = new RoomAdapter(context, arrayListRoom);
        firebaseFirestore = FirebaseFirestore.getInstance();

        // recyclerView
        recyclerViewRoom.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerViewRoom.setLayoutManager(linearLayout);
        recyclerViewRoom.setAdapter(adapter);

        firebaseFirestore.collection("boardingHouse").document(roomType.getIdBoardingHouse()).collection("roomType").document(roomType.getIdRoomType()).get()
                .addOnCompleteListener(task -> {
                    arrayListRoom.clear();
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        assert documentSnapshot != null;
                        Map<String, Object> map = documentSnapshot.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().equals("room")) {
                                Map<String, Object> mapRoom = (Map<String, Object>) entry.getValue();
                                for (Map.Entry<String, Object> field : mapRoom.entrySet()) {
                                    Room room = new Room();
                                    room.setNameRoom(field.getKey());
                                    room.setIdBoardingHouse(roomType.getIdBoardingHouse());
                                    room.setIdRoomType(roomType.getIdRoomType());
                                    Map<String, Object> mapField = (Map<String, Object>) field.getValue();
                                    for (Map.Entry<String, Object> image : mapField.entrySet()) {
                                        if (image.getKey().equals("status")) {
                                            room.setStatusRoom((Boolean) image.getValue());
                                        }
                                    }
                                    arrayListRoom.add(room);
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNameRoomType, textViewNumberPeople, textViewPrice, textViewArea;
        LinearLayout expandable;
        Button buttonAddRoom;

        public MyViewHolder(View view) {
            super(view);
            // mapping
            textViewNameRoomType = view.findViewById(R.id.item_rt_name_room_type);
            recyclerViewRoom = view.findViewById(R.id.recyclerViewRoom);
            buttonAddRoom = view.findViewById(R.id.item_rt_btn_add_room);
            textViewArea = view.findViewById(R.id.item_rt_area);
            textViewNumberPeople = view.findViewById(R.id.item_rt_number_people);
            textViewPrice = view.findViewById(R.id.item_rt_price);

            expandable = view.findViewById(R.id.item_rt_expand);

            textViewNameRoomType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RoomType roomType = arrayList.get(getAdapterPosition());
                    roomType.setExpanded(!roomType.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });
            buttonAddRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Room room = new Room();
                    RoomType roomType = arrayList.get(getAdapterPosition());
                    room.setIdBoardingHouse(roomType.getIdBoardingHouse());
                    room.setIdRoomType(roomType.getIdRoomType());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("newRoom", room);
                    Intent intent = new Intent(view.getContext(), CreateRoomActivity.class);
                    intent.putExtras(bundle);
                    ((Activity)  view.getContext()).startActivityForResult(intent,0);
                }
            });

        }

    }



}
