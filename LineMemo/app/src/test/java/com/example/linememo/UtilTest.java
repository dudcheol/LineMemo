package com.example.linememo;

import android.content.Context;

import com.example.linememo.util.ConvertUtil;

import org.junit.Test;


public class UtilTest {
    String TAG = "utilTest = ";

    // convert util check
    // 날짜변환
    @Test
    public void dateConverText() {
        String res = ConvertUtil.longDateToShortString(System.currentTimeMillis());
        System.out.println(TAG + res);
    }

    @Test
    public void dateConverLongText() {
        String res = ConvertUtil.longDateToLongString(System.currentTimeMillis());
        System.out.println(TAG + res);
    }
}
