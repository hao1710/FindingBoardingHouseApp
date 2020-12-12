package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class UpdateAccountActivity extends AppCompatActivity {
    private TextInputLayout textInputName, textInputAddress, textInputPhoneNumber, textInputEmail, textInputPassword;
    private TextInputEditText textInputEditTextName, textInputEditTextAddress, textInputEditTextPhoneNumber, textInputEditTextPassword;
    private Button buttonUpdate;
    private ProgressBar progressBar;
    private Landlord landlord;

    public static final String MY_PREFERENCES = "MyPre";
    public static int RESULT_CODE_FROM_UPDATE_ACCOUNT = 31;

    @SuppressLint("TimberArgCount")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        findView();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        landlord = (Landlord) bundle.getSerializable("landlordUpdate");

        // setText
        Objects.requireNonNull(textInputName.getEditText()).setText(landlord.getNameLandlord());
        Objects.requireNonNull(textInputAddress.getEditText()).setText(landlord.getAddressLandlord());
        Objects.requireNonNull(textInputPhoneNumber.getEditText()).setText(landlord.getPhoneNumberLandlord());
        Objects.requireNonNull(textInputEmail.getEditText()).setText(landlord.getEmailLandlord());
        Objects.requireNonNull(textInputPassword.getEditText()).setText(landlord.getPasswordLandlord());

        setOnEditorAction();

        buttonUpdate.setOnClickListener(v -> {
            String name = Objects.requireNonNull(textInputName.getEditText()).getText().toString().trim();
            String address = Objects.requireNonNull(textInputAddress.getEditText()).getText().toString().trim();
            String phoneNumber = Objects.requireNonNull(textInputPhoneNumber.getEditText()).getText().toString().trim();
            String email = Objects.requireNonNull(textInputEmail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(textInputPassword.getEditText()).getText().toString().trim();
            if (!validateName(name) | !validateEmail(email) | !validatePhoneNumber(phoneNumber) | !validatePassword(password) | !validateAddress(address)) {
                return;
            }
            progressBar.setVisibility(View.VISIBLE);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            user.updatePassword(password).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Map<String, Object> update = new HashMap<>();
                    update.put("name", name);
                    update.put("address", address);
                    update.put("phoneNumber", phoneNumber);
                    update.put("email", email);
                    update.put("password", password);
                    FirebaseFirestore.getInstance().collection("landlord").document(landlord.getIdLandlord()).update(update);

                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFERENCES, MODE_PRIVATE).edit();
                    editor.clear();
                    editor.putString("id", landlord.getIdLandlord());
                    editor.putString("name", name);
                    editor.putString("address", address);
                    editor.putString("phoneNumber", phoneNumber);
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Cập nhật thông tin thất bại, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
                    Log.i("ErrorWhy: ", task.getException().getMessage());
                }
            });
        });
    }

    private void setOnEditorAction() {
        textInputEditTextName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = textInputEditTextAddress.getText();
                Selection.setSelection(e, Objects.requireNonNull(textInputEditTextAddress.getText()).length());
            }
            return false;
        });
        textInputEditTextAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = textInputEditTextPhoneNumber.getText();
                Selection.setSelection(e, Objects.requireNonNull(textInputEditTextPhoneNumber.getText()).length());
            }
            return false;
        });
        textInputEditTextPhoneNumber.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = textInputEditTextPassword.getText();
                Selection.setSelection(e, Objects.requireNonNull(textInputEditTextPassword.getText()).length());
            }
            return false;
        });
        textInputEditTextPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                textInputEditTextPassword.clearFocus();
            }
            return false;
        });
    }

    private void findView() {
        textInputName = findViewById(R.id.ua_textInput_name);
        textInputAddress = findViewById(R.id.ua_textInput_address);
        textInputPhoneNumber = findViewById(R.id.ua_textInput_phoneNumber);
        textInputEmail = findViewById(R.id.ua_textInput_email);
        textInputPassword = findViewById(R.id.ua_textInput_password);

        textInputEditTextName = findViewById(R.id.ua_textInputEditText_name);
        textInputEditTextAddress = findViewById(R.id.ua_textInputEditText_address);
        textInputEditTextPhoneNumber = findViewById(R.id.ua_textInputEditText_phoneNumber);
        textInputEditTextPassword = findViewById(R.id.ua_textInputEditText_password);

        progressBar = findViewById(R.id.ua_progressBar);

        buttonUpdate = findViewById(R.id.ua_button_update);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CODE_FROM_UPDATE_ACCOUNT, intent);
        finish();
        super.onBackPressed();
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