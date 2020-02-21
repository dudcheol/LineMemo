package com.example.linememo.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linememo.R;
import com.example.linememo.model.Memo;
import com.example.linememo.util.ConvertUtil;
import com.example.linememo.util.DialogUtil;
import com.example.linememo.view.adapter.ImageAdapter;
import com.example.linememo.view.animation.ActivityTransitionAnim;
import com.example.linememo.viewmodel.EditViewModel;
import com.example.linememo.viewmodel.MemoViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditMemoActivity extends AppCompatActivity {
    public static final int CREATE_MODE = 1001;
    public static final int MODIFY_MODE = 1002;
    public static final int CREATE_MODE_MEMO_ID = -2;
    public static final int CAMERA_REQUEST_CODE = 2001;
    public static final int GALLERY_REQUEST_CODE = 2002;

    private static final String TAG = "EditMemoActivity";
    private static final int ERROR = -1;

    private MemoViewModel mMemoViewModel;
    private EditViewModel mEditViewModel;
    private Toolbar myToolbar;
    private LinearLayout imageAreaNoti;
    private EditText titleEdit;
    private EditText contentEdit;
    private Button saveButton;
    private RecyclerView imageRecyclerView;
    private ImageAdapter mAdapter;

    private int viewMode;
    private int mMemoId;
    private Memo mMemoData;
    private List<String> mImageUris;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);

        initSetting();
        showMemo();
        initImageRecyclerView();
    }

    void initSetting() {
        findViewByIds();
        initData();
        setToolbar();
        setListener();
    }

    private void initData() {
        viewMode = getIntent().getIntExtra("mode", ERROR);
        mMemoId =  getIntent().getIntExtra("memoId", ERROR);
        mMemoViewModel = new ViewModelProvider(this).get(MemoViewModel.class);
        mEditViewModel = new ViewModelProvider(this).get(EditViewModel.class);
        mMemoData = mMemoViewModel.find(mMemoId);
        mImageUris = mMemoData == null ? new ArrayList<String>() : mMemoData.getImageUris();
    }

    private void findViewByIds() {
        myToolbar = findViewById(R.id.toolbar);
        imageAreaNoti = findViewById(R.id.image_area_noti);
        titleEdit = findViewById(R.id.title_edit);
        contentEdit = findViewById(R.id.content_edit);
        imageRecyclerView = findViewById(R.id.image_recycler);
        saveButton = findViewById(R.id.save_button);
    }

    private void setToolbar() {
        if (viewMode == CREATE_MODE)
            myToolbar.setTitle(R.string.menu_write);
        else if (viewMode == MODIFY_MODE)
            myToolbar.setTitle(R.string.menu_edit);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_24dp);
        myToolbar.setNavigationOnClickListener(new BackButtonClick());
    }

    private void setListener() {
        saveButton.setOnClickListener(new SaveButtonClick());
        imageAreaNoti.setOnClickListener(new ImageAreaButtonClick());

        titleEdit.addTextChangedListener(editTextChangeListener);
        contentEdit.addTextChangedListener(editTextChangeListener);
    }

    private void showMemo() {
        if (viewMode == CREATE_MODE) {
            changeSaveButtonState(false);
            imageAreaNoti.setVisibility(View.VISIBLE);
        } else if (viewMode == MODIFY_MODE) {
            titleEdit.setText(mMemoData.getTitle());
            contentEdit.setText(mMemoData.getContent());
            if (mImageUris.isEmpty()) imageAreaNoti.setVisibility(View.VISIBLE);
            else imageAreaNoti.setVisibility(View.GONE);
        }
        titleEdit.requestFocus();
    }

    void initImageRecyclerView() {
        imageRecyclerView.setHasFixedSize(true);
        imageRecyclerView.addItemDecoration(imageItemDecoration);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
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
                    /**
                     *  사용자가 카메라로 사진을 촬영하고 촬영한 사진을 선택했다면 RESULT_OK임
                     *  RESULT_OK로 이곳에 진입했다는 것은 createImageFile에서 생성한 사진의 저장경로가 존재한다는 것과 같음
                     *  따라서 mCurrentPhotoPath를 이용해 어댑터에 추가함
                     */
                    mAdapter.addImage(mEditViewModel.uri);
                    break;
            }
        } else {
            Log.e(TAG, "onActivityResult RESULT NO");
        }
    }

    private void oepnCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = mEditViewModel.createImageFile();
            } catch (IOException ex) {
                // Todo : 에러뜨는지확인해보기
                DialogUtil.showErrDialog(this);
            }
            if (photoFile != null) {
                Uri photoURI = mEditViewModel.createImageUri(photoFile);
                Log.e(TAG, "photoURI = " + photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(Intent.createChooser(takePictureIntent
                        , getResources().getString(R.string.memo_eidt_camera_select)), EditMemoActivity.CAMERA_REQUEST_CODE);
            }
        }
    }

    private void oepnAlbum() {
        /**
         * <!> 주의 -- Intent intent = new Intent(Intent.ACTION_PICK) 사용에 대한 문제점
         * ACTION_PICK 사용 시, 파일 uri를 받아올 때 '일시적인 권한'으로 접근할 수 있게 한다.(보안상의 이유로 안드로이드에서 의도한 것)
         * 이렇게 해서 얻은 uri를 통해 다른 컨텍스트에서 접근하려고 한다면, 액세스 불가능하다는 보안 에러가 뜬다.
         * 전달받은 uri는 다른 컨텍스트에서도 접근 가능해야하므로 채택하지 않았다.
         */
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent
                , getResources().getString(R.string.memo_eidt_gallery_select)), EditMemoActivity.GALLERY_REQUEST_CODE);
    }

    private void saveMemoData() {
        if (viewMode == CREATE_MODE) {
            mMemoViewModel.insert(new Memo(
                    titleEdit.getText().toString(),
                    contentEdit.getText().toString(),
                    mImageUris,
                    System.currentTimeMillis()));
        } else if (viewMode == MODIFY_MODE) {
            mMemoData.setTitle(titleEdit.getText().toString());
            mMemoData.setContent(contentEdit.getText().toString());
            mMemoData.setImageUri(mImageUris);
            mMemoData.setDate(System.currentTimeMillis());
            mMemoViewModel.update(mMemoData);
        }
    }

    private TextWatcher editTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            changeSaveButtonState(mEditViewModel.isMemoStorable(new String[]{ConvertUtil.getString(titleEdit), ConvertUtil.getString(contentEdit)}, mImageUris));
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private RecyclerView.ItemDecoration imageItemDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int divider = ConvertUtil.dpToPx(getApplicationContext(), 5);
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

    public void createUploadDialog() {
        DialogUtil.showDialogItems(this
                , R.drawable.ic_attach_file_24dp
                , getResources().getString(R.string.add_image_title)
                , null
                , R.array.add_image_methods_array
                , onClickAddImageListener);
    }

    private void createUriInputDialog() {
        final View v = getLayoutInflater().inflate(R.layout.dialog_uri_input, null);

        MaterialAlertDialogBuilder materialAlertDialogBuilder = DialogUtil.makeDialogWithView(this
                , R.drawable.ic_web_24dp
                , getResources().getString(R.string.add_image_uri_dialog_content1)
                , null
                , v
                , false
                , getResources().getString(R.string.positiveBtn), getResources().getString(R.string.negativeBtn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.addImage(ConvertUtil.getString((EditText) v.findViewById(R.id.uri_input)));
                    }
                }
                , DialogUtil.onClickCancelListener);

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
                positiveButton.setEnabled(mEditViewModel.getTextPassOrNot(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (viewMode == CREATE_MODE)
            ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.HIDE_NEW_PAGE);
        else if (viewMode == MODIFY_MODE)
            ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.FADE_TRANSITION);
    }

    private class SaveButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            saveMemoData();
            setResult(RESULT_OK);
            onBackPressed();
        }
    }

    public class ImageAreaButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            createUploadDialog();
        }
    }

    private DialogInterface.OnClickListener onClickAddImageListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: // 사진첩
                    oepnAlbum();
                    break;
                case 1: // 카메라 촬영
                    oepnCamera();
                    break;
                case 2: // 외부 이미지 주소
                    createUriInputDialog();
                    break;
            }
        }
    };

    private class BackButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    }
}
