package com.example.linememo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MemoEditActivity extends AppCompatActivity {
    public static final int CREATE_MODE = 1000;
    public static final int MODIFY_MODE = 2000;
    public static final int GALLERY_REQUEST_CODE = 100;

    private MemoViewModel viewModel;
    private EditText titleEdit;
    private EditText contentEdit;
    private RecyclerView imageRecyclerView;
    private ImageAdapter mAdapter;

    private int myViewMode;
    private Memo memoData;
    private List<String> imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_edit);

        initSetting();
        initImageRecyclerView();
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
                            imageUri,
                            System.currentTimeMillis()));
                } else if (myViewMode == MODIFY_MODE) {
                    memoData.setTitle(titleEdit.getText().toString());
                    memoData.setContent(contentEdit.getText().toString());
                    memoData.setImageUri(imageUri);
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
        imageRecyclerView = findViewById(R.id.imageRecycler);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        if (myViewMode == CREATE_MODE) {
            myToolbar.setTitle("메모 작성");
            imageUri = new ArrayList<>();
        } else if (myViewMode == MODIFY_MODE) {
            myToolbar.setTitle("메모 수정");
            titleEdit.setText(memoData.getTitle());
            contentEdit.setText(memoData.getContent());
            imageUri = memoData.getImageUri();
        } else {
            // Todo 에러처리
        }
        setSupportActionBar(myToolbar);

        viewModel = new ViewModelProvider(this).get(MemoViewModel.class);
    }

    void initImageRecyclerView() {
        imageRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageRecyclerView.setLayoutManager(layoutManager);

        // 임시데이터
        mAdapter = new ImageAdapter(this, imageUri);
        imageRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImg = data.getData();
                mAdapter.addImage(selectedImg.toString());
            }
        }
    }
}
