package com.example.linememo.view.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.linememo.R;

public class MemoItemViewholder extends RecyclerView.ViewHolder {
    public TextView title;
    public TextView content;
    public ImageView thumbnail;
    public CardView card;
    public TextView date;

    public MemoItemViewholder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.item_title);
        content = itemView.findViewById(R.id.item_content);
        thumbnail = itemView.findViewById(R.id.item_thumbnail);
        card = itemView.findViewById(R.id.item_card);
        date = itemView.findViewById(R.id.item_date);
    }
}

