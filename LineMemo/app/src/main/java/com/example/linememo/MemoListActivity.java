package com.example.linememo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MemoListActivity extends AppCompatActivity {
    public static final String MEMO_LIST_VIEW_MODE_KEY = "viewMode";
    public static final int CREATE_MEMO_REQUEST_CODE = 8000;
    public static final int DETAIL_DELETE_REQUEST_CODE = 9000;
    private MemoViewModel memoViewModel;
    private RecyclerView recyclerView;
    private MemoAdapter mAdapter;
    private LinearLayout memoEmptyMessage;
    private Toolbar myToolbar;
    private Menu menu;

    private int divider;
    private int currentSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_list);

        initSetting();
        initRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.memo_list_menu, menu);
        changeViewModeMenuIcon(currentSpan);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.view_mode:
                changeRecyclerViewLayout(currentSpan == 2 ? 1 : 2);
                changeViewModeMenuIcon(currentSpan);
                return true;
            case R.id.write:
                intent = new Intent(this, MemoEditActivity.class);
                intent.putExtra("mode", MemoEditActivity.CREATE_MODE);
                ActivityTransitionAnim.startActivityWithAnim(this
                        , ActivityTransitionAnim.SHOW_NEW_PAGE
                        , intent
                        , CREATE_MEMO_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void initSetting() {
        recyclerView = findViewById(R.id.recycler);
        memoEmptyMessage = findViewById(R.id.memo_empty_message);

        myToolbar = findViewById(R.id.toolbar);
        myToolbar.setTitle("LINE MEMO");
        myToolbar.setTitleTextColor(getResources().getColor(R.color.colorIconGreen));
        setSupportActionBar(myToolbar);

        memoViewModel = new ViewModelProvider(this).get(MemoViewModel.class);
        divider = AndroidUtil.dpToPx(this, 10);

        int savedSpan = SharedPreferenceManager.getInt(this, MEMO_LIST_VIEW_MODE_KEY);
        currentSpan = savedSpan == -1 ? 2 : savedSpan;
    }

    void initRecyclerView() {
        // 레이아웃 사이즈 설정
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);
        mAdapter = new MemoAdapter(this);

        // 사용하는 레이아웃 = StaggeredGridLayoutManager
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(currentSpan, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        memoViewModel.getAll().observe(this, new Observer<List<Memo>>() {
            @Override
            public void onChanged(List<Memo> memos) {
                if (memos.isEmpty()) {
                    menu.getItem(0).setVisible(false);
                    memoEmptyMessage.setVisibility(View.VISIBLE);
                } else {
                    menu.getItem(0).setVisible(true);
                    memoEmptyMessage.setVisibility(View.GONE);
                }
                mAdapter.setData(memos);
            }
        });
    }

    void changeRecyclerViewLayout(int spanCount) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter.notifyDataSetChanged();
        currentSpan = spanCount;
        SharedPreferenceManager.setInt(this, MEMO_LIST_VIEW_MODE_KEY, spanCount);
    }

    void changeViewModeMenuIcon(int spanCount) {
        if (spanCount == 1)
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_grid_on_24dp));
        else
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_grid_off_24dp));
    }

    private RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getPaddingLeft() != divider) {
                parent.setPadding(divider, divider, divider, divider);
                parent.setClipToPadding(false);
            }
            outRect.top = divider;
            outRect.bottom = divider;
            outRect.left = divider;
            outRect.right = divider;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case DETAIL_DELETE_REQUEST_CODE:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(R.id.memo_list_activity_layout), R.string.memo_deleted_snack, Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(getResources().getColor(R.color.colorIconNavy))
                                    .show();
                        }
                    }, 600);
                    break;
                case CREATE_MEMO_REQUEST_CODE:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(R.id.memo_list_activity_layout), R.string.memo_created_snack, Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(getResources().getColor(R.color.colorIconNavy))
                                    .show();
                            recyclerView.smoothScrollToPosition(0);
                        }
                    }, 600);
                    break;
            }
        }
    }
}
