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
    public static final int CREATE_MODE = 1000;
    public static final int MODIFY_MODE = 2000;

    private MemoViewModel viewModel;
    private EditText titleEdit;
    private EditText contentEdit;

    private int myViewMode;
    private Memo memoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        initSetting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memo_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                if (myViewMode == CREATE_MODE) {
                    viewModel.insert(new Memo(
                            titleEdit.getText().toString(),
                            contentEdit.getText().toString(),
                            "",
                            System.currentTimeMillis()));
                } else if (myViewMode == MODIFY_MODE) {
                    memoData.setTitle(titleEdit.getText().toString());
                    memoData.setContent(contentEdit.getText().toString());
                    memoData.setDate(System.currentTimeMillis());
                    viewModel.update(memoData);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void initSetting() {
        Intent intent = getIntent();
        myViewMode = intent.getIntExtra("mode", -1);
        memoData = (Memo) intent.getExtras().get("memoData");

        titleEdit = findViewById(R.id.titleEdit);
        contentEdit = findViewById(R.id.contentEdit);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        if (myViewMode == CREATE_MODE) {
            myToolbar.setTitle("메모 작성");
        } else if (myViewMode == MODIFY_MODE) {
            myToolbar.setTitle("메모 수정");
            titleEdit.setText(memoData.getTitle());
            contentEdit.setText(memoData.getContent());
        } else {
            // Todo 에러처리
        }
        setSupportActionBar(myToolbar);


        viewModel = new ViewModelProvider(this).get(MemoViewModel.class);
    }
//    int dateParser(long now){
//        Date date = new Date(now);
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//        return Integer.parseInt(simpleDateFormat.format(date));
//    }
}
