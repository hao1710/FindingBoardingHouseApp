package com.example.findingboardinghouseapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogInFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LogInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogInFragment newInstance(String param1, String param2) {
        LogInFragment fragment = new LogInFragment();
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
    private TextView textViewEmail, textViewPassword;

    private FirebaseFirestore firebaseFirestore;
    public SharedPreferences sharedPreferences;
    public static final String MY_PREFERENCES = "MyPre";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        // mapping
        textViewEmail = view.findViewById(R.id.li_editText_email);
        textViewPassword = view.findViewById(R.id.li_editText_password);
        Button buttonLogIn = view.findViewById(R.id.li_btn_login);
        TextView textViewCreateAccount = view.findViewById(R.id.li_edt_create);

        // underline textView
        String create = "Bạn chưa có tài khoản? Đăng kí ngay";
        SpannableString spannableString = new SpannableString(create);
        spannableString.setSpan(new UnderlineSpan(), 0, create.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textViewCreateAccount.setText(spannableString);

        textViewCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        // initial
        firebaseFirestore = FirebaseFirestore.getInstance();
        sharedPreferences = this.getActivity().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        buttonLogIn.setOnClickListener(v -> {
            String email = textViewEmail.getText().toString();
            String password = textViewPassword.getText().toString();
            firebaseFirestore.collection("landlord").whereEqualTo("email", email).whereEqualTo("password", password).get()
                    .addOnCompleteListener(task -> {
                        if (task.getResult().size() > 0) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                Landlord landlord = new Landlord();
                                landlord.setIdLandlord(documentSnapshot.getId());
                                landlord.setNameLandlord(documentSnapshot.getString("name"));
                                landlord.setAddressLandlord(documentSnapshot.getString("address"));
                                landlord.setPhoneNumberLandlord(documentSnapshot.getString("phoneNumber"));
                                landlord.setEmailLandlord(email);
                                landlord.setPasswordLandlord(password);

                                SharedPreferences.Editor editor = getContext().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).edit();
                                editor.putString("id", landlord.getIdLandlord());
                                editor.putString("name", landlord.getNameLandlord());
                                editor.putString("address", landlord.getAddressLandlord());
                                editor.putString("phoneNumber", landlord.getPhoneNumberLandlord());
                                editor.putString("email", email);
                                editor.putString("password", password);

                                editor.commit();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("landlord", landlord);

                                Fragment fragment = new AccountFragment();
                                fragment.setArguments(bundle);
                                FragmentTransaction fragmentTransaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.frame_layout, fragment);
                                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                fragmentTransaction.commit();
                            }
                        } else {
                            Toast.makeText(getContext(), "Sai email hoặc mật khẩu, vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        return view;
    }
}