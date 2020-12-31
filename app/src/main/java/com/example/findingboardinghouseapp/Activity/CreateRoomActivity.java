package com.example.findingboardinghouseapp.Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.SelectImageAdapter;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.Model.RoomCRUD;
import com.example.findingboardinghouseapp.Model.RoomImage;
import com.example.findingboardinghouseapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateRoomActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_FROM_CREATE_ROOM = 29;
    public static final int RESULT_CODE_FROM_CREATE_ROOM = 28;
    private Room room;

    private StorageReference storageReference;

    TextInputLayout textInputName;
    TextInputEditText editTextName;
    Button buttonSelectImage;
    RecyclerView rvRoomImage;
    ProgressBar progressBar;
    Button buttonCreateRoom;

    ArrayList<RoomImage> roomImageList;

    SelectImageAdapter selectImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        room = (Room) bundle.getSerializable("newRoom");

        // findView
        textInputName = findViewById(R.id.cr_textInput_name);
        editTextName = findViewById(R.id.cr_editText_name);
        buttonSelectImage = findViewById(R.id.cr_button_select_image);
        rvRoomImage = findViewById(R.id.cr_rv_roomImage);
        progressBar = findViewById(R.id.cr_progressBar);
        buttonCreateRoom = findViewById(R.id.cr_button_create_room);

        // initial
        roomImageList = new ArrayList<>();
        selectImageAdapter = new SelectImageAdapter(CreateRoomActivity.this, roomImageList);


        // rv
        rvRoomImage.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(CreateRoomActivity.this, 2);
        rvRoomImage.setLayoutManager(gridLayoutManager);
        rvRoomImage.setAdapter(selectImageAdapter);


        // do something
        buttonSelectImage.setOnClickListener(v -> {
            Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
            intent1.setType("image/*");
            intent1.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent1, "Chọn"), REQUEST_CODE_FROM_CREATE_ROOM);
        });

        buttonCreateRoom.setOnClickListener(v -> {

            String name = Objects.requireNonNull(textInputName.getEditText()).getText().toString().trim();

            if (!validateName(name)) {
                return;
            }
            if (roomImageList.size() == 0) {
                Toast.makeText(getApplicationContext(), "Chưa chọn hình ảnh nhà trọ", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadFile();


        });
        storageReference = FirebaseStorage.getInstance().getReference("image");

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        progressBar.setVisibility(View.VISIBLE);
        ArrayList<String> linkImage = new ArrayList<>();
        final int[] count = {0};
        for (int i = 0; i < roomImageList.size(); i++) {
            if (roomImageList.get(i).getUri() != null) {
                StorageReference file = storageReference.child(System.currentTimeMillis()
                        + "." + getFileExtension(roomImageList.get(i).getUri()));
                file.putFile(roomImageList.get(i).getUri()).addOnSuccessListener(taskSnapshot -> {
//                                    Handler handler = new Handler();
//                                    handler.postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            progressBar.setProgress(100);
//                                        }
//                                    }, 500);

                    file.getDownloadUrl().addOnSuccessListener(uri -> {
                        count[0]++;
//                            RoomCRUD roomCRUD = new RoomCRUD();
//                            roomCRUD.setStatus(false);
//                            roomCRUD.setImage(uri.toString());
//                            Map<String, RoomCRUD> newRoom = new HashMap<>();
//                            newRoom.put(textInputName.getEditText().getText().toString().trim(), roomCRUD);
//                            Map<String, Map> create = new HashMap<>();
//                            create.put("room", newRoom);
//
//                            FirebaseFirestore.getInstance().collection("boardingHouse").document(room.getIdBoardingHouse()).collection("roomType").document(room.getIdRoomType())
//                                    .set(create, SetOptions.merge());
                        linkImage.add(uri.toString());
                        if (count[0] == roomImageList.size()) {
                            progressBar.setVisibility(View.GONE);
                            RoomCRUD roomCRUD = new RoomCRUD();
                            roomCRUD.setStatus(false);
                            roomCRUD.setImage(linkImage);
                            Map<String, RoomCRUD> newRoom = new HashMap<>();
                            newRoom.put(Objects.requireNonNull(textInputName.getEditText()).getText().toString().trim(), roomCRUD);
                            Map<String, Map> create = new HashMap<>();
                            create.put("room", newRoom);
                            FirebaseFirestore.getInstance().collection("boardingHouse").document(room.getIdBoardingHouse()).collection("roomType").document(room.getIdRoomType())
                                    .set(create, SetOptions.merge());

                            Intent intent = new Intent();
                            Bundle bundle = new Bundle();
                            room.setNameRoom("aka");
                            bundle.putSerializable("newRoom", room);
                            intent.putExtra("a", bundle);
                            setResult(RESULT_CODE_FROM_CREATE_ROOM, intent);
                            finish();
                            Toast.makeText(getApplicationContext(), "Thêm phòng mới thành công!", Toast.LENGTH_SHORT).show();
                        }
                    });
//                    Toast.makeText(getApplicationContext(), "Thêm phòng mới thành công!", Toast.LENGTH_SHORT).show();

                })
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
//                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                                    Double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
//                                    progressBar.setProgress(progress.intValue());
//                                }
//                            });
            }
        }

//        RoomCRUD roomCRUD = new RoomCRUD();
//        roomCRUD.setStatus(false);
//        roomCRUD.setImage(linkImage);
//        Map<String, RoomCRUD> newRoom = new HashMap<>();
//        newRoom.put(textInputName.getEditText().getText().toString().trim(), roomCRUD);
//        Map<String, Map> create = new HashMap<>();
//        create.put("room", newRoom);
//
//        FirebaseFirestore.getInstance().collection("boardingHouse").document(room.getIdBoardingHouse()).collection("roomType").document(room.getIdRoomType())
//                .set(create, SetOptions.merge());
//        Toast.makeText(getApplicationContext(), "Thêm phòng mới thành công!", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FROM_CREATE_ROOM && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    RoomImage roomImage = new RoomImage();
                    roomImage.setUri(data.getClipData().getItemAt(i).getUri());
                    roomImageList.add(roomImage);
                }
                selectImageAdapter.notifyDataSetChanged();
            } else if (data.getData() != null) {

                RoomImage roomImage = new RoomImage();
                roomImage.setUri(data.getData());
                roomImageList.add(roomImage);

                selectImageAdapter.notifyDataSetChanged();
            }

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


        super.onBackPressed();
    }
}