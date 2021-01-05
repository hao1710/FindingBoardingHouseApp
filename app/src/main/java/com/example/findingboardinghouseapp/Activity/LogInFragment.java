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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.findingboardinghouseapp.Model.Admin;
import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LogInFragment extends Fragment {

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // code below
    ProgressBar progressBar;
    TextInputLayout tilEmail, tilPassword;
    TextInputEditText edtEmail, edtPassword;
    TextView tvCreateAccount;
    Button buttonLogIn;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    public SharedPreferences sharedPreferences;
    public static final String MY_PREFERENCES = "MyPre";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        findView(view);

        // initial
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // underline textView
        String create = "Bạn chưa có tài khoản? Đăng kí ngay";
        SpannableString spannableString = new SpannableString(create);
        spannableString.setSpan(new UnderlineSpan(), 0, create.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCreateAccount.setText(spannableString);

        // sharedPreferences
        sharedPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        // do something
        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CreateAccountActivity.class);
            startActivity(intent);
        });

        edtPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edtPassword.clearFocus();
            }
            return false;
        });
        buttonLogIn.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if (!validateEmail(email) | !validatePassword(password)) {
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                        if (firebaseAuth.getCurrentUser().getEmail().equals("anhhaovo1710@gmail.com")) {
                            firebaseFirestore.collection("admin").whereEqualTo("email", email)
                                    .whereEqualTo("password", password).get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            for (DocumentSnapshot documentSnapshot : task1.getResult().getDocuments()) {
                                                Admin admin = new Admin();
                                                admin.setIdAdmin(documentSnapshot.getId());
                                                admin.setEmailAdmin(email);
                                                admin.setPasswordAdmin(password);

                                                SharedPreferences.Editor editor = getContext().getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE).edit();
                                                editor.putString("id", admin.getEmailAdmin());
                                                editor.putString("email", email);
                                                editor.putString("password", password);
                                                editor.apply();

                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("admin", admin);

                                                Fragment fragment = new AdminFragment();
                                                fragment.setArguments(bundle);
                                                FragmentTransaction fragmentTransaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                                fragmentTransaction.replace(R.id.main_frameLayout, fragment);
                                                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                                fragmentTransaction.commit();
                                            }
                                        }
                                    });
                        } else {
                            firebaseFirestore.collection("landlord").whereEqualTo("email", email)
                                    .whereEqualTo("password", password).get()
                                    .addOnCompleteListener(task12 -> {
                                        if (task12.isSuccessful()) {
                                            for (DocumentSnapshot documentSnapshot : task12.getResult().getDocuments()) {
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
                                                editor.apply();

                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("landlord", landlord);

                                                Fragment fragment = new AccountFragment();
                                                fragment.setArguments(bundle);
                                                FragmentTransaction fragmentTransaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
                                                fragmentTransaction.replace(R.id.main_frameLayout, fragment);
                                                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                                fragmentTransaction.commit();
                                            }
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "Vui lòng xác nhận địa chỉ email", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Sai email hoặc mật khẩu, vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                }
            });
        });
        return view;
    }

    private void findView(View view) {
        progressBar = view.findViewById(R.id.li_progressBar);

        tilEmail = view.findViewById(R.id.li_til_email);
        tilPassword = view.findViewById(R.id.li_til_password);

        edtEmail = view.findViewById(R.id.li_edt_email);
        edtPassword = view.findViewById(R.id.li_edt_password);

        buttonLogIn = view.findViewById(R.id.li_button_log_in);

        tvCreateAccount = view.findViewById(R.id.li_tv_create_account);
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng điền email");
            return false;
        } else {
            tilEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng điền mật khẩu");
            return false;
        } else {
            tilPassword.setError(null);
            return true;
        }
    }
}