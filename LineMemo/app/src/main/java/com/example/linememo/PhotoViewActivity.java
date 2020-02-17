package com.example.linememo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
        showPhotoView();
    }

    private void initSetting() {
        Intent intent = getIntent();
        uri = intent.getStringExtra("uri");

        photoView = findViewById(R.id.photo_view);
        progressBar = findViewById(R.id.progress_bar);
        photoViewCloseButton = findViewById(R.id.photo_view_close_button);
        photoViewText = findViewById(R.id.photo_view_text);

        photoViewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        photoView.setZoomTransitionDuration(400);
        photoView.setMaximumScale(5f);
        photoView.setMediumScale(2f);
        photoView.setMinimumScale(1f);

        photoView.setOnScaleChangeListener(new OnScaleChangedListener() {
            @Override
            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
                Log.e(TAG, "getcale=" + photoView.getScale());
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

    private void showPhotoView() {
        Glide.with(this)
                .load(uri)
                .override(2048, 2048)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, e + "");
                        progressBar.setVisibility(View.GONE);
                        photoViewText.setText(R.string.memo_edit_load_fail_snack);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.ic_unknown_50dp)
                .into(photoView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.SCALE_DOWN_FADE_OUT);
    }
}
