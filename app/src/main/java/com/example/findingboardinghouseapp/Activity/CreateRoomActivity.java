package com.example.findingboardinghouseapp.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.Model.RoomCRUD;
import com.example.findingboardinghouseapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class CreateRoomActivity extends AppCompatActivity {
    private TextInputLayout textInputName, textInputDescription;
    public static final int REQUEST_CODE_FROM_CREATE_ROOM = 29;
    public static final int RESULT_CODE_FROM_CREATE_ROOM = 28;
    private Room room;

    private Uri uriImage;
    private ImageView imageView;
    private ProgressBar progressBar;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        room = (Room) bundle.getSerializable("newRoom");

        // mapping
        textInputName = findViewById(R.id.cr_textInput_name);

        Button buttonSelectImage = findViewById(R.id.cr_button_select_image);
        Button buttonCreateRoom = findViewById(R.id.cr_button_create_room);
        progressBar = findViewById(R.id.cr_progressBar);
        imageView = findViewById(R.id.cr_imageView_room);

        // do something
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonCreateRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = textInputName.getEditText().getText().toString().trim();

                if (!validateName(name)) {
                    return;
                }
                uploadFile();
            }
        });
        storageReference = FirebaseStorage.getInstance().getReference("image");

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (uriImage != null) {
            StorageReference file = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(uriImage));
            file.putFile(uriImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(100);
                                }
                            }, 500);

                            file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    RoomCRUD roomCRUD = new RoomCRUD();
                                    roomCRUD.setStatus(false);
                                    roomCRUD.setImage(uri.toString());
                                    Map<String, RoomCRUD> newRoom = new HashMap<>();
                                    newRoom.put(textInputName.getEditText().getText().toString().trim(), roomCRUD);
                                    Map<String, Map> create = new HashMap<>();
                                    create.put("room", newRoom);

                                    FirebaseFirestore.getInstance().collection("boardingHouse").document(room.getIdBoardingHouse()).collection("roomType").document(room.getIdRoomType())
                                            .set(create, SetOptions.merge());
                                }
                            });
                            Toast.makeText(getApplicationContext(), "Thêm phòng mới thành công!", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            Double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progressBar.setProgress(progress.intValue());
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Chưa chọn hình ảnh nhà trọ", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_FROM_CREATE_ROOM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FROM_CREATE_ROOM && resultCode == RESULT_OK && data.getData() != null && data != null) {
            uriImage = data.getData();
            Picasso.with(this).load(uriImage).into(imageView);
        }
    }

    private boolean validateName(String name) {
        if (name.isEmpty()) {
            textInputName.setError("Vui lòng điền tên phòng");
            return false;
        } else {
            textInputName.setError(null);
//            textInputEmail.setEnabled(false);
            return true;
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        room.setNameRoom("aka");
        bundle.putSerializable("newRoom", room);
        intent.putExtra("a", bundle);
        setResult(RESULT_CODE_FROM_CREATE_ROOM, intent);
        finish();
        super.onBackPressed();
    }
}