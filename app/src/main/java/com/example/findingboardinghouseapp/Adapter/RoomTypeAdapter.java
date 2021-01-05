package com.example.findingboardinghouseapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Activity.CreateRoomActivity;
import com.example.findingboardinghouseapp.Activity.UpdateRoomTypeActivity;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.R;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;

import java.util.ArrayList;
import java.util.Map;

public class RoomTypeAdapter extends RecyclerView.Adapter<RoomTypeAdapter.MyViewHolder> {
    private Context context;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RoomTypeAdapter.MyViewHolder holder, int position) {
        RoomType roomType = arrayList.get(position);

        holder.textViewNameRoomType.setText("Loại phòng: " + roomType.getNameRoomType());
        holder.textViewArea.setText(roomType.getAreaRoomType() + " m\u00b2");
        holder.textViewPrice.setText("Giá: " + roomType.getPriceRoomType() + " triệu");
        holder.textViewNumberPeople.setText("Tối đa "+roomType.getNumberPeopleRoomType() + " người");

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

        firebaseFirestore.collection("boardingHouse").document(roomType.getIdBoardingHouse())
                .collection("roomType").document(roomType.getIdRoomType()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                arrayListRoom.clear();
                if (value.exists()) {
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
                                    if(image.getKey().equals("image")){
                                        ArrayList<String> arrayList = (ArrayList<String>) image.getValue();
                                        room.setImageRoom(arrayList);
                                    }
                                }
                                arrayListRoom.add(room);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

//        firebaseFirestore.collection("boardingHouse").document(roomType.getIdBoardingHouse())
//                .collection("roomType").document(roomType.getIdRoomType()).get()
//                .addOnCompleteListener(task -> {
//                    arrayListRoom.clear();
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot documentSnapshot = task.getResult();
//                        assert documentSnapshot != null;
//                        Map<String, Object> map = documentSnapshot.getData();
//                        for (Map.Entry<String, Object> entry : map.entrySet()) {
//                            if (entry.getKey().equals("room")) {
//                                Map<String, Object> mapRoom = (Map<String, Object>) entry.getValue();
//                                for (Map.Entry<String, Object> field : mapRoom.entrySet()) {
//                                    Room room = new Room();
//                                    room.setNameRoom(field.getKey());
//                                    room.setIdBoardingHouse(roomType.getIdBoardingHouse());
//                                    room.setIdRoomType(roomType.getIdRoomType());
//                                    Map<String, Object> mapField = (Map<String, Object>) field.getValue();
//                                    for (Map.Entry<String, Object> image : mapField.entrySet()) {
//                                        if (image.getKey().equals("status")) {
//                                            room.setStatusRoom((Boolean) image.getValue());
//                                        }
//                                    }
//                                    arrayListRoom.add(room);
//                                }
//                            }
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNameRoomType, textViewNumberPeople, textViewPrice, textViewArea;
        ImageButton imageButton, imageButtonAddComment;

        ExpandableRelativeLayout expandableRelativeLayout;


        public MyViewHolder(View view) {
            super(view);
            // mapping
            textViewNameRoomType = view.findViewById(R.id.item_rt_name_room_type);
            recyclerViewRoom = view.findViewById(R.id.recyclerViewRoom);

            textViewArea = view.findViewById(R.id.item_rt_area);
            textViewNumberPeople = view.findViewById(R.id.item_rt_number_people);
            textViewPrice = view.findViewById(R.id.item_rt_price);
            imageButton = view.findViewById(R.id.rt_imageButton_menu);

            //expandableRelativeLayout = view.findViewById(R.id.item_rt_expandable_layout);



            textViewNameRoomType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableRelativeLayout.toggle();
                }
            });


            imageButton.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(v.getContext(), imageButton);
                    popupMenu.getMenuInflater().inflate(R.menu.menu_popup_in_rta, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(item -> {
                        switch (item.getItemId()) {
                            case R.id.rt_item_create_room:

                                Room room = new Room();
                                RoomType roomType = arrayList.get(getAdapterPosition());
                                room.setIdBoardingHouse(roomType.getIdBoardingHouse());
                                room.setIdRoomType(roomType.getIdRoomType());
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("newRoom", room);
                                Intent intent = new Intent(view.getContext(), CreateRoomActivity.class);
                                intent.putExtras(bundle);
                                ((Activity) view.getContext()).startActivityForResult(intent, 41);
                                // return true;
                                break;
                            case R.id.rt_item_update:
                                RoomType roomTypeUpdate = arrayList.get(getAdapterPosition());
                                Bundle bundleUpdate = new Bundle();
                                bundleUpdate.putSerializable("roomTypeUpdate", roomTypeUpdate);
                                Intent intentUpdate = new Intent(v.getContext(), UpdateRoomTypeActivity.class);
                                intentUpdate.putExtras(bundleUpdate);

                                ((Activity) v.getContext()).startActivityForResult(intentUpdate, 41);
                                break;
                        }
                        return true;
                    });
                    popupMenu.show();
                }
            });
            view.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Bạn muốn xóa loại phòng này?")
                        .setPositiveButton("Xóa", (dialog, id) -> {
                            // FIRE ZE MISSILES!
                            FirebaseFirestore.getInstance().collection("boardingHouse").document(arrayList.get(getAdapterPosition()).getIdBoardingHouse())
                                    .collection("roomType").document(arrayList.get(getAdapterPosition()).getIdRoomType()).delete();
                            Toast.makeText(context, "Xóa loại phòng thành công", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", (dialog, id) -> {
                            // User cancelled the dialog
                            dialog.dismiss();
                        });
                // Create the AlertDialog object and return it
                builder.create();
                builder.show();
                return true;
            });
        }

    }



}
