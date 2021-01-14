package com.example.findingboardinghouseapp.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.Landlord;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class UpdateAccountActivity extends AppCompatActivity {
    TextInputLayout tilName, tilAddress, tilPhoneNumber, tilEmail, tilPassword;
    TextInputEditText edtName, edtAddress, edtPhoneNumber, edtEmail, edtPassword;
    Button buttonUpdate;

    ImageButton ibBack;
    ProgressDialog progressDialog;
    public static final String MY_PREFERENCES = "MyPre";
    public static int RESULT_CODE_FROM_UPDATE_ACCOUNT = 31;
    private Landlord landlord;

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
        edtName.setText(landlord.getNameLandlord());
        edtAddress.setText(landlord.getAddressLandlord());
        edtPhoneNumber.setText(landlord.getPhoneNumberLandlord());
        edtEmail.setText(landlord.getEmailLandlord());
        edtPassword.setText(landlord.getPasswordLandlord());

        setOnEditorAction();

        buttonUpdate.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String phoneNumber = edtPhoneNumber.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if (!validateName(name) | !validateEmail(email) | !validatePhoneNumber(phoneNumber) | !validatePassword(password) | !validateAddress(address)) {
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            AuthCredential credential = EmailAuthProvider
                    .getCredential(landlord.getEmailLandlord(), landlord.getPasswordLandlord());
            assert user != null;
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Vui lòng đợi");
                    progressDialog.show();
                    user.updatePassword(password).addOnCompleteListener(task1 -> {
                        progressDialog.dismiss();
                        if (task1.isSuccessful()) {
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
                            Intent intentResult = new Intent();
                            setResult(RESULT_CODE_FROM_UPDATE_ACCOUNT, intentResult);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                            Timber.i("Error: %s", Objects.requireNonNull(task1.getException()).getMessage());
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng đăng nhập lại để cập nhật thông tin", Toast.LENGTH_SHORT).show();
                    Timber.i("ErrorAu: %s", Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        });
        ibBack.setOnClickListener(v -> finish());
    }

    private void setOnEditorAction() {
        edtName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = edtAddress.getText();
                Selection.setSelection(e, Objects.requireNonNull(edtAddress.getText()).length());
            }
            return false;
        });
        edtAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = edtPhoneNumber.getText();
                Selection.setSelection(e, Objects.requireNonNull(edtPhoneNumber.getText()).length());
            }
            return false;
        });
        edtPhoneNumber.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Editable e = edtPassword.getText();
                Selection.setSelection(e, Objects.requireNonNull(edtPassword.getText()).length());
            }
            return false;
        });
        edtPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edtPassword.clearFocus();
            }
            return false;
        });
    }

    private void findView() {
        ibBack = findViewById(R.id.ua_ib_back);

        tilName = findViewById(R.id.ua_til_name);
        tilAddress = findViewById(R.id.ua_til_address);
        tilPhoneNumber = findViewById(R.id.ua_til_phoneNumber);
        tilEmail = findViewById(R.id.ua_til_email);
        tilPassword = findViewById(R.id.ua_til_password);

        edtName = findViewById(R.id.ua_edt_name);
        edtAddress = findViewById(R.id.ua_edt_address);
        edtPhoneNumber = findViewById(R.id.ua_edt_phoneNumber);
        edtEmail = findViewById(R.id.ua_edt_email);
        edtPassword = findViewById(R.id.ua_edt_password);

        buttonUpdate = findViewById(R.id.ua_button_update);
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
            tilEmail.setError("Vui lòng điền đúng email");
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