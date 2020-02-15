package com.example.linememo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ItemViewHolder> {
    private Context mContext;
    private List<String> mImageUris;

    public ImageAdapter(Context context, List<String> imageUri) {
        this.mContext = context;
        this.mImageUris = imageUri;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_image_item, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        // 이미지 로드
        Glide.with(mContext)
                .load(mImageUris.get(position))
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Snackbar.make(((Activity) mContext).findViewById(R.id.memo_edit_activity_layout),
                                "이미지를 불러오는데 실패했습니다. 다시 시도해주세요.",
                                Snackbar.LENGTH_LONG)
                                .setBackgroundTint(mContext.getResources().getColor(R.color.colorErr))
                                .show();
                        //Todo : 역시 콜백에서 함부로 지우는건 위험하다
                        // 다르게 이미지를 가져올 수 없는 경우(URL이 잘못되었거나)에 대한 처리를 해야함..고민해볼것
                        removeImage(position); // AdapterObserver가 감지할 수 있도록 데이터셋 변경을 알림
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.itemDeleteBtn.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(holder.imageView);
        holder.imageView.setOnClickListener(null); // Todo : 크게보기
        holder.itemDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeImage(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageUris.size();
    }

    public void setImageUris(List<String> imageUris) {
        mImageUris = imageUris;
        notifyDataSetChanged();
    }

    public void addImage(String imageUri) {
        mImageUris.add(imageUri);
        notifyDataSetChanged();
    }

    public void removeImage(int position) {
        if (mImageUris.size() >= position) return;
        mImageUris.remove(position);
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView itemDeleteBtn;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.itemImage);
            itemDeleteBtn = itemView.findViewById(R.id.itemDeleteBtn);
        }
    }
}
