package com.example.findingboardinghouseapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Activity.RoomDetailActivity;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.R;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomRecommendationAdapter extends RecyclerView.Adapter<RoomRecommendationAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Room> arrayList;

    public RoomRecommendationAdapter(Context context, ArrayList<Room> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_recommendation, null);
        MyViewHolder myViewHolder = new MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Room roomRecommendation = arrayList.get(position);
        holder.textViewNameBoardingHouse.setText(roomRecommendation.getNameBoardingHouse());
        holder.textViewAddressBoardingHouse.setText(roomRecommendation.getAddressBoardingHouse());
        holder.textViewPriceRoom.setText(String.valueOf(roomRecommendation.getPriceRoomType()));

        Picasso.with(context).load(roomRecommendation.getImageRoom())
                .placeholder(R.drawable.load_image_room)
                .error(R.drawable.ic_app)
                .into(holder.imageViewRoom);


//        holder.textViewName.setText(roomRecommendation.getNameRoom());
//
//        holder.textViewAddress.setText(Boolean.toString(roomRecommendation.isStatus()));
//        holder.textViewPrice.setText((int) roomRecommendation.getPriceRoomType());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewRoom;
        public TextView textViewNameBoardingHouse, textViewAddressBoardingHouse, textViewPriceRoom;

        public MyViewHolder(View view) {
            super(view);
            imageViewRoom = view.findViewById(R.id.rr_image_room);
            textViewNameBoardingHouse = view.findViewById(R.id.rr_name_boarding_house);
            textViewAddressBoardingHouse = view.findViewById(R.id.rr_address_boarding_house);
            textViewPriceRoom = view.findViewById(R.id.rr_price_room);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, RoomDetailActivity.class);
                    Room room = arrayList.get(getPosition());
                    i.putExtra("room", (Serializable) room);
                    context.startActivity(i);
                }
            });
        }
    }
}
