package com.example.linememo.view.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linememo.R;
import com.example.linememo.databinding.ActivityEditMemoBinding;
import com.example.linememo.model.Memo;
import com.example.linememo.util.BaseActivity;
import com.example.linememo.util.ConvertUtil;
import com.example.linememo.util.DialogUtil;
import com.example.linememo.util.SnackbarPresenter;
import com.example.linememo.view.adapter.ImageAdapter;
import com.example.linememo.view.animation.ActivityTransitionAnim;
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

    private ActivityEditMemoBinding mBinding;
    private MemoViewModel mMemoViewModel;
    private EditViewModel mEditViewModel;
    private ImageAdapter mAdapter;

    private int viewMode;
    private Memo mMemoData;
    private List<String> mImageUris;

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
        setBinding(R.layout.activity_edit_memo);

        initSetting();
        initImageRecyclerView();
    }

    private void initSetting() {
        mBinding = (ActivityEditMemoBinding) getBinding();
        mBinding.setLifecycleOwner(this);
        mMemoViewModel = new ViewModelProvider(this).get(MemoViewModel.class);
        mEditViewModel = new ViewModelProvider(this).get(EditViewModel.class);
        mBinding.setMemoViewModel(mMemoViewModel);

        viewMode = super.editViewMode;
        int mMemoId = getIntent().getIntExtra("memoId", ERROR);
        if (viewMode == ERROR || mMemoId == ERROR) DialogUtil.showErrDialog(this);

        mMemoData = mMemoViewModel.find(mMemoId);
        mImageUris = mMemoData != null ? mMemoData.getImageUris() : new ArrayList<String>();
        if (viewMode == CREATE_MODE) changeSaveButtonState(false);

        setListener();
    }

    private void setListener() {
        mBinding.saveButton.setOnClickListener(new SaveButtonClick());
        mBinding.imageAreaNoti.setOnClickListener(new ImageAreaButtonClick());
        mBinding.titleEdit.addTextChangedListener(editTextChangeListener);
        mBinding.contentEdit.addTextChangedListener(editTextChangeListener);
        mBinding.titleEdit.requestFocus();
    }

    private void initImageRecyclerView() {
        mBinding.imageRecycler.setHasFixedSize(true);
        int divider = ConvertUtil.dpToPx(this, 5);
        mBinding.imageRecycler.addItemDecoration(ConvertUtil.getRecyclerPaddingItemDeco(divider, 0, divider, 0));
        mBinding.imageRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new ImageAdapter(this, mImageUris);
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mAdapter.getItemCount() <= 1) {
                    changeSaveButtonState(mEditViewModel.getTextPassOrNot(ConvertUtil.getString(mBinding.titleEdit, true)));
                    mBinding.imageAreaNoti.setVisibility(View.VISIBLE);
                } else {
                    changeSaveButtonState(true);
                    mBinding.imageAreaNoti.setVisibility(View.GONE);
                }
                mBinding.imageRecycler.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
        mBinding.imageRecycler.setAdapter(mAdapter);
        mBinding.imageRecycler.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    private DialogInterface.OnClickListener onClickAddImageListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case 0: // 사진첩
                    openAlbum();
                    break;
                case 1: // LINE MEMO 카메라로 촬영
                    openLineMemoCamera();
                    break;
                case 2: // 다른 앱 카메라로 촬영
                    openAnotherCamera();
                    break;
                case 3: // 외부 이미지 주소
                    createUriInputDialog();
                    break;
            }
        }
    };

    private void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.memo_eidt_gallery_select)), EditMemoActivity.GALLERY_REQUEST_CODE);
    }

    private void openLineMemoCamera() {
        Intent intent = new Intent(EditMemoActivity.this, CameraPreviewActivity.class);
        ActivityTransitionAnim.startActivityWithAnim(EditMemoActivity.this, ActivityTransitionAnim.SHOW_NEW_PAGE, intent);
    }

    private void openAnotherCamera() {
        Intent intent = mEditViewModel.openCamera();
        if (intent != null)
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.memo_eidt_camera_select)), EditMemoActivity.CAMERA_REQUEST_CODE);
        else DialogUtil.showErrDialog(this);
    }

    private void saveMemoData() {
        if (viewMode == CREATE_MODE)  // '새 메모 쓰기' - 새로운 메모 추가
            mMemoViewModel.insert(new Memo(ConvertUtil.getString(mBinding.titleEdit, false)
                    , ConvertUtil.getString(mBinding.contentEdit, false), mImageUris, System.currentTimeMillis()));
        else if (viewMode == MODIFY_MODE)  // '메모 수정하기' - 기존 메모 데이터 업데이트
            mMemoViewModel.update(mMemoData);
    }

    private void changeSaveButtonState(boolean clickable) {
        if (clickable) {
            mBinding.saveButton.setEnabled(true);
            mBinding.saveButton.setBackground(getResources().getDrawable(R.drawable.green_button));
        } else {
            mBinding.saveButton.setEnabled(false);
            mBinding.saveButton.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
        }
    }

    public void createUploadDialog() {
        DialogUtil.showDialogItems(this, R.drawable.ic_attach_file_24dp, getResources().getString(R.string.add_image_title)
                , null, R.array.add_image_methods_array, onClickAddImageListener);
    }

    private void createUriInputDialog() {
        final View v = getLayoutInflater().inflate(R.layout.dialog_uri_input, null);
        MaterialAlertDialogBuilder materialAlertDialogBuilder = DialogUtil.makeDialogWithView(this
                , R.drawable.ic_web_24dp, getResources().getString(R.string.add_image_uri_dialog_content1), null, v, false, getResources().getString(R.string.positiveBtn), getResources().getString(R.string.negativeBtn)
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.addImage(ConvertUtil.getString((EditText) v.findViewById(R.id.uri_input), true));
                    }
                }, DialogUtil.onClickCancelListener);

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

    private class ImageAreaButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            createUploadDialog();
        }
    }

    private TextWatcher editTextChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            changeSaveButtonState(mEditViewModel.isMemoStorable(new String[]{ConvertUtil.getString(mBinding.titleEdit, true),
                    ConvertUtil.getString(mBinding.contentEdit, true)}, mImageUris));
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            if (intent.hasExtra("selected"))
                mAdapter.addImage(intent.getStringExtra("selected"));
        } else SnackbarPresenter.showCommonError(mBinding.memoEditActivityLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    if (data != null && data.getData() != null)
                        mAdapter.addImage(data.getData().toString());
                    else SnackbarPresenter.showCommonError(mBinding.memoEditActivityLayout);
                    break;
                case CAMERA_REQUEST_CODE:
                    mAdapter.addImage(mEditViewModel.getUri()); // 카메라 촬영 후 촬영한 사진 선택 -> 선택된 사진 어댑터 리스트에 추가
                    break;
            }
        }
    }
}
