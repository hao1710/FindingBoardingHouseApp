package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.BoardingHouseAdapter;
import com.example.findingboardinghouseapp.Model.BoardingHouse;
import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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

    //code under

    public static final int REQUEST_CODE_FROM_ACCOUNT_FRAGMENT = 34;
    public static final int RESULT_CODE_FROM_ACCOUNT_FRAGMENT = 33;
    public static final String MY_PREFERENCES = "MyPre";
    public SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ImageButton imageButtonMenu;
    private EditText editTextName, editTextAddress, editTextPhoneNumber, editTextEmail, editTextPassword;
    private ExpandableRelativeLayout expandLayoutSetting;
    private Button buttonSaveSetting, buttonCancelSetting;
    private Landlord landlord;
    private RecyclerView recyclerViewBoardingHouse;

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        findView(view);

        // sharedPreferences
        sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        //
        Bundle bundle = getArguments();
        assert bundle != null;
        landlord = (Landlord) bundle.getSerializable("landlord");

        // recyclerView
        recyclerViewBoardingHouse.setHasFixedSize(true);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewBoardingHouse.setLayoutManager(linearLayout);

        // do something
        setTextDataLandlord(landlord);
        expandLayoutSetting.collapse();
        readDataBoardingHouse();

        editTextName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Editable e = editTextAddress.getText();
                    Selection.setSelection(e, editTextAddress.getText().length());

                }
                return false;
            }
        });
        editTextAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    Editable e = editTextPhoneNumber.getText();
                    Selection.setSelection(e, editTextPhoneNumber.getText().length());
                }
                return false;
            }
        });
        editTextPhoneNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editTextPhoneNumber.clearFocus();
                }
                return false;
            }
        });
        imageButtonMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getContext(), imageButtonMenu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_popup_in_af, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.a_item_log_out:
                        SharedPreferences.Editor editor = Objects.requireNonNull(getContext()).getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).edit();
                        editor.clear();
                        editor.apply();

                        FirebaseAuth.getInstance().signOut();

                        Fragment fragment = new LogInFragment();
                        FragmentTransaction fragmentTransaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, fragment);
                        fragmentTransaction.commit();

                        return true;
                    case R.id.a_item_create_boarding_house:
                        BoardingHouse boardingHouse = new BoardingHouse();
                        boardingHouse.setIdOwnerBoardingHouse(landlord.getIdLandlord());
                        Bundle bundle2 = new Bundle();
                        bundle2.putSerializable("boardingHouse", boardingHouse);
                        Intent intent = new Intent(getContext(), CreateBoardingHouseActivity.class);
                        intent.putExtras(bundle2);
                        startActivityForResult(intent, REQUEST_CODE_FROM_ACCOUNT_FRAGMENT);
                        return true;
                    case R.id.a_item_setting_account:
                        expandLayoutSetting.expand();
                        editTextName.setEnabled(true);
                        editTextName.requestFocus();
                        Editable e = editTextName.getText();
                        Selection.setSelection(e, editTextName.getText().length());
                        // show keyboard
                        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        // hide keyboard
