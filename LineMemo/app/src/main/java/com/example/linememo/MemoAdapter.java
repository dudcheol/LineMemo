package com.example.linememo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ItemViewHolder> {
    private Context mContext;
    private List<Memo> mDataset;

    public MemoAdapter(Context context) {
        mContext = context;
        mDataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memo_list_item, parent, false);
        ItemViewHolder iv = new ItemViewHolder(v);
        return iv;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, final int position) {
        holder.title.setText(mDataset.get(position).getTitle());
        holder.content.setText(mDataset.get(position).getContent());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailViewActivity.class);
                intent.putExtra("memoData",mDataset.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView content;
        private ImageView thumbnail;
        private CardView card;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemTitle);
            content = itemView.findViewById(R.id.itemContent);
            thumbnail = itemView.findViewById(R.id.itemThumbnail);
            card = itemView.findViewById(R.id.card);
        }
    }

    public void setData(List<Memo> newData) {
        Collections.sort(newData, new Comparator<Memo>() {
            @Override
            public int compare(Memo memo, Memo t1) {
                return -Long.compare(memo.getDate(), t1.getDate());
            }
        });
        this.mDataset = newData;
        notifyDataSetChanged();
    }
}
