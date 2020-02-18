package com.example.linememo.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.linememo.view.activity.PhotoViewActivity;
import com.example.linememo.R;
import com.example.linememo.view.adapter.viewholder.ImagePagerItemViewHolder;
import com.example.linememo.view.animation.ActivityTransitionAnim;

import java.util.List;

public class ImageViewPagerAdapter extends RecyclerView.Adapter<ImagePagerItemViewHolder> {
    private Context mContext;
    private List<String> mImageUris;

    public ImageViewPagerAdapter(Context mContext, List<String> mImageUris) {
        this.mContext = mContext;
        this.mImageUris = mImageUris;
    }

    @NonNull
    @Override
    public ImagePagerItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_viewpager_item, parent, false);
        return new ImagePagerItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImagePagerItemViewHolder holder, final int position) {
        Glide.with(mContext)
                .load(mImageUris.get(position))
                .override(700 ,700)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .error(R.drawable.ic_unknown_50dp)
                .into(holder.viewPagerImage);

        holder.viewPagerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhotoViewActivity.class);
                intent.putExtra("uri", mImageUris.get(position));
                ActivityTransitionAnim.startActivityWithAnim((Activity) mContext, ActivityTransitionAnim.SCALE_UP_FADE_IN, intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageUris.size();
    }

    public void setImageUris(List<String> imageUris) {
        this.mImageUris = imageUris;
        notifyDataSetChanged();
    }
}
