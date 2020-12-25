package com.example.findingboardinghouseapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Model.RoomType;
import com.example.findingboardinghouseapp.R;

import java.util.ArrayList;

public class RoomTypeAdminAdapter extends RecyclerView.Adapter<RoomTypeAdminAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<RoomType> arrayList;

    public RoomTypeAdminAdapter(Context context, ArrayList<RoomType> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
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
        RoomType roomType = arrayList.get(position);
        holder.tvName.setText("Loại phòng: " + roomType.getNameRoomType());
        holder.tvArea.setText(roomType.getAreaRoomType() + " m2");
        holder.tvNumberPeople.setText(roomType.getNumberPeopleRoomType() + " người ở");
        holder.tvPrice.setText(roomType.getPriceRoomType() + " triệu");
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvArea, tvNumberPeople;

        public MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_name);
            tvArea = view.findViewById(R.id.tv_area);
            tvPrice = view.findViewById(R.id.tv_price);
            tvNumberPeople = view.findViewById(R.id.tv_numberPeople);
        }
    }
}
