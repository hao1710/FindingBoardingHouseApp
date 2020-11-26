package com.example.findingboardinghouseapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Model.Facility;
import com.example.findingboardinghouseapp.R;

import java.util.ArrayList;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Facility> arrayList;

    public FacilityAdapter(Context context, ArrayList<Facility> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public FacilityAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_facility, parent, false);
        FacilityAdapter.MyViewHolder myViewHolder = new FacilityAdapter.MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityAdapter.MyViewHolder holder, int position) {
        Facility facility = arrayList.get(position);
        int resId = context.getResources().getIdentifier(facility.getImage(), "drawable", context.getPackageName());

        holder.textViewName.setText(facility.getName());
        holder.imageView.setImageResource(resId);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            textViewName = view.findViewById(R.id.item_facility_name);
            imageView = view.findViewById(R.id.item_facility_image);
        }
    }
}
