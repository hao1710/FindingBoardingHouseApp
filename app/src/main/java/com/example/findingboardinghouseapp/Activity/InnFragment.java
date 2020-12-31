package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.CommentAdminAdapter;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.Comment;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class InnFragment extends Fragment {

    public InnFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //
    TextView tvName, tvAddress, tvViewMap, tvEPrice, tvWPrice, tvDescription;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch aSwitch;
    RecyclerView rvComment;
    FloatingActionButton floatingActionButton;

    BoardingHouse boardingHouse;
    ArrayList<Comment> listComment;
    CommentAdminAdapter commentAdapter;

    double statusInn;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inn, container, false);

        // findView
        tvName = view.findViewById(R.id.inn_tv_name);
        tvAddress = view.findViewById(R.id.inn_tv_address);
        tvViewMap = view.findViewById(R.id.inn_tv_viewMap);
        tvEPrice = view.findViewById(R.id.inn_tv_ePrice);
        tvWPrice = view.findViewById(R.id.inn_tv_wPrice);
        tvDescription = view.findViewById(R.id.inn_tv_description);
        aSwitch = view.findViewById(R.id.inn_switch_status);
        rvComment = view.findViewById(R.id.inn_rv_comment);
        floatingActionButton = view.findViewById(R.id.inn_fab);

        // underline textView
        String create = "Xem map";
        SpannableString spannableString = new SpannableString(create);
        spannableString.setSpan(new UnderlineSpan(), 0, create.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvViewMap.setText(spannableString);

        Bundle bundle;
        bundle = this.getArguments();
        boardingHouse = new BoardingHouse();

        assert bundle != null;
        boardingHouse = (BoardingHouse) bundle.getSerializable("inn");
        tvName.setText(boardingHouse.getNameBoardingHouse());
        tvAddress.setText("Địa chỉ: " + boardingHouse.getAddressBoardingHouse());
        tvEPrice.setText(boardingHouse.getElectricityPriceBoardingHouse() + " nghìn VND");
        tvWPrice.setText(boardingHouse.getWaterPriceBoardingHouse() + " nghìn VND");

        statusInn = boardingHouse.getStatusBoardingHouse();
        if (statusInn == -1) {
            aSwitch.setChecked(false);
            aSwitch.setText("Trạng thái: Nhà trọ mới");
        } else if (statusInn % 2 == 0) {
            aSwitch.setChecked(true);
            aSwitch.setText("Trạng thái: Đang hoạt động");
        } else {
            int i = (int) statusInn;
            int number = i / 2;
            number++;
            aSwitch.setChecked(false);
            aSwitch.setText("Trạng thái: Đang bị vô hiệu hóa lần " + number);
        }

        tvDescription.setText(boardingHouse.getDescriptionBoardingHouse());
        tvViewMap.setOnClickListener(v -> {
            ArrayList<BoardingHouse> listBoardingHouse = new ArrayList<>();
            listBoardingHouse.add(boardingHouse);
            Intent intentViewMap = new Intent(v.getContext(), MapActivity.class);
            intentViewMap.putExtra("name", boardingHouse.getNameBoardingHouse());
            intentViewMap.putExtra("latitude", boardingHouse.getLatitude());
            intentViewMap.putExtra("longitude", boardingHouse.getLongitude());
            intentViewMap.putExtra("list", listBoardingHouse);
            startActivity(intentViewMap);
        });
        listComment = new ArrayList<>();
        commentAdapter = new CommentAdminAdapter(getContext(), listComment);

        rvComment.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvComment.setLayoutManager(linearLayout);
        rvComment.setAdapter(commentAdapter);

        floatingActionButton.setOnClickListener(v -> {
            boolean status = aSwitch.isChecked();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            if (status) {
                builder.setMessage("Bạn muốn vô hiệu hóa nhà trọ này?")
                        .setPositiveButton("Thực hiện", (dialog, id) -> {
                            // FIRE ZE MISSILES!
                            statusInn++;
                            FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
                                    .update("status", statusInn);
                            aSwitch.setChecked(false);
                            int i = (int) statusInn;
                            int number = i / 2;
                            number++;
                            aSwitch.setText("Trạng thái: Đang bị vô hiệu hóa lần " + number);
                            //       button.setText("Xác nhận");
                            Toast.makeText(getContext(), "Vô hiệu hóa thành công", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", (dialog, id) -> {
                            // User cancelled the dialog
                            dialog.dismiss();
                        });
                // Create the AlertDialog object and return it
            } else {
                builder.setMessage("Bạn muốn xác nhận nhà trọ này?")
                        .setPositiveButton("Thực hiện", (dialog, id) -> {
                            // FIRE ZE MISSILES!

                            statusInn++;
                            FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
                                    .update("status", statusInn);
                            aSwitch.setChecked(true);

                            aSwitch.setText("Trạng thái: Đang hoạt động");
                            //   button.setText("Vô hiệu hóa");
                            Toast.makeText(getContext(), "Xác nhận thành công", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Hủy", (dialog, id) -> {
                            // User cancelled the dialog
                            dialog.dismiss();
                        });
                // Create the AlertDialog object and return it
            }
            builder.create();
            builder.show();
        });

        FirebaseFirestore.getInstance().collection("comment")
                .whereEqualTo("boardingHouse", boardingHouse.getIdBoardingHouse()).addSnapshotListener((value, error) -> {
            listComment.clear();
            assert value != null;
            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                Comment comment = new Comment();
                comment.setIdComment(documentSnapshot.getId());
                comment.setNameTenant(documentSnapshot.getString("name"));
                comment.setContentComment(documentSnapshot.getString("content"));
                comment.setIdBoardingHouse(documentSnapshot.getString("boardingHouse"));
                listComment.add(comment);
            }
            commentAdapter.notifyDataSetChanged();
        });
        return view;
    }
}