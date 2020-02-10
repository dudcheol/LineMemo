package com.example.linememo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MemoEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        initDisplay();
    }

    void initDisplay() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle("메모 작성");
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);
    }
}
