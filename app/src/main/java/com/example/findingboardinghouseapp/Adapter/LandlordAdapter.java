package com.example.findingboardinghouseapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class LandlordAdapter extends RecyclerView.Adapter<LandlordAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Landlord> arrayList;
    private RecyclerView rvBoardingHouse;
    FirebaseFirestore firebaseFirestore;

    public LandlordAdapter(Context context, ArrayList<Landlord> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @NonNull
    @Override
    public LandlordAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_admin, parent, false);
        return new LandlordAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Landlord landlord = arrayList.get(position);
        holder.tvName.setText(landlord.getNameLandlord());

        ArrayList<BoardingHouse> arrayListBoardingHouse;
        InnAdapter adapter;
        // initial
        arrayListBoardingHouse = new ArrayList<>();
        adapter = new InnAdapter(context, arrayListBoardingHouse);
        firebaseFirestore = FirebaseFirestore.getInstance();

        // recyclerView
        rvBoardingHouse.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        rvBoardingHouse.setLayoutManager(linearLayout);
        rvBoardingHouse.setAdapter(adapter);

        firebaseFirestore.collection("boardingHouse").whereEqualTo("owner", landlord.getIdLandlord()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()).getDocuments()) {
                            BoardingHouse boardingHouse = new BoardingHouse();
                            boardingHouse.setIdBoardingHouse(documentSnapshot.getId());
                            boardingHouse.setNameBoardingHouse(documentSnapshot.getString("name"));
                            boardingHouse.setAddressBoardingHouse(documentSnapshot.getString("address"));
                            boardingHouse.setDistanceBoardingHouse(documentSnapshot.getDouble("distance"));
                            boardingHouse.setElectricityPriceBoardingHouse(documentSnapshot.getDouble("electricityPrice"));
                            boardingHouse.setWaterPriceBoardingHouse(documentSnapshot.getDouble("waterPrice"));
                            boardingHouse.setDescriptionBoardingHouse(documentSnapshot.getString("description"));
                            boardingHouse.setIdOwnerBoardingHouse(documentSnapshot.getString("owner"));
                            boardingHouse.setStatusBoardingHouse(documentSnapshot.getDouble("status"));
                            boardingHouse.setLatitude(Objects.requireNonNull(documentSnapshot.getGeoPoint("point")).getLatitude());
                            boardingHouse.setLongitude(Objects.requireNonNull(documentSnapshot.getGeoPoint("point")).getLongitude());
                            arrayListBoardingHouse.add(boardingHouse);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvEmail;

        public MyViewHolder(View view) {
            super(view);
            // mapping
            tvName = view.findViewById(R.id.itemLandlord_tv_name);
            rvBoardingHouse = view.findViewById(R.id.itemLandlord_rv_boardingHouse);

        }

    }


}
