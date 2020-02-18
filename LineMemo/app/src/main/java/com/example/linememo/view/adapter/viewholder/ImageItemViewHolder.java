package com.example.linememo.view.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linememo.R;

public class ImageItemViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public ImageView itemDeleteBtn;
    public ProgressBar progressBar;
    public RelativeLayout zoomIcon;

    public ImageItemViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.itemImage);
        itemDeleteBtn = itemView.findViewById(R.id.itemDeleteBtn);
        progressBar = itemView.findViewById(R.id.progress_bar);
        zoomIcon = itemView.findViewById(R.id.zoom_icon);
    }
}