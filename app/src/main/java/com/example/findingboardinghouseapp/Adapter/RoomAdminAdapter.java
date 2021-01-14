package com.example.findingboardinghouseapp.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RoomAdminAdapter extends RecyclerView.Adapter<RoomAdminAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<Room> arrayList;

    public RoomAdminAdapter(Context context, ArrayList<Room> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RoomAdminAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_admin, parent, false);
        return new RoomAdminAdapter.MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RoomAdminAdapter.MyViewHolder holder, int position) {
        Room room = arrayList.get(position);
        holder.textViewNameRoom.setMaxLines(1);
        holder.textViewNameRoom.setEllipsize(TextUtils.TruncateAt.END);
        if (room.isStatusRoom()) {
            holder.textViewNameRoom.setText("Phòng " + room.getNameRoom() + ": có người thuê");
        } else {
            holder.textViewNameRoom.setText("Phòng " + room.getNameRoom() + ": trống");
        }

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

        public MyViewHolder(View view) {
            super(view);
            textViewNameRoom = view.findViewById(R.id.admin_tv_name_room);
            imageSlider = view.findViewById(R.id.admin_image_slider);
            expandableLinearLayout = view.findViewById(R.id.admin_expand_image);
            ibShow = view.findViewById(R.id.admin_ib_show);

            ibShow.setOnClickListener(v -> {
                expandableLinearLayout.toggle();
                if (expandableLinearLayout.isExpanded()) {
                    ibShow.setBackgroundResource(R.drawable.ic_arrow_right);
                } else {
                    ibShow.setBackgroundResource(R.drawable.ic_arrow_drop_down);
                }
            });

        }
    }
}
