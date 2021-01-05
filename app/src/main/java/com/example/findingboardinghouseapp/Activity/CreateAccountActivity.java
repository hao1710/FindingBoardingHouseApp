package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.LandlordCRUD;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateAccountActivity extends AppCompatActivity {

    ImageButton ibBack;
    ProgressDialog progressDialog;
    TextInputLayout tilName, tilAddress, tilPhoneNumber, tilEmail, tilPassword;
    TextInputEditText edtName, edtAddress, edtPhoneNumber, edtEmail, edtPassword;
    Button buttonCreateAccount;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        findView();

        // do something
        edtPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edtPassword.clearFocus();
            }
            return false;
        });

        ibBack.setOnClickListener(v -> finish());

        buttonCreateAccount.setOnClickListener(v -> {
            firebaseAuth = FirebaseAuth.getInstance();
            String name = edtName.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if (!validateName(name) | !validateEmail(email) | !validatePhoneNumber(phoneNumber) | !validatePassword(password) | !validateAddress(address)) {
                return;
            }
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Vui lòng đợi");
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
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
                            Toast.makeText(CreateAccountActivity.this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(CreateAccountActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onStart() {
        Log.i("LifeCycle", "onStart");
        super.onStart();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onResume() {
        Log.i("LifeCycle", "onResume");
        super.onResume();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onPause() {
        Log.i("LifeCycle", "onPause");
        super.onPause();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onStop() {
        Log.i("LifeCycle", "onStop");
        super.onStop();
    }

    @SuppressLint("LogNotTimber")
    @Override
    protected void onDestroy() {
        Log.i("LifeCycle", "onDestroy");
        super.onDestroy();
    }

    private void findView() {
        ibBack = findViewById(R.id.ca_ib_back);

        tilName = findViewById(R.id.ca_til_name);
        tilAddress = findViewById(R.id.ca_til_address);
        tilPhoneNumber = findViewById(R.id.ca_til_phoneNumber);
        tilEmail = findViewById(R.id.ca_til_email);
        tilPassword = findViewById(R.id.ca_til_password);

        edtName = findViewById(R.id.ca_edt_name);
        edtAddress = findViewById(R.id.ca_edt_address);
        edtPhoneNumber = findViewById(R.id.ca_edt_phoneNumber);
        edtEmail = findViewById(R.id.ca_edt_email);
        edtPassword = findViewById(R.id.ca_edt_password);
        buttonCreateAccount = findViewById(R.id.ca_button_create);
    }

    private boolean validateName(String name) {
        if (name.isEmpty()) {
            tilName.setError("Vui lòng điền họ tên");
            return false;
        } else {
            tilName.setError(null);
            return true;
        }
    }

    private boolean validateAddress(String address) {
        if (address.isEmpty()) {
            tilAddress.setError("Vui lòng điền địa chỉ");
            return false;
        } else {
            tilAddress.setError(null);
            return true;
        }
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            tilPhoneNumber.setError("Vui lòng điền số điền thoại");
            return false;
        } else if (phoneNumber.length() > 11 || phoneNumber.length() < 10 || !phoneNumber.startsWith("0")) {
            tilPhoneNumber.setError("Vui lòng điền đúng số điện thoại");
            return false;

        } else {
            tilPhoneNumber.setError(null);
            return true;
        }
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            tilEmail.setError("Vui lòng điền email");
            return false;
        }
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(null);
            return true;
        } else {
            tilEmail.setError("Vui lòng điền đúng định dạng email");
            return false;
        }
    }


    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            tilPassword.setError("Vui lòng điền mật khẩu");
            return false;
        }
        if (password.length() < 7) {
            tilPassword.setError("Mật khẩu phải có ít nhất 7 kí tự");
            return false;
        } else {
            tilPassword.setError(null);
            return true;
        }
    }
}