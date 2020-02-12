package com.example.linememo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import static com.example.linememo.MemoEditActivity.GALLERY_REQUEST_CODE;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ItemViewHolder> {
    Context mContext;
    List<String> mImageUris;

    public ImageAdapter(Context context, List<String> imageUri) {
        if (!imageUri.contains("AddPhotoBtn")) imageUri.add("AddPhotoBtn");
        this.mContext = context;
        this.mImageUris = imageUri;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_image_item, parent, false);
        ItemViewHolder iv = new ItemViewHolder(v);
        return iv;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // 마지막 아이템은 항상 이미지 추가 버튼
        if (position == getItemCount() - 1) {
            Glide.with(mContext)
                    .load(R.drawable.ic_add_a_photo_black_24dp)
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createDialog().show();
                }
            });
        } else {
            Glide.with(mContext)
                    .load(mImageUris.get(position))
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return mImageUris.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.itemImage);
        }
    }

    private Dialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.add_image_title)
                .setItems(R.array.add_image_methods_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0: // 사진첩
                                goToGallery();
                                break;
                            case 1: // 카메라 촬영
                                Toast.makeText(mContext, "카메라 촬영", Toast.LENGTH_SHORT).show();
                                break;
                            case 2: // 외부 이미지 주소
                                Toast.makeText(mContext, "외부 이미지 주소", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
        return builder.create();
    }

    private void goToGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        ((Activity) mContext).startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    public void addImage(String imageUri) {
        mImageUris.add(getItemCount() - 1, imageUri);
        notifyDataSetChanged();
    }
}
