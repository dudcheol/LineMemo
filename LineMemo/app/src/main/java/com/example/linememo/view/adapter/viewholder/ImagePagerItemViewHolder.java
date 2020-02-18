package com.example.linememo.view.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linememo.R;

public class ImagePagerItemViewHolder extends RecyclerView.ViewHolder {
    public ImageView viewPagerImage;
    public CardView viewPagerCard;
    public ProgressBar progressBar;

    public ImagePagerItemViewHolder(@NonNull View itemView) {
        super(itemView);
        viewPagerImage = itemView.findViewById(R.id.view_pager_image);
        viewPagerCard = itemView.findViewById(R.id.view_pager_card);
        progressBar = itemView.findViewById(R.id.progress_bar);
    }
}