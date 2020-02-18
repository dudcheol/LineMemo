package com.example.linememo.view.adapter.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linememo.R;

public class ImageLastItemViewHolder extends RecyclerView.ViewHolder {
    public CardView addPhotoButton;

    public ImageLastItemViewHolder(@NonNull View itemView) {
        super(itemView);

        addPhotoButton = itemView.findViewById(R.id.add_photo_card);
    }
}