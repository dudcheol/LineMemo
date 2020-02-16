package com.example.linememo;

import android.content.Intent;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.OnMatrixChangedListener;
import com.github.chrisbanes.photoview.PhotoView;

public class PhotoViewActivity extends AppCompatActivity {
    private static final String TAG = "PhotoViewActivity";
    private PhotoView photoView;
    private String uri;
    private ProgressBar progressBar;
    private RelativeLayout fullScreen;
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
        fullScreen = findViewById(R.id.full_screen);
        photoViewCloseButton = findViewById(R.id.photo_view_close_button);
        photoViewText = findViewById(R.id.photo_view_text);

        photoViewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        photoView.setOnMatrixChangeListener(new OnMatrixChangedListener() {
            @Override
            public void onMatrixChanged(RectF rect) {
                Log.e(TAG, "rect=" + rect);
                if (rect.left >= 0) {
                    photoViewCloseButton.setVisibility(View.VISIBLE);
                    photoViewText.setVisibility(View.VISIBLE);
                } else {
                    photoViewCloseButton.setVisibility(View.GONE);
                    photoViewText.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showPhotoView() {
        Glide.with(this)
                .load(uri)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
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
        ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.FADE_TRANSITION);
    }
}
