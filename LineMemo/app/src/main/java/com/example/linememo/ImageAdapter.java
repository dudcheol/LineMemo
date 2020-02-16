package com.example.linememo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
    private static final String TAG = "ImageAdapter";
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
        final String uri = mImageUris.get(position);
        final boolean[] isAlreadyNotice = {false};
        // 이미지 로드
        Log.e(TAG, uri + "/position:" + position);
        Glide.with(mContext)
                .load(uri)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "target = "+target);
                        Log.e(TAG, model.toString());
                        Log.e(TAG, uri);
                        if(!isAlreadyNotice[0]) {
                            Snackbar.make(((Activity) mContext).findViewById(R.id.memo_edit_activity_layout),
                                    R.string.memo_edit_load_fail_snack,
                                    Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(mContext.getResources().getColor(R.color.colorErr))
                                    .show();
                            removeImage(uri); // AdapterObserver가 감지할 수 있도록 데이터셋 변경을 알림
                            isAlreadyNotice[0] = true;
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.itemDeleteBtn.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.imageView);
        holder.imageView.setOnClickListener(null); // Todo : 크게보기 - 디테일뷰에서 뷰페이저 아이템 클릭했을때와 동일하게
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

    public void addImage(String imageUri) {
        mImageUris.add(imageUri);
        notifyDataSetChanged();
    }

    public void removeImage(int position) {
        if (position >= mImageUris.size()) return;
        mImageUris.remove(position);
        notifyDataSetChanged();
    }

    public void removeImage(String value) {
        mImageUris.remove(value);
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView itemDeleteBtn;
        private ProgressBar progressBar;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.itemImage);
            itemDeleteBtn = itemView.findViewById(R.id.itemDeleteBtn);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}
