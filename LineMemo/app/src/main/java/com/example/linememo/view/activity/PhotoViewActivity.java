package com.example.linememo.view.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.linememo.R;
import com.example.linememo.databinding.ActivityPhotoViewBinding;
import com.example.linememo.util.GlideUtil;
import com.example.linememo.view.animation.ActivityTransitionAnim;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.OnViewTapListener;

public class PhotoViewActivity extends AppCompatActivity {
    private static final String TAG = "PhotoViewActivity";
    private ActivityPhotoViewBinding mBinding;
    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_photo_view);

        initSetting();
        showPhoto();
    }

    private void initSetting() {
        uri = getIntent().getStringExtra("uri");
        setPhotoView();
        setListener();
    }

    private void setPhotoView() {
        mBinding.photoView.setZoomTransitionDuration(400);
        mBinding.photoView.setMaximumScale(5f);
        mBinding.photoView.setMediumScale(2f);
        mBinding.photoView.setMinimumScale(1f);
    }

    private void setListener() {
        mBinding.photoViewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBinding.photoView.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
//                Log.e(TAG, "getcale=" + photoView.getScale());
                if (mBinding.photoView.getScale() >= 1.1) {
                    mBinding.photoViewCloseButton.setVisibility(View.GONE);
                    mBinding.photoViewText.setVisibility(View.GONE);
                } else {
                    mBinding.photoViewCloseButton.setVisibility(View.VISIBLE);
                    mBinding.photoViewText.setVisibility(View.VISIBLE);
                }
            }
        });

        mBinding.photoView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                mBinding.photoView.setScale(1f, true);
            }
        });
    }

    private void showPhoto() {
        GlideUtil.showAddReqListener(this
                , uri
                , new int[]{2048, 2048}
                , mBinding.photoView
                , requestListener
                , true);
    }

    private RequestListener requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            mBinding.progressBar.setVisibility(View.GONE);
            mBinding.photoViewText.setText(R.string.photo_view_load_fail_notice);
            mBinding.photoViewText.setTextColor(getResources().getColor(R.color.colorErr));
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            mBinding.progressBar.setVisibility(View.GONE);
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.SCALE_DOWN_FADE_OUT);
    }
}
