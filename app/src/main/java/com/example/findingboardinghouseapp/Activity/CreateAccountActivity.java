package com.example.findingboardinghouseapp.Activity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.LandlordCRUD;
import com.example.findingboardinghouseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateAccountActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextInputLayout textInputName, textInputAddress, textInputPhoneNumber, textInputEmail, textInputPassword;
    private Button buttonCreateAccount;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        findView();


        // do something
        textInputPassword.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    textInputPassword.getEditText().clearFocus();
                }
                return false;
            }
        });

        buttonCreateAccount.setOnClickListener(v -> {
            firebaseAuth = FirebaseAuth.getInstance();
            String name = textInputName.getEditText().getText().toString().trim();
            String address = textInputAddress.getEditText().getText().toString().trim();
            String phoneNumber = textInputPhoneNumber.getEditText().getText().toString().trim();
            String email = textInputEmail.getEditText().getText().toString().trim();
            String password = textInputPassword.getEditText().getText().toString().trim();
            if (!validateName(name) | !validateEmail(email) | !validatePhoneNumber(phoneNumber) | !validatePassword(password) | !validateAddress(address)) {
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                LandlordCRUD landlord = new LandlordCRUD();
                                landlord.setName(name);
                                landlord.setAddress(address);
                                landlord.setPhoneNumber(phoneNumber);
                                landlord.setPassword(password);
                                landlord.setEmail(email);
                                FirebaseFirestore.getInstance().collection("landlord").add(landlord);
                                Toast.makeText(CreateAccountActivity.this, "Đăng ký thành công, vui lòng kiểm tra email và xác nhận", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CreateAccountActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(CreateAccountActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void findView() {
        progressBar = findViewById(R.id.ca_progressBar);

        textInputName = findViewById(R.id.ca_textInput_name);
        textInputAddress = findViewById(R.id.ca_textInput_address);
        textInputPhoneNumber = findViewById(R.id.ca_textInput_phoneNumber);
        textInputEmail = findViewById(R.id.ca_textInput_email);
        textInputPassword = findViewById(R.id.ca_textInput_password);

        buttonCreateAccount = findViewById(R.id.ca_button_create);
    }

    private boolean validateName(String name) {
        if (name.isEmpty()) {
            textInputName.setError("Vui lòng điền họ tên");
            return false;
        } else {
            textInputName.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        }
    }

    private boolean validateAddress(String address) {
        if (address.isEmpty()) {
            textInputAddress.setError("Vui lòng điền địa chỉ");
            return false;
        } else {
            textInputAddress.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        }
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            textInputPhoneNumber.setError("Vui lòng điền số điền thoại");
            return false;
        } else if (phoneNumber.length() > 11 || phoneNumber.length() < 10 || !phoneNumber.startsWith("0")) {
            textInputPhoneNumber.setError("Vui lòng điền đúng số điện thoại");
            return false;

        } else {
            textInputPhoneNumber.setError(null);
            return true;
        }
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            textInputEmail.setError("Vui lòng điền email");
            return false;
        }
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputEmail.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        } else {
            textInputEmail.setError("Vui lòng điền đúng định dạng email");
            return false;
        }
    }


    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            textInputPassword.setError("Vui lòng điền mật khẩu");
            return false;
        }
        if (password.length() < 6) {
            textInputPassword.setError("Mật khẩu phải có ít nhất 6 kí tự");
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }
}