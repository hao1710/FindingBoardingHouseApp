package com.example.findingboardinghouseapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Activity.BoardingHouseActivity;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.R;

import java.io.Serializable;
import java.util.ArrayList;

public class BoardingHouseAdapter extends RecyclerView.Adapter<BoardingHouseAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<BoardingHouse> arrayList;

    public BoardingHouseAdapter(Context context, ArrayList<BoardingHouse> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public BoardingHouseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_boarding_house, parent, false);
        BoardingHouseAdapter.MyViewHolder myViewHolder = new BoardingHouseAdapter.MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoardingHouseAdapter.MyViewHolder holder, int position) {
        BoardingHouse boardingHouse = arrayList.get(position);
        holder.textViewNameBoardingHouse.setText(boardingHouse.getNameBoardingHouse());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNameBoardingHouse;

        public MyViewHolder(View view) {
            super(view);
            textViewNameBoardingHouse = view.findViewById(R.id.item_bh_name_boarding_house);
            view.setOnClickListener((View.OnClickListener) v -> {
                Intent intent = new Intent(context, BoardingHouseActivity.class);
                BoardingHouse boardingHouse = arrayList.get(getLayoutPosition());
                intent.putExtra("boardingHouse", (Serializable) boardingHouse);
                context.startActivity(intent);
            });
        }
    }
}
