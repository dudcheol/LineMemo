package com.example.linememo.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.linememo.dao.MemoDao;
import com.example.linememo.dao.MemoDatabase;
import com.example.linememo.model.Memo;
import com.example.linememo.util.SharedPreferenceManager;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MemoRepository {
    private MemoDao mMemoDao;
    private Application application;

    public MemoRepository(Application application) {
        mMemoDao = MemoDatabase.getInstance(application).memoDao();
        this.application = application;
    }

    public LiveData<List<Memo>> getAll() {
        return mMemoDao.getAll();
    }

    public LiveData<Memo> findLive(int id) {
        try {
            return new FindLiveAsyncTask(mMemoDao).execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Memo find(int id) {
        try {
            return new FindAsyncTask(mMemoDao).execute(id).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void insert(Memo memo) {
        new InsertAsyncTask(mMemoDao).execute(memo);
    }

    public void delete(Memo memo) {
        new DeleteAsyncTask(mMemoDao).execute(memo);
    }

    public void update(Memo memo) {
        new UpdateAsyncTask(mMemoDao).execute(memo);
    }

    public void saveRecyclerLayoutState(int spanCount) {
        SharedPreferenceManager.setInt(application.getApplicationContext(), "viewMode", spanCount);
    }

    public int getSavedRecyclerLayoutState() {
        int savedSpan = SharedPreferenceManager.getInt(application.getApplicationContext(), "viewMode");
        return savedSpan == -1 ? 2 : savedSpan;
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

    private static class FindLiveAsyncTask extends AsyncTask<Integer, Void, LiveData<Memo>> {
        private MemoDao mMemoDao;

        public FindLiveAsyncTask(MemoDao memoDao) {
            this.mMemoDao = memoDao;
        }

        @Override
        protected LiveData<Memo> doInBackground(Integer... integers) {
            return mMemoDao.findLive(integers[0]);
        }
    }

    private static class FindAsyncTask extends AsyncTask<Integer, Void, Memo> {
        private MemoDao mMemoDao;

        public FindAsyncTask(MemoDao memoDao) {
            this.mMemoDao = memoDao;
        }

        @Override
        protected Memo doInBackground(Integer... integers) {
            return mMemoDao.find(integers[0]);
        }
    }
}
