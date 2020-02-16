package com.example.linememo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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

    private static final String TAG = "MemoEditActivity";

    private MemoViewModel viewModel;
    private LinearLayout imageAreaNoti;
    private EditText titleEdit;
    private EditText contentEdit;
    private Button saveButton;
    private RecyclerView imageRecyclerView;
    private ImageAdapter mAdapter;

    private int divider;
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

    void initSetting() {
        Intent intent = getIntent();
        myViewMode = intent.getIntExtra("mode", -1);
        mMemoData = (Memo) intent.getExtras().get("memoData");

        imageAreaNoti = findViewById(R.id.image_area_noti);
        titleEdit = findViewById(R.id.title_edit);
        contentEdit = findViewById(R.id.content_edit);
        imageRecyclerView = findViewById(R.id.image_recycler);
        saveButton = findViewById(R.id.save_button);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        if (myViewMode == CREATE_MODE) {
            myToolbar.setTitle("새 메모 쓰기");
            changeSaveButtonState(false);
            mImageUris = new ArrayList<>();
            imageAreaNoti.setVisibility(View.VISIBLE);
        } else if (myViewMode == MODIFY_MODE) {
            myToolbar.setTitle("메모 수정하기");
            titleEdit.setText(mMemoData.getTitle());
            contentEdit.setText(mMemoData.getContent());
            mImageUris = mMemoData.getImageUris();
            if (mImageUris.isEmpty()) imageAreaNoti.setVisibility(View.VISIBLE);
            else imageAreaNoti.setVisibility(View.GONE);
        } else {
            // Todo 에러처리
        }
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_24dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMemoData();
                setResult(RESULT_OK);
                onBackPressed();
            }
        });
        imageAreaNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUploadDialog();
            }
        });

        titleEdit.addTextChangedListener(editTextChangeListener);
        titleEdit.requestFocus();

        viewModel = new ViewModelProvider(this).get(MemoViewModel.class);
        divider = AndroidUtil.dpToPx(this, 5);
    }

    void initImageRecyclerView() {
        imageRecyclerView.setHasFixedSize(true);
        imageRecyclerView.addItemDecoration(imageItemDecoration);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ImageAdapter(this, mImageUris);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.e(TAG, "mAdapter onChanged and getItemCount = " + mAdapter.getItemCount());
                if (mAdapter.getItemCount() <= 1) {
                    if (titleEdit.length() == 0)
                        changeSaveButtonState(false);
                    imageAreaNoti.setVisibility(View.VISIBLE);
                } else {
                    changeSaveButtonState(true);
                    imageAreaNoti.setVisibility(View.GONE);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                    }
                }, 600);
            }
        });
        imageRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.e(TAG, "onActivityResult RESULT OK");
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    Uri selectedImg = data.getData();
                    Log.e(TAG, data.getData().toString());
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
            Log.e(TAG, "onActivityResult RESULT NO");
        }
    }

    public void createUploadDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.add_image_title)
                .setIcon(R.drawable.ic_attach_file_24dp)
                .setItems(R.array.add_image_methods_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // 사진첩
                                goToGallery();
                                break;
                            case 1: // 카메라 촬영
                                goToCamera();
                                break;
                            case 2: // 외부 이미지 주소
                                createUriInputDialog();
                                break;
                        }
                    }
                })
                .show();
    }

    private void createUriInputDialog() {
        final View v = getLayoutInflater().inflate(R.layout.dialog_uri_input, null);
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this)
                .setView(v)
                .setCancelable(false)
                .setPositiveButton(R.string.positiveBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAdapter.addImage(getUrlStringFromEdit((EditText) v.findViewById(R.id.uri_input)));
                    }
                })
                .setNegativeButton(R.string.negativeBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        final AlertDialog alertDialog = materialAlertDialogBuilder.show();
        final Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setEnabled(false);

        EditText uriInput = v.findViewById(R.id.uri_input);
        uriInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0)
                    positiveButton.setEnabled(false);
                else
                    positiveButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                Log.e(TAG, "photoURI = " + photoURI.toString());
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

    private String getUrlStringFromEdit(EditText urlEdit) {
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
        Log.e(TAG, "Image URI = " + mCurrentPhotoPath);
        return image;
    }

    private void saveMemoData() {
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
    }

    private TextWatcher editTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().trim().length() == 0 && mImageUris.isEmpty())
                changeSaveButtonState(false);
            else
                changeSaveButtonState(true);
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private RecyclerView.ItemDecoration imageItemDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getPaddingLeft() != divider) {
                parent.setPadding(divider, 0, divider, 0);
                parent.setClipToPadding(false);
            }
            outRect.left = divider;
            outRect.right = divider;
        }
    };

    private void changeSaveButtonState(boolean clickable) {
        if (clickable) {
            saveButton.setEnabled(true);
            saveButton.setBackground(getResources().getDrawable(R.drawable.green_button));
        } else {
            saveButton.setEnabled(false);
            saveButton.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (myViewMode == CREATE_MODE)
            ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.HIDE_NEW_PAGE);
        else
            ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.FADE_TRANSITION);
    }
}
