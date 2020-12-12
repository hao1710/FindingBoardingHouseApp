package com.example.findingboardinghouseapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Activity.BoardingHouseActivity;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

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
                            FirebaseFirestore.getInstance().collection("boardingHouse").document(arrayList.get(getAdapterPosition()).getIdBoardingHouse()).delete();
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
