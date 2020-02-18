package com.example.linememo.util;

import android.os.Handler;
import android.view.View;

import androidx.annotation.StringRes;

import com.example.linememo.R;
import com.google.android.material.snackbar.Snackbar;

public class SnackbarPresenter {
    public static final int ERROR = 0;
    public static final int NORMAL = 1;

    private static final int[] MODE_COLOR = {R.color.colorErr, R.color.colorIconNavy};

    public static void show(int mode, View v, @StringRes int msg, int duration) {
        Snackbar.make(v, msg, duration)
                .setBackgroundTint(v.getResources().getColor(MODE_COLOR[mode]))
                .show();
    }

    public static void show(final int mode, final View v, @StringRes final int msg, final int duration, int delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(v, msg, duration)
                        .setBackgroundTint(v.getResources().getColor(MODE_COLOR[mode]))
                        .show();
            }
        }, delay);
    }
}
