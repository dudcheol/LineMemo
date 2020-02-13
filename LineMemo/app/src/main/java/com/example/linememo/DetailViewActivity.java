package com.example.linememo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DetailViewActivity extends AppCompatActivity {
    private RecyclerView imageRecyclerView;
    private ImageAdapter mAdapter;
    private TextView title;
    private TextView content;

    private MemoViewModel viewModel;
    private int memoId;
    private Memo memoData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        initSetting();
        showMemo();
        initImageRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memo_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.edit:
                intent = new Intent(this, MemoEditActivity.class);
                intent.putExtra("mode", MemoEditActivity.MODIFY_MODE);
                intent.putExtra("memoData", memoData);
                startActivity(intent);
                return true;
            case R.id.delete:
                // Todo 정말 삭제하겠냐는 alert 메시지 띄움
                viewModel.delete(memoData);
                Toast.makeText(this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initSetting() {
        Intent intent = getIntent();
        memoId = intent.getIntExtra("memoId", -1);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle("메모 상세 보기");
        myToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(myToolbar);

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        imageRecyclerView = findViewById(R.id.imageRecycler);

        viewModel = new ViewModelProvider(this).get(MemoViewModel.class);
    }

    private void showMemo() {
        viewModel.find(memoId).observe(this, new Observer<Memo>() {
            @Override
            public void onChanged(Memo memo) {
                if (memo != null) {
                    title.setText(memo.getTitle());
                    content.setText(memo.getContent());
                    mAdapter.setImageUris(memo.getImageUris());
                    memoData = memo;
                }
            }
        });
    }

    private void initImageRecyclerView() {
        imageRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        imageRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ImageAdapter(this, new ArrayList<String>(), ImageAdapter.IMAGE_ADAPTER_VIEW_MODE);
        imageRecyclerView.setAdapter(mAdapter);
    }
}
