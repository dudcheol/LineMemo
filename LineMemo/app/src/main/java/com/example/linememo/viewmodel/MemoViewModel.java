package com.example.linememo.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.linememo.model.Memo;
import com.example.linememo.repository.MemoRepository;
import com.example.linememo.util.SharedPreferenceManager;

import java.util.List;

public class MemoViewModel extends AndroidViewModel {
    public static final String MEMO_LIST_VIEW_MODE_KEY = "viewMode";
    private MemoRepository mRepository;

    public MemoViewModel(Application application) {
        super(application);
        mRepository = new MemoRepository(application);
    }

    public LiveData<List<Memo>> getAll() {
        return mRepository.getAll();
    }

    public LiveData<Memo> find(int id) {
        return mRepository.find(id);
    }

    public void insert(Memo memo) {
        mRepository.insert(memo);
    }

    public void delete(Memo memo) {
        mRepository.delete(memo);
    }

    public void update(Memo memo) {
        mRepository.update(memo);
    }

    public void saveRecyclerLayoutState(int spanCount) {
        SharedPreferenceManager.setInt(getApplication(), MEMO_LIST_VIEW_MODE_KEY, spanCount);
    }

    public int getSavedRecyclerLayoutState() {
        int savedSpan = SharedPreferenceManager.getInt(getApplication(), MEMO_LIST_VIEW_MODE_KEY);
        return savedSpan == -1 ? 2 : savedSpan;
    }
}
