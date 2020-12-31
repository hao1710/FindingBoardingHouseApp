package com.example.findingboardinghouseapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Activity.InnManagementActivity;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.R;

import java.util.ArrayList;

public class InnAdapter extends RecyclerView.Adapter<InnAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<BoardingHouse> arrayList;

    public InnAdapter(Context context, ArrayList<BoardingHouse> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public InnAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_boarding_house_admin, parent, false);
        return new InnAdapter.MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InnAdapter.MyViewHolder holder, int position) {
        BoardingHouse boardingHouse = arrayList.get(position);
        double status = boardingHouse.getStatusBoardingHouse();
        if (status == -1) {
            holder.aSwitch.setChecked(false);
            holder.tvName.setText(boardingHouse.getNameBoardingHouse() + ": nhà trọ mới");
        } else if (status % 2 == 0) {
            holder.aSwitch.setChecked(true);
            holder.tvName.setText(boardingHouse.getNameBoardingHouse());
        } else {
            int i = (int) (status / 2) + 1;
            holder.aSwitch.setChecked(false);
            holder.tvName.setText(boardingHouse.getNameBoardingHouse() + ": đang bị vô hiệu hóa lần " + i);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public Switch aSwitch;

        public MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.itemBHA_name);
            aSwitch = view.findViewById(R.id.itemBHA_switch);

            view.setOnClickListener(v -> {
                Intent intent = new Intent(context, InnManagementActivity.class);
                BoardingHouse boardingHouse = arrayList.get(getLayoutPosition());
                intent.putExtra("boardingHouse", boardingHouse);

                ((Activity) context).startActivityForResult(intent, 5);
            });
        }
    }

}
