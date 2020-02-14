package com.example.linememo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ItemViewHolder> {
    public static final int IMAGE_ADAPTER_VIEW_MODE = 3001;
    public static final int IMAGE_ADAPTER_EDIT_MODE = 3002;

    private int currentMode;
    private Context mContext;
    private List<String> mImageUris;

    public ImageAdapter(Context context, List<String> imageUri, int mode) {
        this.mContext = context;
        this.mImageUris = imageUri;
        this.currentMode = mode;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_image_item, parent, false);
        return  new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        // 이미지 로드
        Glide.with(mContext)
                .load(mImageUris.get(position))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Todo : 이미지 로드 실패시 에러처리
                        Log.e("ImageAdapter", "glide load fail reason = " + e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.imageView);
        holder.imageView.setOnClickListener(null); // Todo : 크게보기

//        // 어댑터 모드 분기
//        if (currentMode == IMAGE_ADAPTER_EDIT_MODE) {
//            // IMAGE_ADAPTER_EDIT_MODE - 삭제버튼 있어야 함
//            holder.itemDeleteBtn.setVisibility(View.VISIBLE);
//            holder.itemDeleteBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    removeImage(position);
//                }
//            });
//        } else {
//            // IMAGE_ADAPTER_VIEW_MODE - 삭제버튼 없음
//            holder.itemDeleteBtn.setVisibility(View.GONE);
//        }
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
