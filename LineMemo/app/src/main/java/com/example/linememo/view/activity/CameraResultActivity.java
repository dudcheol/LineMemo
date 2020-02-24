package com.example.linememo.view.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.linememo.R;
import com.example.linememo.databinding.ActivityCameraResultBinding;
import com.example.linememo.util.GlideUtil;
import com.example.linememo.view.animation.ActivityTransitionAnim;
import com.example.linememo.viewmodel.EditViewModel;

import java.io.File;

public class CameraResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityCameraResultBinding mBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera_result);
        EditViewModel mEditViewModel = new ViewModelProvider(this).get(EditViewModel.class);

        String path = getIntent().getStringExtra("path");
        GlideUtil.show(this, path, mBinding.imageResult);

        Intent intent = new Intent(this, EditMemoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        mBinding.tryAgainButton.setOnClickListener(v -> {
            mEditViewModel.deleteFile(new File(path));
            onBackPressed();
        });

        mBinding.selectImageButton.setOnClickListener(v -> {
            intent.putExtra("selected", path);
            ActivityTransitionAnim.startActivityWithAnim(this, ActivityTransitionAnim.HIDE_NEW_PAGE, intent);
        });

        mBinding.closeButton.setOnClickListener(v -> {
            mEditViewModel.deleteFile(new File(path));
            ActivityTransitionAnim.startActivityWithAnim(this, ActivityTransitionAnim.HIDE_NEW_PAGE, intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.NO_TRANSITION);
    }
}
