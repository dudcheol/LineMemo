package com.example.linememo.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.linememo.R;
import com.example.linememo.databinding.ActivityMainMemoBinding;
import com.example.linememo.util.BaseActivity;
import com.example.linememo.util.ConvertUtil;
import com.example.linememo.util.SnackbarPresenter;
import com.example.linememo.view.adapter.MemoAdapter;
import com.example.linememo.view.animation.ActivityTransitionAnim;
import com.example.linememo.viewmodel.MemoViewModel;

public class MainMemoActivity extends BaseActivity {
    public static final int CREATE_MEMO_REQUEST_CODE = 8000;
    public static final int DETAIL_DELETE_REQUEST_CODE = 9000;

    private static final String TAG = "MainMemoActivity";

    private int mCurrentRecyclerLayoutSpan;

    private ActivityMainMemoBinding mBinding;
    private MemoViewModel mViewModel;
    private MemoAdapter mAdapter;

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
        setBinding(R.layout.activity_main_memo);

        initSetting();
        showMemoList();
        initRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_mode:
                recyclerLayoutChange();
                return true;
            case R.id.write:
                startEditActivityForCreateMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initSetting() {
        mBinding = (ActivityMainMemoBinding) getBinding();
        mBinding.setLifecycleOwner(this);
        mViewModel = new ViewModelProvider(this).get(MemoViewModel.class);
        mBinding.setViewModel(mViewModel);

        mAdapter = new MemoAdapter(this);
        mCurrentRecyclerLayoutSpan = mViewModel.getSavedRecyclerLayoutState();
    }

    private void showMemoList() {
        mViewModel.getAll().observe(this, memos -> {
            if (memos.isEmpty()) mBinding.memoEmptyMessage.setVisibility(View.VISIBLE);
            else mBinding.memoEmptyMessage.setVisibility(View.GONE);
            mAdapter.setData(memos);
        });
        mViewModel.getCount();
    }

    private void initRecyclerView() {
        mBinding.recyclerView.setHasFixedSize(true);
        int divider = ConvertUtil.dpToPx(this, 10);
        mBinding.recyclerView.addItemDecoration(ConvertUtil.getRecyclerPaddingItemDeco(divider, divider, divider, divider));
        setRecyclerViewLayout(mCurrentRecyclerLayoutSpan);
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    private int setRecyclerViewLayout(int spanCount) {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mBinding.recyclerView.setLayoutManager(layoutManager);
        mAdapter.notifyDataSetChanged();
        return spanCount;
    }

    private void recyclerLayoutChange() {
        mCurrentRecyclerLayoutSpan = setRecyclerViewLayout(mCurrentRecyclerLayoutSpan == 2 ? 1 : 2);
        SnackbarPresenter.show(SnackbarPresenter.NORMAL, mBinding.memoListActivityLayout
                , R.string.memo_layout_change, SnackbarPresenter.LENGTH_SHORT);
    }

    private void startEditActivityForCreateMode() {
        Intent intent = new Intent(this, EditMemoActivity.class);
        intent.putExtra("mode", EditMemoActivity.CREATE_MODE);
        intent.putExtra("memoId", EditMemoActivity.CREATE_MODE);
        ActivityTransitionAnim.startActivityWithAnim(this, ActivityTransitionAnim.SHOW_NEW_PAGE, intent, CREATE_MEMO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case DETAIL_DELETE_REQUEST_CODE:
                    SnackbarPresenter.show(SnackbarPresenter.NORMAL, mBinding.memoListActivityLayout
                            , R.string.memo_deleted_snack, SnackbarPresenter.LENGTH_SHORT, 600);
                    break;
                case CREATE_MEMO_REQUEST_CODE:
                    mBinding.recyclerView.smoothScrollToPosition(0);
                    SnackbarPresenter.show(SnackbarPresenter.NORMAL, mBinding.memoListActivityLayout
                            , R.string.memo_created_snack, SnackbarPresenter.LENGTH_SHORT, 600);
                    break;
            }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewModel.saveRecyclerLayoutState(mCurrentRecyclerLayoutSpan);
    }
}
