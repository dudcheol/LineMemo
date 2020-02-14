package com.example.linememo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class DetailViewActivity extends AppCompatActivity {
    private RecyclerView imageRecyclerView;
    private ImageAdapter mAdapter;
    private ImageViewPagerAdapter mViewPagerAdapter;
    private TextView title;
    private TextView content;
    private ViewPager2 imageViewPager;
    private RelativeLayout imageArea;

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
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_30dp);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        imageViewPager = findViewById(R.id.image_view_pager);
        imageArea = findViewById(R.id.image_area);

        viewModel = new ViewModelProvider(this).get(MemoViewModel.class);
    }

    private void showMemo() {
        viewModel.find(memoId).observe(this, new Observer<Memo>() {
            @Override
            public void onChanged(Memo memo) {
                if (memo != null) {
                    title.setText(memo.getTitle());
                    content.setText(memo.getContent());
                    if (!memo.getImageUris().isEmpty()) {
                        mViewPagerAdapter.setImageUris(memo.getImageUris());
                        imageArea.setVisibility(View.VISIBLE);
                    } else imageArea.setVisibility(View.GONE);
                    memoData = memo;
                }
            }
        });
    }

    private void initImageRecyclerView() {
        mViewPagerAdapter = new ImageViewPagerAdapter(this, new ArrayList<String>());
        // offsetPx : 미리 보이길 원하는 다음 혹은 이전 이미지의 길이
        final float offsetPx = AndroidUtil.dpToPx(this, 45);
        imageViewPager.setClipToPadding(false);
        imageViewPager.setClipChildren(false);
        imageViewPager.setOffscreenPageLimit(3);
        imageViewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float offset = position * -(2 * offsetPx);
                if (imageViewPager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                    if (ViewCompat.getLayoutDirection(imageViewPager) == ViewCompat.LAYOUT_DIRECTION_RTL)
                        page.setTranslationX(-offset);
                    else page.setTranslationX(offset);
                } else page.setTranslationY(offset);
            }
        });
        imageViewPager.setAdapter(mViewPagerAdapter);


//        mAdapter = new ImageAdapter(this, new ArrayList<String>(), ImageAdapter.IMAGE_ADAPTER_VIEW_MODE);
//        imageRecyclerView.setAdapter(mAdapter);
    }
}
