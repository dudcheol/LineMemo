package com.example.linememo;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.linememo.viewmodel.EditViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class EditViewModelTest {
    private String TAG = "EditViewModelTest : ";
    private EditViewModel mEditViewModel;

    @Before
    public void init() {
        mEditViewModel = new EditViewModel((Application) ApplicationProvider.getApplicationContext());
    }

    @Test
    public void createImageFineTest() throws IOException {
        File file = mEditViewModel.createImageFile();
        Uri uri = mEditViewModel.createImageUri(file);
        Log.e(TAG, file.toString());
        Log.e(TAG, uri.toString());

        // 뷰모델에 정상적으로 uri가 저장되는지 확인
        Assert.assertEquals(uri.toString(), mEditViewModel.getUri());
    }

    @Test
    public void checkStringTest() {
        String[] s = {"test1", "test2", "test3"};
        List<String> list = Arrays.asList(s);

        // isMemoStorable TEST
        {
            // 배열과 리스트에 데이터가 있음
            Assert.assertTrue(mEditViewModel.isMemoStorable(s, list));

            // 배열만 데이터가 없음
            Assert.assertTrue(mEditViewModel.isMemoStorable(new String[]{}, list));

            // 리스트만 데이터가 없음
            Assert.assertTrue(mEditViewModel.isMemoStorable(s, new ArrayList<String>()));

            // 배열과 리스트에 데이터가 없음
            Assert.assertFalse(mEditViewModel.isMemoStorable(new String[]{}, new ArrayList<String>()));

            // 배열에 '빈 칸' 만 있고, 리스트에 데이터가 없음
            Assert.assertFalse(mEditViewModel.isMemoStorable(new String[]{"     "}, new ArrayList<String>()));
        }

        //  getTextPassOrNot TEST
        {
            // 텍스트의 길이가 0이 아님
            Assert.assertTrue(mEditViewModel.getTextPassOrNot("test"));

            // 텍스트에 빈 칸만 있음
            Assert.assertFalse(mEditViewModel.getTextPassOrNot("    "));

            // 텍스트가 비었음 & null임
            Assert.assertFalse(mEditViewModel.getTextPassOrNot(new String()));
        }
    }
}
