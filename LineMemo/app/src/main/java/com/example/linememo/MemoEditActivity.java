package com.example.linememo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MemoEditActivity extends AppCompatActivity {
    public static final int CREATE_MODE = 1001;
    public static final int MODIFY_MODE = 1002;
    public static final int CAMERA_REQUEST_CODE = 2001;
    public static final int GALLERY_REQUEST_CODE = 2002;

    private MemoViewModel viewModel;
    private EditText titleEdit;
    private EditText contentEdit;
    private RecyclerView imageRecyclerView;
    private ImageAdapter mAdapter;

    private int myViewMode;
    private Memo mMemoData;
    private List<String> mImageUris;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        initSetting();
        initImageRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memo_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (myViewMode == CREATE_MODE) {
                    viewModel.insert(new Memo(
                            titleEdit.getText().toString(),
                            contentEdit.getText().toString(),
                            mImageUris,
                            System.currentTimeMillis()));
                } else if (myViewMode == MODIFY_MODE) {
                    mMemoData.setTitle(titleEdit.getText().toString());
                    mMemoData.setContent(contentEdit.getText().toString());
                    mMemoData.setImageUri(mImageUris);
                    mMemoData.setDate(System.currentTimeMillis());
                    viewModel.update(mMemoData);
                }
                finish();
                return true;
            case R.id.addPhoto:
                createUploadDialog().show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void initSetting() {
        Intent intent = getIntent();
        myViewMode = intent.getIntExtra("mode", -1);
        mMemoData = (Memo) intent.getExtras().get("memoData");

        titleEdit = findViewById(R.id.titleEdit);
        contentEdit = findViewById(R.id.contentEdit);
        imageRecyclerView = findViewById(R.id.imageRecycler);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        if (myViewMode == CREATE_MODE) {
            myToolbar.setTitle("메모 작성");
            mImageUris = new ArrayList<>();
        } else if (myViewMode == MODIFY_MODE) {
            myToolbar.setTitle("메모 수정");
            titleEdit.setText(mMemoData.getTitle());
            contentEdit.setText(mMemoData.getContent());
            mImageUris = mMemoData.getImageUris();
        } else {
            // Todo 에러처리
        }
        setSupportActionBar(myToolbar);

        viewModel = new ViewModelProvider(this).get(MemoViewModel.class);
    }

    void initImageRecyclerView() {
        imageRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ImageAdapter(this, mImageUris, ImageAdapter.IMAGE_ADAPTER_EDIT_MODE);
        imageRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.e("MemoEdit", "onActivityResult RESULT OK");
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    Uri selectedImg = data.getData();
                    Log.e("MemoEdit-Result", data.getData().toString());
                    mAdapter.addImage(selectedImg.toString());
                    break;
                case CAMERA_REQUEST_CODE:
                    // 사용자가 카메라 intent에서 사진을 촬영하고 그것을 선택했다면 RESULT_OK이므로 이 곳에 진입
                    // RESULT_OK로 이곳에 진입했다는 것은 ImageAdapter에서 설정한 사진의 저장경로가 있다는 의미이므로
                    // getTakenPictureUri을 통해 새로 찍은 사진이 저장된 uri를 가져옴
                    mAdapter.addImage(mCurrentPhotoPath);
                    break;
            }
        } else {
            Log.e("MemoEdit", "onActivityResult RESULT NO");
        }
    }

    private Dialog createUploadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_image_title)
                .setItems(R.array.add_image_methods_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0: // 사진첩
                                goToGallery();
                                break;
                            case 1: // 카메라 촬영
                                goToCamera();
                                break;
                            case 2: // 외부 이미지 주소
                                createUriInputDialog().show();
                                break;
                        }
                    }
                });
        return builder.create();
    }

    private Dialog createUriInputDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_uri_input, null);
        builder.setView(v)
                .setCancelable(false)
                .setPositiveButton(R.string.positiveBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAdapter.addImage(getUrlStringFromView(v));
                    }
                })
                .setNegativeButton(R.string.negativeBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        return builder.create();
    }

    private void goToCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.linememo.fileprovider",
                        photoFile);
                Log.e("ImageAdapter", "photoURI = " + photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                takePictureIntent.putExtra("imgUri", photoURI);
                startActivityForResult(takePictureIntent, MemoEditActivity.CAMERA_REQUEST_CODE);
            }
        }
    }

    private void goToGallery() {
        /** 주의 -- Intent intent = new Intent(Intent.ACTION_PICK) 사용에 대한 문제점
         * ACTION_PICK 사용 시, 파일 uri를 받아올 때 '일시적인 권한'으로 접근할 수 있게 한다.(보안상의 이유로 안드로이드에서 의도적으로 한 것)
         * 이렇게 해서 얻은 uri를 통해 다른 컨텍스트에서 접근하려고 한다면, 액세스 불가능하다는 보안 에러가 뜬다.
         * 따라서, onActivityResult에서 전달받은 uri를 가지고 이미지 데이터를 가져와야 하는데,
         * 우리 앱은 로컬에 저장된 이미지를 보여주는 기능을 하는 것이기 때문에 채택하지 않았다.
         */
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        // 이미지 파일에 접근할 수 있는 chooser가 많다면, 사용자가 선택할 수 있게 한다
        startActivityForResult(Intent.createChooser(intent, "사진첩을 선택하세요"), MemoEditActivity.GALLERY_REQUEST_CODE);
    }

    private String getUrlStringFromView(View v) {
        EditText urlEdit = v.findViewById(R.id.uriEdit);
        return urlEdit.getText().toString().trim();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "LINEMEMO_" + timeStamp + "_";
        // getExternalFilesDir : LineMemo 앱 이외에는 비공개, 디렉터리에 저장한 파일은 사용자가 앱을 제거할 때 삭제됨
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e("ImageAdapter", "Image URI = " + mCurrentPhotoPath);
        return image;
    }
}
