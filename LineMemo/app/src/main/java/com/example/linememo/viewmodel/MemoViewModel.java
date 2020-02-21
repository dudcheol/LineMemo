package com.example.linememo.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.linememo.model.Memo;
import com.example.linememo.repository.MemoRepository;

import java.util.List;

public class MemoViewModel extends AndroidViewModel {
    private MemoRepository mRepository;

    public MemoViewModel(Application application) {
        super(application);
        mRepository = new MemoRepository(application);
    }

    public LiveData<List<Memo>> getAll() {
        return mRepository.getAll();
    }

    public LiveData<Memo> findLive(int id) {
        return mRepository.findLive(id);
    }

    public Memo find(int id) {
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
        mRepository.saveRecyclerLayoutState(spanCount);
    }

    public int getSavedRecyclerLayoutState() {
        return mRepository.getSavedRecyclerLayoutState();
    }
}
