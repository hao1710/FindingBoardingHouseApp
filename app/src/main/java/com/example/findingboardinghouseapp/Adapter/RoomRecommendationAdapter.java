package com.example.findingboardinghouseapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Room roomRecommendation = arrayList.get(position);

        holder.tvNumberPeople.setText(roomRecommendation.getNumberPeopleRoomType() + " người ở");

        holder.textViewNameBoardingHouse.setText(roomRecommendation.getNameBoardingHouse());

        holder.textViewAddressBoardingHouse.setMaxLines(1);
        holder.textViewAddressBoardingHouse.setEllipsize(TextUtils.TruncateAt.END);
        holder.textViewAddressBoardingHouse.setText(roomRecommendation.getAddressBoardingHouse());

        holder.textViewPriceRoom.setText(roomRecommendation.getPriceRoomType() + " triệu / tháng");
        holder.tvDistance.setText("Cách ĐHCT " + roomRecommendation.getDistanceBoardingHouse() + " km");
        Picasso.get().load(roomRecommendation.getImageRoom().get(0))
                .fit().centerCrop()
                .placeholder(R.drawable.load_image_room)
                .error(R.drawable.ic_app)
                .into(holder.imageViewRoom);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewRoom;
        public TextView textViewNameBoardingHouse, textViewAddressBoardingHouse, textViewPriceRoom, tvNumberPeople;
        public TextView tvDistance;

        public MyViewHolder(View view) {
            super(view);
            imageViewRoom = view.findViewById(R.id.rr_image_room);
            tvNumberPeople = view.findViewById(R.id.numberPeople);
            textViewNameBoardingHouse = view.findViewById(R.id.rr_name_boarding_house);
            textViewAddressBoardingHouse = view.findViewById(R.id.rr_address_boarding_house);
            textViewPriceRoom = view.findViewById(R.id.rr_price_room);
            tvDistance = view.findViewById(R.id.rr_distance);

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
