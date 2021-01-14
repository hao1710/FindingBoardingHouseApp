package com.example.findingboardinghouseapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Map;

public class RoomTypeAdminAdapter extends RecyclerView.Adapter<RoomTypeAdminAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<RoomType> roomTypeList;

    RecyclerView rvRoom;
    FirebaseFirestore firebaseFirestore;

    public RoomTypeAdminAdapter(Context context, ArrayList<RoomType> arrayList) {
        this.context = context;
        this.roomTypeList = arrayList;
    }

    @NonNull
    @Override
    public RoomTypeAdminAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_room_type_admin, parent, false);
        return new RoomTypeAdminAdapter.MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RoomTypeAdminAdapter.MyViewHolder holder, int position) {
        RoomType roomType = roomTypeList.get(position);
        holder.tvName.setMaxLines(1);
        holder.tvName.setEllipsize(TextUtils.TruncateAt.END);
        holder.tvName.setText("Loại phòng: " + roomType.getNameRoomType());

        holder.tvArea.setMaxLines(1);
        holder.tvArea.setEllipsize(TextUtils.TruncateAt.END);
        holder.tvArea.setText(roomType.getAreaRoomType() + " m\u00b2");

        holder.tvNumberPeople.setMaxLines(1);
        holder.tvNumberPeople.setEllipsize(TextUtils.TruncateAt.END);
        holder.tvNumberPeople.setText(roomType.getNumberPeopleRoomType() + " người ở");

        holder.tvPrice.setMaxLines(1);
        holder.tvPrice.setEllipsize(TextUtils.TruncateAt.END);
        holder.tvPrice.setText(roomType.getPriceRoomType() + " triệu");

        ArrayList<Room> arrayListRoom = new ArrayList<>();
        RoomAdminAdapter adapter = new RoomAdminAdapter(context, arrayListRoom);
        // initial
        firebaseFirestore = FirebaseFirestore.getInstance();
        // recyclerView
        rvRoom.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvRoom.setLayoutManager(linearLayout);
        rvRoom.setAdapter(adapter);
        firebaseFirestore.collection("boardingHouse").document(roomType.getIdBoardingHouse())
                .collection("roomType").document(roomType.getIdRoomType()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.i("HA3", "error");
                }
                if (value == null) {
                    Log.i("HA3", "value null");
                }
                if (value.exists()) {
                    arrayListRoom.clear();
                    Log.i("HA3", "value ko null");
                    Map<String, Object> map = value.getData();
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
                                    if (image.getKey().equals("image")) {
                                        ArrayList<String> arrayList = (ArrayList<String>) image.getValue();
                                        Log.i("HA3", arrayList.size() + " size");
                                        room.setImageRoom(arrayList);
                                    }
                                }
                                arrayListRoom.add(room);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return roomTypeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvArea, tvNumberPeople;
        public MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvArea = view.findViewById(R.id.tv_area);
            tvPrice = view.findViewById(R.id.tv_price);
            tvNumberPeople = view.findViewById(R.id.tv_numberPeople);
            rvRoom = view.findViewById(R.id.admin_rv_room_type);
        }
    }
}
