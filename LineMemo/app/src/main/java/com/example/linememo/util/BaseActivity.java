package com.example.linememo.util;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.example.linememo.R;
import com.example.linememo.view.activity.EditMemoActivity;
import com.example.linememo.view.animation.ActivityTransitionAnim;

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {
    public static final int MAIN_ACTIVITY = 0;
    public static final int DETAIL_ACTIVITY = 1;
    public static final int EDIT_ACTIVITY = 2;

    private static final String TAG = "BaseActivity";
    private static final int ERROR = -1;

    private T binding;
    private Toolbar toolbar;
    protected int editViewMode;
    protected Menu menu;

    protected abstract int getActivityType();

    protected abstract int getBackPressAnim();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setBinding(@LayoutRes int layoutResId) {
        if (binding == null) {
            binding = DataBindingUtil.setContentView(this, layoutResId);
        }
        setToolbar();
    }

    protected T getBinding() {
        return binding;
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);

        switch (getActivityType()) {
            case MAIN_ACTIVITY:
                toolbar.setTitle(R.string.app_name);
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorIconGreen));
                setSupportActionBar(toolbar);
                break;
            case DETAIL_ACTIVITY:
                toolbar.setTitle("");
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_keyboard_arrow_left_30dp);
                }
                toolbar.setNavigationOnClickListener(new BackButtonClick());
                break;
            case EDIT_ACTIVITY:
                editViewMode = getIntent().getIntExtra("mode", ERROR);
                if (editViewMode == EditMemoActivity.CREATE_MODE)
                    toolbar.setTitle(R.string.menu_write);
                else if (editViewMode == EditMemoActivity.MODIFY_MODE)
                    toolbar.setTitle(R.string.menu_edit);
                toolbar.setTitleTextColor(getResources().getColor(R.color.colorBackgroundNavy));
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_24dp);
                }
                toolbar.setNavigationOnClickListener(new BackButtonClick());
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        switch (getActivityType()) {
            case MAIN_ACTIVITY:
                getMenuInflater().inflate(R.menu.memo_list_menu, menu);
                break;
            case DETAIL_ACTIVITY:
                getMenuInflater().inflate(R.menu.memo_detail_menu, menu);
                break;
            case EDIT_ACTIVITY:
                menu.clear();
                break;
        }
        return true;
    }

    private class BackButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switch (getBackPressAnim()) {
            case MAIN_ACTIVITY:
                break;
            case DETAIL_ACTIVITY:
                ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.HIDE_DETAIL_PAGE);
                break;
            case EditMemoActivity.CREATE_MODE:
                ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.HIDE_NEW_PAGE);
                break;
            case EditMemoActivity.MODIFY_MODE:
                ActivityTransitionAnim.finishActivityWithAnim(this, ActivityTransitionAnim.FADE_TRANSITION);
                break;
        }
    }
}
