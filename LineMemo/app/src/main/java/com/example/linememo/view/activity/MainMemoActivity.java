package com.example.linememo.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.linememo.R;
import com.example.linememo.model.Memo;
import com.example.linememo.util.BaseActivity;
import com.example.linememo.util.ConvertUtil;
import com.example.linememo.util.SnackbarPresenter;
import com.example.linememo.view.adapter.MemoAdapter;
import com.example.linememo.view.animation.ActivityTransitionAnim;
import com.example.linememo.viewmodel.MemoViewModel;

import java.util.List;

public class MainMemoActivity extends BaseActivity {
    public static final int CREATE_MEMO_REQUEST_CODE = 8000;
    public static final int DETAIL_DELETE_REQUEST_CODE = 9000;

    private static final String TAG = "MainMemoActivity";

    private MemoViewModel memoViewModel;
    private RecyclerView recyclerView;
    private MemoAdapter mAdapter;
    private LinearLayout memoEmptyMessage;

    private int currentRecyclerLayoutSpan;
    private RelativeLayout memoListActivityLayout;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main_memo;
    }

    @Override
    protected int getActivityType() {
        return MAIN_ACTIVITY;
    }

    @Override
    protected int getBackPressAnim() {
        return MAIN_ACTIVITY;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSetting();
        initRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_mode:
                currentRecyclerLayoutSpan = setRecyclerViewLayout(currentRecyclerLayoutSpan == 2 ? 1 : 2);
                return true;
            case R.id.write:
                Intent intent = new Intent(this, EditMemoActivity.class);
                intent.putExtra("mode", EditMemoActivity.CREATE_MODE);
                intent.putExtra("memoId", EditMemoActivity.CREATE_MODE);
                ActivityTransitionAnim.startActivityWithAnim(this
                        , ActivityTransitionAnim.SHOW_NEW_PAGE
                        , intent
                        , CREATE_MEMO_REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initSetting() {
        findViewByIds();
        initData();
    }

    private void initData() {
        mAdapter = new MemoAdapter(this);
        memoViewModel = new ViewModelProvider(this).get(MemoViewModel.class);
        currentRecyclerLayoutSpan = memoViewModel.getSavedRecyclerLayoutState();
        setRecyclerViewLayout(currentRecyclerLayoutSpan);
    }

    private void findViewByIds() {
        memoListActivityLayout = findViewById(R.id.memo_list_activity_layout);
        recyclerView = findViewById(R.id.recycler);
        memoEmptyMessage = findViewById(R.id.memo_empty_message);
    }

    private void initRecyclerView() {
        recyclerView.setHasFixedSize(true);
        int divider = ConvertUtil.dpToPx(this, 10);
        recyclerView.addItemDecoration(ConvertUtil.getRecyclerPaddingItemDeco(divider, divider, divider, divider));
        setRecyclerViewLayout(currentRecyclerLayoutSpan);
        recyclerView.setAdapter(mAdapter);

        memoViewModel.getAll().observe(this, new Observer<List<Memo>>() {
            @Override
            public void onChanged(List<Memo> memos) {
                if (memos.isEmpty()) {
                    if (menu != null) menu.findItem(R.id.view_mode).setVisible(false);
                    memoEmptyMessage.setVisibility(View.VISIBLE);
                } else {
                    if (menu != null) menu.findItem(R.id.view_mode).setVisible(true);
                    memoEmptyMessage.setVisibility(View.GONE);
                }
                mAdapter.setData(memos);
            }
        });
    }

    private int setRecyclerViewLayout(int spanCount) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter.notifyDataSetChanged();
        return spanCount;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case DETAIL_DELETE_REQUEST_CODE:
                    SnackbarPresenter.show(SnackbarPresenter.NORMAL
                            , memoListActivityLayout
                            , R.string.memo_deleted_snack
                            , SnackbarPresenter.LENGTH_SHORT
                            , 600);
                    break;
                case CREATE_MEMO_REQUEST_CODE:
                    recyclerView.smoothScrollToPosition(0);
                    SnackbarPresenter.show(SnackbarPresenter.NORMAL
                            , memoListActivityLayout
                            , R.string.memo_created_snack
                            , SnackbarPresenter.LENGTH_SHORT
                            , 600);
                    break;
            }
    }

    @Override
    protected void onStop() {
        super.onStop();
        memoViewModel.saveRecyclerLayoutState(currentRecyclerLayoutSpan);
    }
}
