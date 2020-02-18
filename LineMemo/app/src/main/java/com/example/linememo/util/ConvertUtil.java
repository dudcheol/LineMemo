package com.example.linememo.util;

import android.content.Context;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  데이터 변환 클래스
 */

public class ConvertUtil {

    /**
     * dp size를 px size로 변환
     *
     * @param context
     * @param dp
     * @return
     */

    public static int dpToPx(Context context, int dp) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    /**
     * px size를 dp size로 변환
     *
     * @param context
     * @param px
     * @return
     */

    public static int pxToDp(Context context, int px) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    /**
     * long형 날짜 데이터를 String형으로 변환
     *
     * @param longDate
     * @return "2020.1.23"
     */

    public static String longDateToShortString(long longDate) {

        Date date = new Date(longDate);

        return new SimpleDateFormat("yyyy.M.d").format(date);
    }


    /**
     * long형 날짜 데이터를 String형으로 변환
     *
     * @param longDate
     * @return "2020년 1월 23일 오후 4:56"
     */

    public static String longDateToLongString(long longDate) {

        Date date = new Date(longDate);

        return new SimpleDateFormat("yyyy년 M월 d일 a h:mm").format(date);
    }
}
