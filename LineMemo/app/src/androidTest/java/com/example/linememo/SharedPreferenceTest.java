package com.example.linememo;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.linememo.util.SharedPreferenceManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SharedPreferenceTest {
    Context context;

    @Before
    public void createContext() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void SharedPreferenceTest() {
        SharedPreferenceManager.setInt(context, "key", 123);
        Assert.assertEquals(123, SharedPreferenceManager.getInt(context, "key"));
    }
}
