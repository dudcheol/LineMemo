package com.example.linememo.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.linememo.R;
import com.example.linememo.databinding.ActivityDetailMemoBinding;
import com.example.linememo.util.BaseActivity;
import com.example.linememo.util.ConvertUtil;
import com.example.linememo.util.DialogUtil;
import com.example.linememo.view.adapter.ImageViewPagerAdapter;
import com.example.linememo.view.animation.ActivityTransitionAnim;
import com.example.linememo.viewmodel.MemoViewModel;

import java.util.ArrayList;

public class DetailMemoActivity extends BaseActivity {
    private static final int ERROR = -1;

    private ImageViewPagerAdapter mViewPagerAdapter;

    private MemoViewModel mViewModel;
    private int mMemoId;
    private ActivityDetailMemoBinding mBinding;

    @Override
    protected int getActivityType() {
        return BaseActivity.DETAIL_ACTIVITY;
    }

    @Override
    protected int getBackPressAnim() {
        return DETAIL_ACTIVITY;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBinding(R.layout.activity_detail_memo);

        initSetting();
        showMemo();
        initImageRecyclerView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                changeEditMode();
                return true;
            case R.id.delete:
                showDeleteDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initSetting() {
        mBinding = (ActivityDetailMemoBinding) getBinding();
        mBinding.setLifecycleOwner(this);
        mViewModel = new ViewModelProvider(this).get(MemoViewModel.class);
        mMemoId = getIntent().getIntExtra("memoId", ERROR);
        if (mMemoId == ERROR) DialogUtil.showErrDialog(this);

        mBinding.memoArea.setOnClickListener(new startEditActivityButtonClick());
    }

    private void showMemo() {
        mViewModel.findLive(mMemoId).observe(this, memo -> {
            if (memo != null) {
                mViewPagerAdapter.setData(memo.getImageUris());
                mBinding.imageViewPager.setAdapter(mViewPagerAdapter);
            }
        });
        mBinding.setViewModel(mViewModel);
    }

    private void initImageRecyclerView() {
        mViewPagerAdapter = new ImageViewPagerAdapter(this, new ArrayList<String>());
        // offsetPx : 미리 보이길 원하는 다음 혹은 이전 이미지의 길이
        final float offsetPx = ConvertUtil.dpToPx(this, 45);
        mBinding.imageViewPager.setClipToPadding(false);
        mBinding.imageViewPager.setClipChildren(false);
        mBinding.imageViewPager.setOffscreenPageLimit(3);
        mBinding.imageViewPager.setPageTransformer((page, position) -> {
            float offset = position * -(2 * offsetPx);
            if (mBinding.imageViewPager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                if (ViewCompat.getLayoutDirection(mBinding.imageViewPager) == ViewCompat.LAYOUT_DIRECTION_RTL)
                    page.setTranslationX(-offset);
                else page.setTranslationX(offset);
            } else page.setTranslationY(offset);
        });
        mBinding.imageViewPager.setAdapter(mViewPagerAdapter);
    }

    private class startEditActivityButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            changeEditMode();
        }
    }

    private void changeEditMode() {
        Intent intent = new Intent(this, EditMemoActivity.class);
        intent.putExtra("mode", EditMemoActivity.MODIFY_MODE);
        intent.putExtra("memoId", mMemoId);
        ActivityTransitionAnim.startActivityWithAnim(this, ActivityTransitionAnim.FADE_TRANSITION, intent);
    }

    private void showDeleteDialog() {
        DialogUtil.showDialog(this
                , R.drawable.ic_warning_24dp
                , getResources().getString(R.string.memo_delete_alert)
                , null
                , getResources().getString(R.string.positiveBtn)
                , getResources().getString(R.string.negativeBtn)
                , (dialog, which) -> {
                    mViewModel.delete(mViewModel.find(mMemoId));
                    setResult(RESULT_OK);
                    onBackPressed();
                }
                , DialogUtil.onClickCancelListener);
    }
}
