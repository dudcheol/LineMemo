package com.example.linememo.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import com.example.linememo.viewmodel.MemoViewModel;
import com.example.linememo.R;
import com.example.linememo.view.adapter.MemoAdapter;
import com.example.linememo.model.Memo;
import com.example.linememo.view.animation.ActivityTransitionAnim;
import com.example.linememo.util.ConvertUtil;
import com.example.linememo.util.SharedPreferenceManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainMemoActivity extends AppCompatActivity {
    public static final String MEMO_LIST_VIEW_MODE_KEY = "viewMode";
    public static final int CREATE_MEMO_REQUEST_CODE = 8000;
    public static final int DETAIL_DELETE_REQUEST_CODE = 9000;

    private static final String TAG = "MainMemoActivity";

    private MemoViewModel memoViewModel;
    private RecyclerView recyclerView;
    private MemoAdapter mAdapter;
    private LinearLayout memoEmptyMessage;
    private StaggeredGridLayoutManager layoutManager;
    private Toolbar myToolbar;
    private Menu menu;

    private int currentRecyclerLayoutSpan;

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
        changeViewModeMenuIcon(currentRecyclerLayoutSpan);
        Log.e(TAG, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_mode:
                currentRecyclerLayoutSpan = changeRecyclerViewLayout(currentRecyclerLayoutSpan == 2 ? 1 : 2);
                changeViewModeMenuIcon(currentRecyclerLayoutSpan);
                return true;
            case R.id.write:
                Intent intent = new Intent(this, EditMemoActivity.class);
                intent.putExtra("mode", EditMemoActivity.CREATE_MODE);
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
        myToolbar.setTitle(R.string.app_name);
        myToolbar.setTitleTextColor(getResources().getColor(R.color.colorIconGreen));
        setSupportActionBar(myToolbar);

        currentRecyclerLayoutSpan = getSavedRecyclerLayoutState();
        memoViewModel = new ViewModelProvider(this).get(MemoViewModel.class);
    }

    void initRecyclerView() {
        // 레이아웃 사이즈 설정
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(itemDecoration);
        mAdapter = new MemoAdapter(this);

        // 사용하는 레이아웃 = StaggeredGridLayoutManager
        layoutManager = new StaggeredGridLayoutManager(currentRecyclerLayoutSpan, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        memoViewModel.getAll().observe(this, new Observer<List<Memo>>() {
            @Override
            public void onChanged(List<Memo> memos) {
                if (memos.isEmpty()) {
                    if (menu != null) menu.getItem(0).setVisible(false);
                    memoEmptyMessage.setVisibility(View.VISIBLE);
                } else {
                    if (menu != null) menu.getItem(0).setVisible(true);
                    memoEmptyMessage.setVisibility(View.GONE);
                }
                mAdapter.setData(memos);
            }
        });
    }

    int changeRecyclerViewLayout(int spanCount) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter.notifyDataSetChanged();
        return spanCount;
    }

    void changeViewModeMenuIcon(int spanCount) {
        if (spanCount == 1)
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_grid_on_24dp));
        else
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_grid_off_24dp));
    }

    int getSavedRecyclerLayoutState() {
        int savedSpan = SharedPreferenceManager.getInt(this, MEMO_LIST_VIEW_MODE_KEY);
        return savedSpan == -1 ? 2 : savedSpan;
    }

    void saveViewState(int spanCount) {
        SharedPreferenceManager.setInt(this, MEMO_LIST_VIEW_MODE_KEY, spanCount);
    }

    private RecyclerView.ItemDecoration itemDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int divider = ConvertUtil.dpToPx(getApplicationContext(), 10);
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

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop()");
        saveViewState(currentRecyclerLayoutSpan);
    }
}
