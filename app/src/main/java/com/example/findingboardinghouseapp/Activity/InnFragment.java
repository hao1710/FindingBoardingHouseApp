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
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.CommentAdapter;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.Comment;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InnFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InnFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InnFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InnFragment newInstance(String param1, String param2) {
        InnFragment fragment = new InnFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //
    TextView tvName, tvAddress, tvViewMap, tvEPrice, tvWPrice, tvDescription;
    Switch aSwitch;
    BoardingHouse boardingHouse;
    ArrayList<Comment> arrayList;
    RecyclerView rvComment;
    CommentAdapter adapter;
    //Button button;
    FloatingActionButton floatingActionButton;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inn, container, false);

        // findView
        tvName = view.findViewById(R.id.tv_name_bh);
        tvAddress = view.findViewById(R.id.tv_address_bh);
        tvViewMap = view.findViewById(R.id.tv_viewMap);
        tvEPrice = view.findViewById(R.id.tv_ePrice);
        tvWPrice = view.findViewById(R.id.tv_wPrice);
        tvDescription = view.findViewById(R.id.inn_description);
        aSwitch = view.findViewById(R.id.switch_status);
        rvComment = view.findViewById(R.id.rv_comment);
        //button = view.findViewById(R.id.button_status);
        floatingActionButton = view.findViewById(R.id.inn_fab);
        // underline textView
        String create = "Xem map";
        SpannableString spannableString = new SpannableString(create);
        spannableString.setSpan(new UnderlineSpan(), 0, create.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvViewMap.setText(spannableString);

        Bundle bundle = new Bundle();
        bundle = this.getArguments();
        boardingHouse = new BoardingHouse();
        arrayList = new ArrayList<>();
        assert bundle != null;
        boardingHouse = (BoardingHouse) bundle.getSerializable("inn");
        arrayList = (ArrayList<Comment>) bundle.getSerializable("comment");
        tvName.setText(boardingHouse.getNameBoardingHouse());
        tvAddress.setText("Địa chỉ: " + boardingHouse.getAddressBoardingHouse());
        tvEPrice.setText(boardingHouse.getElectricityPriceBoardingHouse() + " ngàn VND");
        tvWPrice.setText(boardingHouse.getWaterPriceBoardingHouse() + " ngàn VND");
        aSwitch.setChecked(boardingHouse.isStatusBoardingHouse());
        if (boardingHouse.isStatusBoardingHouse()) {
           // button.setText("Vô hiệu hóa");
            aSwitch.setText("Trạng thái: Đang hoạt động");
        } else {
            aSwitch.setText("Trạng thái: Đang bị vô hiệu hóa");
           // button.setText("Xác nhận");
        }

        tvDescription.setText(boardingHouse.getDescriptionBoardingHouse());
        tvViewMap.setOnClickListener(v -> {
            ArrayList<BoardingHouse> listBoardingHouse = new ArrayList<>();
            listBoardingHouse.add(boardingHouse);
            Intent intent1 = new Intent(v.getContext(), MapActivity.class);
            intent1.putExtra("name", boardingHouse.getNameBoardingHouse());
            intent1.putExtra("latitude", boardingHouse.getLatitude());
            intent1.putExtra("longitude", boardingHouse.getLongitude());
            intent1.putExtra("list", listBoardingHouse);
            startActivity(intent1);
        });

        adapter = new CommentAdapter(getContext(), arrayList);
        rvComment.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvComment.setLayoutManager(linearLayout);
        rvComment.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        floatingActionButton.setOnClickListener(v -> {
            boolean status = aSwitch.isChecked();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            if (status) {
                builder.setMessage("Bạn muốn vô hiệu hóa nhà trọ này?")
                        .setPositiveButton("Thực hiện", (dialog, id) -> {
                            // FIRE ZE MISSILES!

                            FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
                                    .update("status", false);
                            aSwitch.setChecked(false);
                            aSwitch.setText("Trạng thái: Đang bị vô hiệu hóa");
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

                            FirebaseFirestore.getInstance().collection("boardingHouse").document(boardingHouse.getIdBoardingHouse())
                                    .update("status", true);
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

        return view;
    }
}