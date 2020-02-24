package com.example.linememo.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.linememo.R;
import com.example.linememo.util.GlideUtil;
import com.example.linememo.util.SnackbarPresenter;
import com.example.linememo.view.activity.EditMemoActivity;
import com.example.linememo.view.activity.PhotoViewActivity;
import com.example.linememo.view.adapter.viewholder.ImageItemViewHolder;
import com.example.linememo.view.adapter.viewholder.ImageLastItemViewHolder;
import com.example.linememo.view.animation.ActivityTransitionAnim;

import java.util.ArrayList;
import java.util.List;


public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ImageAdapter";
    private Context mContext;
    private List<String> mImageUris;

    public ImageAdapter(Context context, List<String> imageUri) {
        this.mContext = context;
        if (imageUri != null) this.mImageUris = imageUri;
        else this.mImageUris = new ArrayList<>();
    }

    /**
     * viewType에 따라 다른 viewHolder 사용
     *
     * @param position
     * @return 0 = 마지막 아이템으로, 이미지 추가버튼
     * 1 = 마지막을 제외한 아이템으로, 사용자가 추가한 이미지 표시
     */
    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) return 0; // 마지막은 이미지 추가 버튼
        else return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new ImageLastItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_edit_last_item, parent, false));
        else
            return new ImageItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_edit_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0:
                final ImageLastItemViewHolder lastItemViewHolder = (ImageLastItemViewHolder) holder;

                if (getItemCount() > 1)
                    lastItemViewHolder.addPhotoButton.setVisibility(View.VISIBLE);
                else
                    lastItemViewHolder.addPhotoButton.setVisibility(View.GONE);

                lastItemViewHolder.addPhotoButton.setOnClickListener(v -> {
                    if (mContext instanceof EditMemoActivity)
                        ((EditMemoActivity) mContext).createUploadDialog();
                });
                break;
            case 1:
                final ImageItemViewHolder itemViewHolder = (ImageItemViewHolder) holder;
                final String uri = mImageUris.get(position);
                final boolean[] isAlreadyNotice = {false};

                GlideUtil.showAddReqListener(mContext
                        , uri
                        , new int[]{100, 100}
                        , itemViewHolder.imageView
                        , setRequestListener(itemViewHolder, uri, isAlreadyNotice)
                        , false);

                itemViewHolder.imageView.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, PhotoViewActivity.class);
                    intent.putExtra("uri", mImageUris.get(position));
                    ActivityTransitionAnim.startActivityWithAnim((Activity) mContext, ActivityTransitionAnim.SCALE_UP_FADE_IN, intent);
                });
                itemViewHolder.itemDeleteBtn.setOnClickListener(view -> removeImage(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mImageUris.size() + 1; // 마지막 이미지 추가 버튼
    }

    public void addImage(String imageUri) {
        mImageUris.add(getItemCount() - 1, imageUri); // 가장 마지막(이미지 추가 버튼)의 앞에 추가
        notifyDataSetChanged();
    }

    public void removeImage(int position) {
        if (position >= mImageUris.size()) return;
        mImageUris.remove(position);
        notifyDataSetChanged(); // AdapterObserver가 감지할 수 있도록 데이터셋 변경을 알림
    }

    public void removeImage(String value) {
        mImageUris.remove(value);
        notifyDataSetChanged();
    }

    public void setData(List<String> imageUris) {
        this.mImageUris = imageUris;
        notifyDataSetChanged();
    }

    private RequestListener setRequestListener(final ImageItemViewHolder holder, final String uri, final boolean[] isAlreadyNotice) {
        return new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                if (!isAlreadyNotice[0]) {
                    SnackbarPresenter.show(SnackbarPresenter.ERROR
                            , ((Activity) mContext).findViewById(R.id.memo_edit_activity_layout)
                            , R.string.memo_edit_load_fail_snack
                            , SnackbarPresenter.LENGTH_LONG);
                    removeImage(uri);
                    isAlreadyNotice[0] = true;
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.itemDeleteBtn.setVisibility(View.VISIBLE);
                holder.zoomIcon.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.GONE);
                return false;
            }
        };
    }
}
