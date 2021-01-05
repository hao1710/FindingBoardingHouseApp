package com.example.findingboardinghouseapp.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findingboardinghouseapp.Adapter.SelectImageAdapter;
import com.example.findingboardinghouseapp.Model.Room;
import com.example.findingboardinghouseapp.Model.RoomCRUD;
import com.example.findingboardinghouseapp.Model.RoomImage;
import com.example.findingboardinghouseapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateRoomActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_FROM_CREATE_ROOM = 29;
    public static final int RESULT_CODE_FROM_CREATE_ROOM = 28;

    private Room room;

    private StorageReference storageReference;

    ImageButton ibBack;
    TextInputLayout tilName;
    TextInputEditText edtName;
    Button buttonSelectImage;
    RecyclerView rvRoomImage;
    Button buttonCreateRoom;

    ProgressDialog progressDialog;

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
        ibBack = findViewById(R.id.cr_ib_back);
        tilName = findViewById(R.id.cr_til_name);
        edtName = findViewById(R.id.cr_edt_name);
        buttonSelectImage = findViewById(R.id.cr_button_select_image);
        rvRoomImage = findViewById(R.id.cr_rv_roomImage);
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

            String name = edtName.getText().toString().trim();

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
        edtName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                edtName.clearFocus();
            }
            return false;
        });

        ibBack.setOnClickListener(v -> finish());
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Vui lòng đợi");
        progressDialog.show();
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
                            progressDialog.dismiss();
                            RoomCRUD roomCRUD = new RoomCRUD();
                            roomCRUD.setStatus(false);
                            roomCRUD.setImage(linkImage);
                            Map<String, RoomCRUD> newRoom = new HashMap<>();
                            newRoom.put(edtName.getText().toString().trim(), roomCRUD);
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
                            Toast.makeText(getApplicationContext(), "Thêm phòng mới thành công", Toast.LENGTH_SHORT).show();
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
            roomImageList.clear();
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
            tilName.setError("Vui lòng điền tên phòng");
            return false;
        } else {
            tilName.setError(null);
            return true;
        }
    }

}