package com.example.linememo;

import android.app.Activity;
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

import com.bumptech.glide.Glide;

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
        String title = mDataset.get(position).getTitle().trim();
        String content = mDataset.get(position).getContent().trim();
        List<String> uris = mDataset.get(position).getImageUris();

        if (title.length() != 0) {
            holder.title.setText(title);
            holder.title.setVisibility(View.VISIBLE);
        } else holder.title.setVisibility(View.GONE);

        if (content.length() != 0) {
            holder.content.setText(content);
            holder.content.setVisibility(View.VISIBLE);
        } else holder.content.setVisibility(View.GONE);

        if (uris.size() != 0)
            Glide.with(mContext)
                    .load(uris.get(0))
                    .into(holder.thumbnail);
        else
            // 변경에 대해서 명시적으로 삭제해주어야 함
            Glide.with(mContext)
                    .clear(holder.thumbnail);

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailViewActivity.class);
                intent.putExtra("memoId", mDataset.get(position).getId());
                ActivityTransitionAnim.startActivityWithAnim((Activity) mContext, ActivityTransitionAnim.SHOW_DETAIL_PAGE, intent);
                Log.e("Memo_Item_Selected", mDataset.get(position).toString());
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

            title = itemView.findViewById(R.id.item_title);
            content = itemView.findViewById(R.id.item_content);
            thumbnail = itemView.findViewById(R.id.item_thumbnail);
            card = itemView.findViewById(R.id.item_card);
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
