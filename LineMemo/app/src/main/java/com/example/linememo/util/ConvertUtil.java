package com.example.linememo.util;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 데이터 변환 클래스
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


    /**
     * EditText에 적혀있는 글자를 String으로 변환하고 공백제거
     *
     * @param editText
     * @return
     */

    public static String getString(EditText editText) {
        return editText.getText().toString().trim();
    }


    /**
     * RecyclerView 겹치지 않고 일정한 Padding으로 변환해주는 itemDecoration
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */

    public static RecyclerView.ItemDecoration getRecyclerPaddingItemDeco(final int left, final int top, final int right, final int bottom) {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
                if (parent.getPaddingLeft() != left) {
                    parent.setPadding(left, top, right, bottom);
                    parent.setClipToPadding(false);
                }
                outRect.top = top;
                outRect.bottom = bottom;
                outRect.left = left;
                outRect.right = right;
            }
        };
    }
}
