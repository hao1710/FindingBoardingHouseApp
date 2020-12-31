package com.example.findingboardinghouseapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Model.Comment;
import com.example.findingboardinghouseapp.R;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<Comment> arrayList;

    public CommentAdapter(Context context, ArrayList<Comment> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_comment, parent, false);
        CommentAdapter.MyViewHolder myViewHolder = new CommentAdapter.MyViewHolder(v);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {
        Comment comment = arrayList.get(position);
        holder.textViewName.setText(comment.getNameTenant());
        holder.textViewContent.setText(comment.getContentComment());
//        holder.textViewNameBoardingHouse.setText(roomRecommendation.getNameBoardingHouse());
//        holder.textViewAddressBoardingHouse.setText(roomRecommendation.getAddressBoardingHouse());
//        holder.textViewPriceRoom.setText(String.valueOf(roomRecommendation.getPriceRoomType()));
//
//        Picasso.with(context).load(roomRecommendation.getImageRoom())
//                .placeholder(R.drawable.load_image_room)
//                .error(R.drawable.ic_app)
//                .into(holder.imageViewRoom);
//
//        Log.i("RoomAdapter", roomRecommendation.getImageRoom());
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

        public TextView textViewName, textViewContent;

        public MyViewHolder(View view) {
            super(view);
           textViewName = view.findViewById(R.id.cmt_name);
           textViewContent = view.findViewById(R.id.cmt_content);
//            textViewNameBoardingHouse = view.findViewById(R.id.rr_name_boarding_house);
//            textViewAddressBoardingHouse = view.findViewById(R.id.rr_address_boarding_house);
//            textViewPriceRoom = view.findViewById(R.id.rr_price_room);

        }
    }
}
