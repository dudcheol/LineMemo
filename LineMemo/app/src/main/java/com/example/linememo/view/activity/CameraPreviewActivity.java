package com.example.linememo.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.linememo.R;
import com.example.linememo.databinding.ActivityCameraPreviewBinding;
import com.example.linememo.util.DialogUtil;
import com.example.linememo.util.SnackbarPresenter;
import com.example.linememo.view.animation.ActivityTransitionAnim;
import com.example.linememo.viewmodel.EditViewModel;

import java.io.File;
import java.io.IOException;

public class CameraPreviewActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String TAG = "CameraPreviewActivity";
    private static final double RATIO_4_3_VALUE = 4.0 / 3.0;
    private static final double RATIO_16_9_VALUE = 16.0 / 9.0;

    private String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private ActivityCameraPreviewBinding mBinding;
    private EditViewModel mEditViewModel;

    private CameraX.LensFacing lensFacing = CameraX.LensFacing.BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera_preview);
        mEditViewModel = new ViewModelProvider(this).get(EditViewModel.class);

        mBinding.backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        if (allPermissionsGranted())
            mBinding.viewFinder.post(this::startCamera);
        else
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);

        mBinding.viewFinder.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> updateTransform());
    }

    @SuppressLint("RestrictedApi")
    private void startCamera() {
        bindCameraUseCases();

        mBinding.cameraSelector.setOnClickListener(v -> {
            lensFacing = lensFacing == CameraX.LensFacing.FRONT ? CameraX.LensFacing.BACK : CameraX.LensFacing.FRONT;

            try {
                CameraX.getCameraWithLensFacing(lensFacing);
                bindCameraUseCases();
            } catch (CameraInfoUnavailableException e) {
                DialogUtil.showErrDialog(this);
            }
        });
    }

    private void updateTransform() {
        Matrix matrix = new Matrix();

        float centerX = mBinding.viewFinder.getWidth() / 2f;
        float centerY = mBinding.viewFinder.getHeight() / 2f;

        int rotationDegrees;
        switch (mBinding.viewFinder.getDisplay().getRotation()) {
            case Surface.ROTATION_0:
                rotationDegrees = 0;
                break;
            case Surface.ROTATION_90:
                rotationDegrees = 90;
                break;
            case Surface.ROTATION_180:
                rotationDegrees = 180;
                break;
            case Surface.ROTATION_270:
                rotationDegrees = 270;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + mBinding.viewFinder.getDisplay().getRotation());
        }
        matrix.postRotate((float) -rotationDegrees, centerX, centerY);
    }

    private AspectRatio aspectRatio(int width, int height) {
        int previewRatio = Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - RATIO_4_3_VALUE) <= Math.abs(previewRatio - RATIO_16_9_VALUE))
            return AspectRatio.RATIO_4_3;
        return AspectRatio.RATIO_16_9;
    }

    private void bindCameraUseCases() {
        CameraX.unbindAll();

        DisplayMetrics metrics = new DisplayMetrics();
        mBinding.viewFinder.getDisplay().getRealMetrics(metrics);
        AspectRatio screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels);
        int rotation = mBinding.viewFinder.getDisplay().getRotation();

        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .setLensFacing(lensFacing)
                .build();

        Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(
                previewOutput -> {
                    ViewGroup parent = (ViewGroup) mBinding.viewFinder.getParent();
                    parent.removeView(mBinding.viewFinder);
                    parent.addView(mBinding.viewFinder, 0);

                    mBinding.viewFinder.setSurfaceTexture(previewOutput.getSurfaceTexture());
                    updateTransform();
                });

        ImageCaptureConfig imageCaptureConfig = new ImageCaptureConfig.Builder() // 종횡비와 캡쳐모드를 기반으로 적절한 해상도를 유추함
                .setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .setLensFacing(lensFacing)
                .build();

        ImageCapture imageCapture = new ImageCapture(imageCaptureConfig);
        mBinding.captureButton.setOnClickListener(v -> {
            File file = null;
            try {
                file = mEditViewModel.createImageFile();
            } catch (IOException e) {
                SnackbarPresenter.showCommonError(mBinding.viewFinder);
            }
            if (file == null) return;
            ImageCapture.Metadata metadata = new ImageCapture.Metadata();
            metadata.isReversedHorizontal = lensFacing == CameraX.LensFacing.FRONT;

            imageCapture.takePicture(file, metadata, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedListener() {
                @Override
                public void onImageSaved(@NonNull File file) {
                    mBinding.viewFinder.post(() -> {
                        Intent intent = new Intent(CameraPreviewActivity.this, CameraResultActivity.class);
                        intent.putExtra("path", mEditViewModel.createImageUri(file).toString());
                        ActivityTransitionAnim.startActivityWithAnim(CameraPreviewActivity.this, ActivityTransitionAnim.NO_TRANSITION, intent);
                    });
                }

                @Override
                public void onError(@NonNull ImageCapture.ImageCaptureError imageCaptureError, @NonNull String message, @Nullable Throwable cause) {
                    Log.e(TAG, message);
                    SnackbarPresenter.showCommonError(mBinding.viewFinder);
                }
            });
        });

        CameraX.bindToLifecycle(this, preview, imageCapture);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                mBinding.viewFinder.post(this::startCamera);
            } else {
                SnackbarPresenter.show(SnackbarPresenter.ERROR, mBinding.viewFinder, R.string.permissionError, SnackbarPresenter.LENGTH_SHORT);
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS)
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.HIDE_NEW_PAGE);
    }
}
