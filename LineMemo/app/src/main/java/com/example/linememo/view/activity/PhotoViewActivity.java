package com.example.linememo.view.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.linememo.R;
import com.example.linememo.util.GlideUtil;
import com.example.linememo.view.animation.ActivityTransitionAnim;
import com.github.chrisbanes.photoview.OnScaleChangedListener;
import com.github.chrisbanes.photoview.OnViewTapListener;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoViewActivity extends AppCompatActivity {
    private static final String TAG = "PhotoViewActivity";
    private PhotoView photoView;
    private String uri;
    private ProgressBar progressBar;
    private ImageView photoViewCloseButton;
    private TextView photoViewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        initSetting();
        showPhoto();
    }

    private void initSetting() {
        findViewByIds();
        initData();
        setPhotoView();
        setListener();
    }

    private void findViewByIds() {
        photoView = findViewById(R.id.photo_view);
        progressBar = findViewById(R.id.progress_bar);
        photoViewCloseButton = findViewById(R.id.photo_view_close_button);
        photoViewText = findViewById(R.id.photo_view_text);
    }

    private void initData() {
        uri = getIntent().getStringExtra("uri");
    }

    private void setPhotoView() {
        photoView.setZoomTransitionDuration(400);
        photoView.setMaximumScale(5f);
        photoView.setMediumScale(2f);
        photoView.setMinimumScale(1f);
    }

    private void setListener() {
        photoViewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        photoView.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
//                Log.e(TAG, "getcale=" + photoView.getScale());
                if (photoView.getScale() >= 1.1) {
                    photoViewCloseButton.setVisibility(View.GONE);
                    photoViewText.setVisibility(View.GONE);
                } else {
                    photoViewCloseButton.setVisibility(View.VISIBLE);
                    photoViewText.setVisibility(View.VISIBLE);
                }
            }
        });

        photoView.setOnViewTapListener(new OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                photoView.setScale(1f, true);
            }
        });
    }

    private void showPhoto() {
        GlideUtil.showAddReqListener(this
                , uri
                , new int[]{2048, 2048}
                , photoView
                , requestListener
                , true);
    }

    private RequestListener requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            progressBar.setVisibility(View.GONE);
            photoViewText.setText(R.string.photo_view_load_fail_notice);
            photoViewText.setTextColor(getResources().getColor(R.color.colorErr));
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            progressBar.setVisibility(View.GONE);
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.SCALE_DOWN_FADE_OUT);
    }
}
