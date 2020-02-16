package com.example.linememo;

import android.content.Context;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AndroidUtil {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String longDateToShortString(long longDate) {
        Date date = new Date(longDate);
        return new SimpleDateFormat("yyyy.M.d").format(date);
    }

    public static String longDateToLongString(long longDate) {
        Date date = new Date(longDate);
        return new SimpleDateFormat("yyyy년 M월 d일 a h:mm").format(date);
    }
}
