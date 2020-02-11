package com.example.linememo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

public class MemoEditActivity extends AppCompatActivity {
    private MemoViewModel viewModel;
    private EditText titleEdit;
    private EditText contentEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        findView();
        initSetting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memo_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.save:
                viewModel.insert(new Memo(titleEdit.getText().toString(), contentEdit.getText().toString(), ""));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void findView() {
        titleEdit = findViewById(R.id.titleEdit);
        contentEdit = findViewById(R.id.contentEdit);
    }

    void initSetting() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle("메모 작성");
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        viewModel = new ViewModelProvider(this).get(MemoViewModel.class);
    }
}