//                                InputMethodManager imm = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        editTextAddress.setEnabled(true);
                        editTextPhoneNumber.setEnabled(true);
                }
                return false;
            });
            popupMenu.show();

        });
        buttonCancelSetting.setOnClickListener(v -> {
            expandLayoutSetting.collapse();
            editTextName.setEnabled(false);
            editTextAddress.setEnabled(false);
            editTextPhoneNumber.setEnabled(false);
            editTextEmail.setEnabled(false);
            editTextPassword.setEnabled(false);
//                readDataLandlord(landlord);
        });
        buttonSaveSetting.setOnClickListener(v -> {
            expandLayoutSetting.collapse();
            Map<String, Object> update = new HashMap<>();
            update.put("name", editTextName.getText().toString().trim());

            update.put("address", editTextAddress.getText().toString().trim());
            update.put("phoneNumber", editTextPhoneNumber.getText().toString().trim());
            update.put("email", editTextEmail.getText().toString().trim());
            update.put("password", editTextPassword.getText().toString().trim());
            FirebaseFirestore.getInstance().collection("landlord").document(landlord.getIdLandlord()).update(update);
            editTextName.setEnabled(false);
            editTextAddress.setEnabled(false);
            editTextPhoneNumber.setEnabled(false);
            editTextEmail.setEnabled(false);
            editTextPassword.setEnabled(false);

            landlord.setNameLandlord(editTextName.getText().toString().trim());
            landlord.setAddressLandlord(editTextAddress.getText().toString().trim());
            landlord.setPhoneNumberLandlord(editTextPhoneNumber.getText().toString().trim());
            landlord.setEmailLandlord(editTextEmail.getText().toString().trim());
            landlord.setPasswordLandlord(editTextPassword.getText().toString().trim());
            SharedPreferences.Editor editor = Objects.requireNonNull(getContext()).getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.putString("id", landlord.getIdLandlord());
            editor.putString("name", landlord.getNameLandlord());
            editor.putString("address", landlord.getAddressLandlord());
            editor.putString("phoneNumber", landlord.getPhoneNumberLandlord());
            editor.putString("email", landlord.getEmailLandlord());
            editor.putString("password", landlord.getPasswordLandlord());
            editor.apply();
            Toast.makeText(getContext(), "Chỉnh sửa thông tin thành công", Toast.LENGTH_SHORT).show();
        });
        FirebaseFirestore.getInstance().collection("boardingHouse").addSnapshotListener((value, error) -> readDataBoardingHouse());
        return view;
    }


    private void findView(View view) {
        imageButtonMenu = view.findViewById(R.id.a_imageButton_menu);
        editTextName = view.findViewById(R.id.a_editText_name);
        editTextAddress = view.findViewById(R.id.a_editText_address);
        editTextPhoneNumber = view.findViewById(R.id.a_editText_phoneNumber);
        editTextEmail = view.findViewById(R.id.a_editText_email);
        editTextPassword = view.findViewById(R.id.a_editText_password);
        expandLayoutSetting = view.findViewById(R.id.a_expand_layout_setting);
        buttonSaveSetting = view.findViewById(R.id.a_button_save_setting);
        buttonCancelSetting = view.findViewById(R.id.a_button_cancel_setting);
        recyclerViewBoardingHouse = view.findViewById(R.id.recyclerViewBoardingHouse);
    }

    private void setTextDataLandlord(Landlord landlord) {

        editTextName.setText(landlord.getNameLandlord());
        editTextAddress.setText(landlord.getAddressLandlord());
        editTextPhoneNumber.setText(landlord.getPhoneNumberLandlord());
        editTextEmail.setText(landlord.getEmailLandlord());
        editTextPassword.setText(landlord.getPasswordLandlord());
    }

    private void readDataBoardingHouse() {
        ArrayList<BoardingHouse> arrayList = new ArrayList();
        BoardingHouseAdapter adapter = new BoardingHouseAdapter(getContext(), arrayList);
        recyclerViewBoardingHouse.setAdapter(adapter);
        FirebaseFirestore.getInstance().collection("boardingHouse").whereEqualTo("owner", landlord.getIdLandlord()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()).getDocuments()) {
                            BoardingHouse boardingHouse = new BoardingHouse();
                            boardingHouse.setNameBoardingHouse(documentSnapshot.getString("name"));
                            boardingHouse.setAddressBoardingHouse(documentSnapshot.getString("address"));
                            boardingHouse.setDistanceBoardingHouse(documentSnapshot.getDouble("distance"));
                            boardingHouse.setIdBoardingHouse(documentSnapshot.getId());
                            boardingHouse.setDescriptionBoardingHouse(documentSnapshot.getString("description"));
                            arrayList.add(boardingHouse);
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FROM_ACCOUNT_FRAGMENT && resultCode == CreateBoardingHouseActivity.RESULT_CODE_FROM_CREATE_BOARDING_HOUSE) {
            readDataBoardingHouse();
        }
        if (requestCode == REQUEST_CODE_FROM_ACCOUNT_FRAGMENT) {
            if (resultCode == AccountSettingActivity.RESULT_CODE_SETTING_ACCOUNT) {
                Toast.makeText(getContext(), "Setting", Toast.LENGTH_SHORT).show();
            }
        }
    }
}