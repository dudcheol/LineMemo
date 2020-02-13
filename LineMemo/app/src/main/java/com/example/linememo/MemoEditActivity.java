package com.example.linememo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
    public static final int CAMERA_REQUEST_CODE = 100;
    public static final int GALLERY_REQUEST_CODE = 200;

    private MemoViewModel viewModel;
    private EditText titleEdit;
    private EditText contentEdit;
    private RecyclerView imageRecyclerView;
    private ImageAdapter mAdapter;

    private int myViewMode;
    private Memo mMemoData;
    private List<String> mImageUris;

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
                            mImageUris,
                            System.currentTimeMillis()));
                } else if (myViewMode == MODIFY_MODE) {
                    mMemoData.setTitle(titleEdit.getText().toString());
                    mMemoData.setContent(contentEdit.getText().toString());
                    mMemoData.setImageUri(mImageUris);
                    mMemoData.setDate(System.currentTimeMillis());
                    viewModel.update(mMemoData);
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
        mMemoData = (Memo) intent.getExtras().get("memoData");

        titleEdit = findViewById(R.id.titleEdit);
        contentEdit = findViewById(R.id.contentEdit);
        imageRecyclerView = findViewById(R.id.imageRecycler);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        if (myViewMode == CREATE_MODE) {
            myToolbar.setTitle("메모 작성");
            mImageUris = new ArrayList<>();
        } else if (myViewMode == MODIFY_MODE) {
            myToolbar.setTitle("메모 수정");
            titleEdit.setText(mMemoData.getTitle());
            contentEdit.setText(mMemoData.getContent());
            mImageUris = mMemoData.getImageUri();
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

        mAdapter = new ImageAdapter(this, mImageUris);
        imageRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.e("MemoEdit", "onActivityResult RESULT OK");
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    Uri selectedImg = data.getData();
                    Log.e("MemoEdit-Result", data.getData().toString());
                    mAdapter.addImage(selectedImg.toString());
                    break;
                case CAMERA_REQUEST_CODE:
                    // 사용자가 카메라 intent에서 사진을 촬영하고 그것을 선택했다면 RESULT_OK이므로 이 곳에 진입
                    // RESULT_OK로 이곳에 진입했다는 것은 ImageAdapter에서 설정한 사진의 저장경로가 있다는 의미이므로
                    // getTakenPictureUri을 통해 새로 찍은 사진이 저장된 uri를 가져옴
                    mAdapter.addImage(mAdapter.getTakenPictureUri());
                    break;
            }
        } else {
            Log.e("MemoEdit", "onActivityResult RESULT NO");
        }
    }
}
