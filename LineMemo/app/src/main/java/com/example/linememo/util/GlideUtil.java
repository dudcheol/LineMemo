package com.example.linememo.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.example.linememo.R;

public class GlideUtil {
    public static void show(Context context, String uri, int[] override, ImageView imageView) {
        Glide.with(context)
                .load(uri)
                .override(override[0], override[1])
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_unknown_50dp)
                .into(imageView);
    }

    public static void showAddReqListener(Context context, String uri, int[] override, ImageView imageView, RequestListener<Drawable> listener, boolean cacheStrategy) {
        if (cacheStrategy)
            Glide.with(context)
                    .load(uri)
                    .override(override[0], override[1])
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .listener(listener)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_unknown_50dp)
                    .into(imageView);
        else
            Glide.with(context)
                    .load(uri)
                    .override(override[0], override[1])
                    .listener(listener)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_unknown_50dp)
                    .into(imageView);
    }

    public static void clear(Context context, ImageView imageView) {
        Glide.with(context)
                .clear(imageView);
    }
}
