package com.example.findingboardinghouseapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Model.Comment;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CommentAdminAdapter extends RecyclerView.Adapter<CommentAdminAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<Comment> commentList;

    public CommentAdminAdapter(Context context, ArrayList<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentAdminAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_comment_admin, parent, false);
        CommentAdminAdapter.MyViewHolder myViewHolder = new CommentAdminAdapter.MyViewHolder(v);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdminAdapter.MyViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tvName.setText(comment.getNameTenant());
        holder.tvContent.setText(comment.getContentComment());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName, tvContent;

        public MyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.cmt_name);
            tvContent = view.findViewById(R.id.cmt_content);
//            textViewNameBoardingHouse = view.findViewById(R.id.rr_name_boarding_house);
//            textViewAddressBoardingHouse = view.findViewById(R.id.rr_address_boarding_house);
//            textViewPriceRoom = view.findViewById(R.id.rr_price_room);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage("Bạn muốn xóa bình luận này?")
                            .setPositiveButton("Xóa", (dialog, id) -> {
                                // FIRE ZE MISSILES!
                                FirebaseFirestore.getInstance().collection("comment").document(commentList.get(getAdapterPosition()).getIdComment()).delete();
                                Toast.makeText(context, "Xóa bình luận thành công", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Hủy", (dialog, id) -> {
                                // User cancelled the dialog
                                dialog.dismiss();
                            });
                    // Create the AlertDialog object and return it
                    builder.create();
                    builder.show();
                    return true;
                }
            });
        }
    }
}
