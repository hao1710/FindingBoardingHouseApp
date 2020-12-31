package com.example.findingboardinghouseapp.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Room> arrayList;

    public RoomAdapter(Context context, ArrayList<Room> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RoomAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.MyViewHolder holder, int position) {
        Room room = arrayList.get(position);
        if (room.isStatusRoom()) {
            holder.textViewNameRoom.setText("Phòng " + room.getNameRoom() + ": có người thuê");
        } else {
            holder.textViewNameRoom.setText("Phòng " + room.getNameRoom() + ": trống");
        }
        holder.aSwitchStatus.setChecked(room.isStatusRoom());

        ArrayList<SlideModel> imageList = new ArrayList<>();
        for (int i = 0; i < room.getImageRoom().size(); i++) {
            imageList.add(new SlideModel(room.getImageRoom().get(i), ScaleTypes.CENTER_CROP));
        }
        holder.imageSlider.setImageList(imageList);

        holder.expandableLinearLayout.toggle();

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNameRoom;
        public ImageSlider imageSlider;
        public ExpandableLinearLayout expandableLinearLayout;
        public ImageButton ibShow;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        public Switch aSwitchStatus;

        @SuppressLint("SetTextI18n")
        public MyViewHolder(View view) {
            super(view);
            textViewNameRoom = view.findViewById(R.id.item_r_name_room);
            aSwitchStatus = view.findViewById(R.id.item_r_switch_button);
            imageSlider = view.findViewById(R.id.r_is_image);
            expandableLinearLayout = view.findViewById(R.id.expandImage);
            ibShow = view.findViewById(R.id.item_ib_show);

            ibShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandableLinearLayout.toggle();
                    if (expandableLinearLayout.isExpanded()) {
                        ibShow.setBackgroundResource(R.drawable.ic_arrow_right);
                    } else {
                        ibShow.setBackgroundResource(R.drawable.ic_arrow_drop_down);
                    }
                }
            });
            aSwitchStatus.setOnClickListener(v -> {
                boolean status = aSwitchStatus.isChecked();
                Room room = arrayList.get(getAdapterPosition());
                if (status) {
                    textViewNameRoom.setText("Phòng " + room.getNameRoom() + ": có người thuê");
                } else {
                    textViewNameRoom.setText("Phòng " + room.getNameRoom() + ": trống");
                }
                String name = "room." + room.getNameRoom() + ".status";
                FirebaseFirestore.getInstance().collection("boardingHouse").document(room.getIdBoardingHouse())
                        .collection("roomType").document(room.getIdRoomType())
                        .update(name, status);
            });
            view.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Bạn muốn xóa phòng này?")
                        .setPositiveButton("Xóa", (dialog, id) -> {
                            Map<String, Object> delete = new HashMap<>();
                            String name = "room." + arrayList.get(getAdapterPosition()).getNameRoom();
                            delete.put(name, FieldValue.delete());
                            FirebaseFirestore.getInstance().collection("boardingHouse").document(arrayList.get(getAdapterPosition()).getIdBoardingHouse())
                                    .collection("roomType").document(arrayList.get(getAdapterPosition()).getIdRoomType())
                                    .update(delete);
                            Toast.makeText(context, "Xóa phòng thành công", Toast.LENGTH_SHORT).show();
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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }

}
