package com.example.linememo.view.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.linememo.db.dao.MemoDao;
import com.example.linememo.db.MemoDatabase;
import com.example.linememo.db.entity.Memo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MemoViewModel extends AndroidViewModel {
    private MemoDao memoDao;

    public MemoViewModel(@NonNull Application application) {
        super(application);
        memoDao = MemoDatabase.getInstance(application).memoDao();
    }

    public LiveData<List<Memo>> getAll() {
        return memoDao.getAll();
    }

    public LiveData<Memo> find(int id) {
        try {
            return new FindAsyncTask(memoDao).execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insert(Memo memo) {
        new InsertAsyncTask(memoDao).execute(memo);
    }

    public void delete(Memo memo) {
        new DeleteAsyncTask(memoDao).execute(memo);
    }

    public void update(Memo memo) {
        new UpdateAsyncTask(memoDao).execute(memo);
    }

    private static class InsertAsyncTask extends AsyncTask<Memo, Void, Void> {
        private MemoDao mMemoDao;

        public InsertAsyncTask(MemoDao memoDao) {
            this.mMemoDao = memoDao;
        }

        @Override
        protected Void doInBackground(Memo... memos) {
            mMemoDao.insert(memos[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Memo, Void, Void> {
        private MemoDao mMemoDao;

        public DeleteAsyncTask(MemoDao memoDao) {
            this.mMemoDao = memoDao;
        }

        @Override
        protected Void doInBackground(Memo... memos) {
            mMemoDao.delete(memos[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<Memo, Void, Void> {
        private MemoDao mMemoDao;

        public UpdateAsyncTask(MemoDao memoDao) {
            this.mMemoDao = memoDao;
        }

        @Override
        protected Void doInBackground(Memo... memos) {
            mMemoDao.update(memos[0]);
            return null;
        }
    }

    private static class FindAsyncTask extends AsyncTask<Integer, Void, LiveData<Memo>> {
        private MemoDao mMemoDao;

        public FindAsyncTask(MemoDao memoDao) {
            this.mMemoDao = memoDao;
        }

        @Override
        protected LiveData<Memo> doInBackground(Integer... integers) {
            return mMemoDao.find(integers[0]);
        }
    }
}
