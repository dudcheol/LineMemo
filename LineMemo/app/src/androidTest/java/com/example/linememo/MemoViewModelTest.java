package com.example.linememo;

import android.app.Application;
import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.linememo.viewmodel.MemoViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MemoViewModelTest {
    private String TAG = "MemoViewModelTest : ";
    private MemoViewModel mMemoViewModel;

    @Before
    public void init() {
        mMemoViewModel = new MemoViewModel((Application) ApplicationProvider.getApplicationContext());
    }

    @Test
    public void saveLayoutTest() {
        mMemoViewModel.saveRecyclerLayoutState(1);
        Assert.assertEquals(1, mMemoViewModel.getSavedRecyclerLayoutState());
    }

    @Test
    public void appearTest() {
        Assert.assertEquals(View.GONE, mMemoViewModel.hasAppear(null));
        Assert.assertEquals(View.GONE, mMemoViewModel.hasAppear(new ArrayList<String>()));
        List<String> list = new ArrayList<>();
        list.add("test");
        Assert.assertEquals(View.VISIBLE, mMemoViewModel.hasAppear(list));
    }
}
