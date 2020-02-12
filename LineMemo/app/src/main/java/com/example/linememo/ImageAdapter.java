package com.example.linememo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import static com.example.linememo.MemoEditActivity.GALLERY_REQUEST_CODE;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ItemViewHolder> {
    Context mContext;
    List<String> mImageUris;
    String tempUri;

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
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        // 마지막 아이템은 항상 이미지 추가 버튼
        if (position == getItemCount() - 1) {
            Glide.with(mContext)
                    .load(R.drawable.ic_add_a_photo_black_24dp)
                    .into(holder.imageView);
            holder.itemDeleteBtn.setVisibility(View.GONE);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    createUploadDialog().show();
                }
            });
        } else {
            Glide.with(mContext)
                    .load(mImageUris.get(position))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Todo : 이미지 로드를 실패했다고 해서 position의 url을 제거하는 것은 위험하다
                            removeImage(position);
                            Toast.makeText(mContext, "이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(null);
            holder.itemDeleteBtn.setVisibility(View.VISIBLE);
            holder.itemDeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeImage(position);
                }
            });
        }
        Log.e("ImageAdapter", mImageUris.get(position));
    }

    @Override
    public int getItemCount() {
        return mImageUris.size();
    }

    public void addImage(String imageUri) {
        mImageUris.add(getItemCount() - 1, imageUri);
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

    private Dialog createUploadDialog() {
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
                                createUriInputDialog().show();
                                break;
                        }
                    }
                });
        return builder.create();
    }

    private Dialog createUriInputDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_uri_input, null);
        builder.setView(v)
                .setCancelable(false)
                .setPositiveButton(R.string.positiveBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        addImage(getUrlStringFromView(v));
                        tempUri =  getUrlStringFromView(v);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.negativeBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        return builder.create();
    }

    private void goToGallery() {
        /** 주의 -- Intent intent = new Intent(Intent.ACTION_PICK) 사용에 대한 문제점
         * ACTION_PICK 사용 시, 파일 uri를 받아올 때 '일시적인 권한'으로 접근할 수 있게 한다.(보안상의 이유로 안드로이드에서 의도적으로 한 것)
         * 이렇게 해서 얻은 uri를 통해 다른 컨텍스트에서 접근하려고 한다면, 액세스 불가능하다는 보안 에러가 뜬다.
         * 따라서, onActivityResult에서 전달받은 uri를 가지고 이미지 데이터를 가져와야 하는데,
         * 우리 앱은 로컬에 저장된 이미지를 보여주는 기능을 하는 것이기 때문에 채택하지 않았다.
         */
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        // 이미지 파일에 접근할 수 있는 chooser가 많다면, 사용자가 선택할 수 있게 한다
        ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "사진첩을 선택하세요"), GALLERY_REQUEST_CODE);
    }

    private String getUrlStringFromView(View v) {
        EditText urlEdit = v.findViewById(R.id.uriEdit);
        return urlEdit.getText().toString().trim();
    }
}
