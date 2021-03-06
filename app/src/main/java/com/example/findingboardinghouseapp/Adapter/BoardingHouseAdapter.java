package com.example.findingboardinghouseapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Activity.BoardingHouseActivity;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inn, parent, false);
        BoardingHouseAdapter.MyViewHolder myViewHolder = new BoardingHouseAdapter.MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BoardingHouseAdapter.MyViewHolder holder, int position) {
        BoardingHouse boardingHouse = arrayList.get(position);
        if (boardingHouse.getStatusBoardingHouse() == -1) {
            holder.tvStatus.setText("Nhà trọ mới. Liên hệ admin tại email anhhaovo1710@gmail.com để xác nhận");
//            holder.textViewNameBoardingHouse.setTextColor(context.getResources().getColor(R.color.red));
            int resId = context.getResources().getIdentifier("ic_warning", "drawable", context.getPackageName());
            holder.imageView.setImageResource(resId);
        } else if (boardingHouse.getStatusBoardingHouse() % 2 == 1) {
            holder.tvStatus.setText("Nhà trọ đang bị vô hiệu hóa. Liên hệ admin tại email anhhaovo1710@gmail.com");
//            holder.textViewNameBoardingHouse.setTextColor(context.getResources().getColor(R.color.red));
            int resId = context.getResources().getIdentifier("ic_warning", "drawable", context.getPackageName());
            holder.imageView.setImageResource(resId);
        } else {
            int resId = context.getResources().getIdentifier("ic_check", "drawable", context.getPackageName());
            holder.imageView.setImageResource(resId);
        }
        holder.textViewNameBoardingHouse.setText(boardingHouse.getNameBoardingHouse());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNameBoardingHouse, tvStatus;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            textViewNameBoardingHouse = view.findViewById(R.id.item_bh_name_boarding_house);
            tvStatus = view.findViewById(R.id.tv_status);
            imageView = view.findViewById(R.id.item_bh_iv);
            view.setOnClickListener(v -> {
                Intent intent = new Intent(context, BoardingHouseActivity.class);
                BoardingHouse boardingHouse = arrayList.get(getLayoutPosition());
                intent.putExtra("boardingHouse", boardingHouse);
                context.startActivity(intent);
            });
            view.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Bạn muốn xóa khu trọ này?")
                        .setPositiveButton("Xóa", (dialog, id) -> {
                            // FIRE ZE MISSILES!
                            FirebaseFirestore.getInstance().collection("boardingHouse")
                                    .document(arrayList.get(getAdapterPosition()).getIdBoardingHouse()).delete();
                            Toast.makeText(context, "Xóa khu trọ thành công", Toast.LENGTH_SHORT).show();
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
