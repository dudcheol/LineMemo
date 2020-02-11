package com.example.linememo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MemoViewModel extends AndroidViewModel {
    private MemoDao memoDao;

    public MemoViewModel(@NonNull Application application) {
        super(application);
        memoDao = MemoDatabase.getInstance(application).memoDao();
    }

    LiveData<List<Memo>> getAll(){
        return memoDao.getAll();
    }

    void insert(Memo memo){
        new InsertAsyncTask(memoDao).execute(memo);
    }

    void delete(Memo memo){
        new DeleteAsyncTask(memoDao).execute(memo);
    }

    private static class InsertAsyncTask extends AsyncTask<Memo, Void, Void> {
        private MemoDao mMemoDao;

        public InsertAsyncTask(MemoDao memoDao){
            this.mMemoDao = memoDao;
        }

        @Override
        protected Void doInBackground(Memo... memos) {
            mMemoDao.insert(memos[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Memo,Void,Void>{
        private MemoDao mMemoDao;

        public DeleteAsyncTask(MemoDao memoDao){
            this.mMemoDao = memoDao;
        }

        @Override
        protected Void doInBackground(Memo... memos) {
            mMemoDao.delete(memos[0]);
            return null;
        }
    }
}
