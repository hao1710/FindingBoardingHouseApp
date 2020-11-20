package com.example.findingboardinghouseapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.Model.LandlordCRUD;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {
    private TextInputLayout textInputName, textInputAddress, textInputPhoneNumber, textInputEmail, textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // mapping
        textInputName = findViewById(R.id.ca_text_input_name);
        textInputAddress = findViewById(R.id.ca_text_input_address);
        textInputPhoneNumber = findViewById(R.id.ca_text_input_phone_number);
        textInputEmail = findViewById(R.id.ca_text_input_email);
        textInputPassword = findViewById(R.id.ca_text_input_password);
        Button buttonCreateAccount = findViewById(R.id.ca_button_create_account);


        // do something


        // onClick
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = textInputName.getEditText().getText().toString().trim();
                String address = textInputAddress.getEditText().getText().toString().trim();
                String phoneNumber = textInputPhoneNumber.getEditText().getText().toString().trim();
                String email = textInputEmail.getEditText().getText().toString().trim();
                String password = textInputPassword.getEditText().getText().toString().trim();
                if (!validateName(name) | !validateEmail(email) | !validatePhoneNumber(phoneNumber) | !validatePassword(password) | !validateAddress(address)) {
                    return;
                }
                LandlordCRUD landlord = new LandlordCRUD();
                landlord.setName(name);
                landlord.setAddress(address);
                landlord.setPhoneNumber(phoneNumber);
                landlord.setPassword(password);
                landlord.setEmail(email);
                FirebaseFirestore.getInstance().collection("landlord").add(landlord);
                Toast.makeText(getApplicationContext(), "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
            }
        });
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
            Toast.makeText(getApplicationContext(), phoneNumber, Toast.LENGTH_SHORT).show();
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
        } else {
            textInputPassword.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        }
    }
}