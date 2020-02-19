package com.example.linememo.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.linememo.R;
import com.example.linememo.model.Memo;
import com.example.linememo.util.ConvertUtil;
import com.example.linememo.view.adapter.ImageViewPagerAdapter;
import com.example.linememo.view.animation.ActivityTransitionAnim;
import com.example.linememo.viewmodel.MemoViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class DetailMemoActivity extends AppCompatActivity {
    private static final int ERROR = -1;

    private ImageViewPagerAdapter mViewPagerAdapter;
    private TextView title;
    private TextView content;
    private TextView date;
    private ViewPager2 imageViewPager;
    private RelativeLayout imageArea;
    private RelativeLayout memoArea;

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
        switch (item.getItemId()) {
            case R.id.edit:
                changeEditMode();
                return true;
            case R.id.delete:
                createDeleteAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initSetting() {
        findViewByIds();
        initData();
        setToolbar();
        setListener();
    }

    private void initData() {
        memoId = getIntent().getIntExtra("memoId", ERROR);
        viewModel = new ViewModelProvider(this).get(MemoViewModel.class);
    }

    private void findViewByIds() {
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        imageViewPager = findViewById(R.id.image_view_pager);
        imageArea = findViewById(R.id.image_area);
        memoArea = findViewById(R.id.memo_area);
        date = findViewById(R.id.date);
    }

    private void setToolbar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle("");
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_30dp);
        }
        myToolbar.setNavigationOnClickListener(new BackButtonClick());
    }

    private void setListener() {
        memoArea.setOnClickListener(new MoveEditActivityButtonClick());
    }

    private void showMemo() {
        if (memoId == ERROR) {
            onBackPressed();
            return;
        }
        viewModel.find(memoId).observe(this, new Observer<Memo>() {
            @Override
            public void onChanged(Memo memo) {
                if (memo != null) {
                    title.setText(memo.getTitle());
                    content.setText(memo.getContent());
                    date.setText(ConvertUtil.longDateToLongString(memo.getDate()));
                    if (!memo.getImageUris().isEmpty()) {
                        mViewPagerAdapter.setData(memo.getImageUris());
                        imageViewPager.setAdapter(mViewPagerAdapter);
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
        final float offsetPx = ConvertUtil.dpToPx(this, 45);
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
    }

    private void createDeleteAlert() {
        new MaterialAlertDialogBuilder(this)
                .setIcon(R.drawable.ic_warning_24dp)
                .setTitle(R.string.memo_delete_alert)
                .setPositiveButton(R.string.positiveBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewModel.delete(memoData);
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                })
                .setNegativeButton(R.string.negativeBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void changeEditMode() {
        Intent intent = new Intent(this, EditMemoActivity.class);
        intent.putExtra("mode", EditMemoActivity.MODIFY_MODE);
        intent.putExtra("memoData", memoData);
        ActivityTransitionAnim.startActivityWithAnim(this, ActivityTransitionAnim.FADE_TRANSITION, intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.HIDE_DETAIL_PAGE);
    }

    private class BackButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    }

    private class MoveEditActivityButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            changeEditMode();
        }
    }
}
