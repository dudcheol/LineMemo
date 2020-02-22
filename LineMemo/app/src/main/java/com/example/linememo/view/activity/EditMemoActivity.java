package com.example.linememo.view.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linememo.R;
import com.example.linememo.model.Memo;
import com.example.linememo.util.BaseActivity;
import com.example.linememo.util.ConvertUtil;
import com.example.linememo.util.DialogUtil;
import com.example.linememo.view.adapter.ImageAdapter;
import com.example.linememo.viewmodel.EditViewModel;
import com.example.linememo.viewmodel.MemoViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class EditMemoActivity extends BaseActivity {
    public static final int CREATE_MODE = -1001;
    public static final int MODIFY_MODE = -1002;
    public static final int CAMERA_REQUEST_CODE = 2001;
    public static final int GALLERY_REQUEST_CODE = 2002;

    private static final String TAG = "EditMemoActivity";
    private static final int ERROR = -1;

    private MemoViewModel mMemoViewModel;
    private EditViewModel mEditViewModel;
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
    protected int getLayoutResource() {
        return R.layout.activity_edit_memo;
    }

    @Override
    protected int getActivityType() {
        return BaseActivity.EDIT_ACTIVITY;
    }

    @Override
    protected int getBackPressAnim() {
        return super.editViewMode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSetting();
        showMemo();
        initImageRecyclerView();
    }

    void initSetting() {
        findViewByIds();
        initData();
        setListener();
    }

    private void initData() {
        viewMode = super.editViewMode;
        mMemoId = getIntent().getIntExtra("memoId", ERROR);
        if (viewMode == ERROR || mMemoId == ERROR) DialogUtil.showErrDialog(this);
        mMemoViewModel = new ViewModelProvider(this).get(MemoViewModel.class);
        mEditViewModel = new ViewModelProvider(this).get(EditViewModel.class);
        mMemoData = mMemoViewModel.find(mMemoId);
        mImageUris = mMemoData == null ? new ArrayList<String>() : mMemoData.getImageUris();
    }

    private void findViewByIds() {
        imageAreaNoti = findViewById(R.id.image_area_noti);
        titleEdit = findViewById(R.id.title_edit);
        contentEdit = findViewById(R.id.content_edit);
        imageRecyclerView = findViewById(R.id.image_recycler);
        saveButton = findViewById(R.id.save_button);
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
        int divider = ConvertUtil.dpToPx(this, 5);
        imageRecyclerView.addItemDecoration(ConvertUtil.getRecyclerPaddingItemDeco(divider, 0, divider, 0));
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new ImageAdapter(this, mImageUris);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
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
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    mAdapter.addImage(data.getData().toString());
                    break;
                case CAMERA_REQUEST_CODE:
                    mAdapter.addImage(mEditViewModel.getUri()); // 카메라 촬영 후 촬영한 사진 선택 -> 선택된 사진 어댑터 리스트에 추가
                    break;
            }
        } else Log.e(TAG, "onActivityResult RESULT NO");
    }

    private void openCamera() {
        Intent intent = mEditViewModel.openCamera();
        if (intent != null)
            startActivityForResult(Intent.createChooser(intent
                    , getResources().getString(R.string.memo_eidt_camera_select)), EditMemoActivity.CAMERA_REQUEST_CODE);
        else DialogUtil.showErrDialog(this);
    }

    private void oepnAlbum() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent
                , getResources().getString(R.string.memo_eidt_gallery_select)), EditMemoActivity.GALLERY_REQUEST_CODE);
    }

    private void saveMemoData() {
        if (viewMode == CREATE_MODE) { // '새 메모 쓰기'화면 일 경우 새로운 메모 추가
            mMemoViewModel.insert(new Memo(
                    titleEdit.getText().toString(),
                    contentEdit.getText().toString(),
                    mImageUris,
                    System.currentTimeMillis()));
        } else if (viewMode == MODIFY_MODE) { // '메모 수정하기'일 경우 기존 메모의 데이터를 변경 후 업데이트
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
        ((EditText) v.findViewById(R.id.uri_input)).addTextChangedListener(new TextWatcher() {
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
                    openCamera();
                    break;
                case 2: // 외부 이미지 주소
                    createUriInputDialog();
                    break;
            }
        }
    };
}
