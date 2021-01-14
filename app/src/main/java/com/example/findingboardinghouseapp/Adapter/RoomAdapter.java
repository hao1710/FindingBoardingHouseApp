package com.example.findingboardinghouseapp.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.MyViewHolder> {
    private final Context context;
    private final ArrayList<Room> roomList;
    ProgressDialog progressDialog;

    public RoomAdapter(Context context, ArrayList<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
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
        Room room = roomList.get(position);

        holder.textViewNameRoom.setMaxLines(1);
        holder.textViewNameRoom.setEllipsize(TextUtils.TruncateAt.END);

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
        return roomList.size();
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

            ibShow.setOnClickListener(v -> {
                expandableLinearLayout.toggle();
                if (expandableLinearLayout.isExpanded()) {
                    ibShow.setBackgroundResource(R.drawable.ic_arrow_right);
                } else {
                    ibShow.setBackgroundResource(R.drawable.ic_arrow_drop_down);
                }
            });
            aSwitchStatus.setOnClickListener(v -> {
                boolean status = aSwitchStatus.isChecked();
                Room room = roomList.get(getAdapterPosition());
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
                            progressDialog = new ProgressDialog(v.getContext());
                            progressDialog.setMessage("Vui lòng đợi");
                            progressDialog.show();

                            Map<String, Object> delete = new HashMap<>();
                            String name = "room." + roomList.get(getAdapterPosition()).getNameRoom();
                            delete.put(name, FieldValue.delete());
                            FirebaseFirestore.getInstance().collection("boardingHouse").document(roomList.get(getAdapterPosition()).getIdBoardingHouse())
                                    .collection("roomType").document(roomList.get(getAdapterPosition()).getIdRoomType())
                                    .update(delete);

                            final int[] count = {0};
                            final int size = roomList.get(getAdapterPosition()).getImageRoom().size();
                            for (int i = 0; i < size; i++) {
                                String url = roomList.get(getAdapterPosition()).getImageRoom().get(i);
                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                                storageReference.delete().addOnCompleteListener(task -> {
                                    count[0]++;
                                    if (count[0] == size) {
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Xóa phòng thành công", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
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
