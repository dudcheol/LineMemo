package com.example.linememo.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.linememo.model.Memo;
import com.example.linememo.repository.MemoRepository;
import com.example.linememo.util.ConvertUtil;

import java.util.List;

public class MemoViewModel extends AndroidViewModel {
    private static final String TAG = "MemoViewModel";
    private MemoRepository mRepository;
    private LiveData<Memo> findLiveMemo;
    private Memo findMemo;
    private LiveData<Integer> memoCnt;

    public MemoViewModel(Application application) {
        super(application);
        mRepository = new MemoRepository(application);
    }

    public LiveData<List<Memo>> getAll() {
        return mRepository.getAll();
    }

    public LiveData<Integer> getCount() {
        setMemoCnt(mRepository.getCount());
        return memoCnt;
    }

    public LiveData<Memo> findLive(int id) {
        setFindLiveMemo(mRepository.findLive(id));
        return findLiveMemo;
    }

    public Memo find(int id) {
        setFindMemo(mRepository.find(id));
        return findMemo;
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

    public String convertDate(long date) {
        if (date == 0) return "";
        return ConvertUtil.longDateToLongString(date);
    }

    public int hasAppear(List<String> list) {
        Log.e(TAG, list == null ? "list null" : list.toString());
        if (list == null || list.isEmpty()) return View.GONE;
        else return View.VISIBLE;
    }

    public int hasDisappear(List<String> list){
        Log.e(TAG, list == null ? "list null" : list.toString());
        if (list == null || list.isEmpty()) return View.VISIBLE;
        else return View.GONE;
    }

    // getter & setter
    public LiveData<Memo> getFindLiveMemo() {
        return findLiveMemo;
    }

    public LiveData<Integer> getMemoCnt() {
        return memoCnt;
    }

    public Memo getFindMemo() {
        return findMemo;
    }

    public void setFindLiveMemo(LiveData<Memo> findLiveMemo) {
        this.findLiveMemo = findLiveMemo;
    }

    public void setMemoCnt(LiveData<Integer> memoCnt) {
        this.memoCnt = memoCnt;
    }

    public void setFindMemo(Memo findMemo) {
        this.findMemo = findMemo;
    }
}
