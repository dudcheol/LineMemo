package com.example.linememo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageViewPagerAdapter extends RecyclerView.Adapter<ImageViewPagerAdapter.ImageViewPagerViewHolder> {
    private Context mContext;
    private List<String> mImageUris;

    public ImageViewPagerAdapter(Context mContext, List<String> mImageUris) {
        this.mContext = mContext;
        this.mImageUris = mImageUris;
    }

    @NonNull
    @Override
    public ImageViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_viewpager_item, parent, false);
        return new ImageViewPagerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewPagerViewHolder holder, final int position) {
        Glide.with(mContext)
                .load(mImageUris.get(position))
                .override(1000)
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

    public class ImageViewPagerViewHolder extends RecyclerView.ViewHolder {
        private ImageView viewPagerImage;
        private CardView viewPagerCard;

        public ImageViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            viewPagerImage = itemView.findViewById(R.id.view_pager_image);
            viewPagerCard = itemView.findViewById(R.id.view_pager_card);
        }
    }

    public void setImageUris(List<String> imageUris) {
        this.mImageUris = imageUris;
        notifyDataSetChanged();
    }
}
