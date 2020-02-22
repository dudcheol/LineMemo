package com.example.linememo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.linememo.dao.MemoDao;
import com.example.linememo.dao.MemoDatabase;
import com.example.linememo.model.Memo;
import com.example.linememo.repository.MemoRepository;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class MemoDBTest {
    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    private static final String TAG = "MemoDBTest";
    private MemoDao memoDao;
    private MemoDatabase db;

    @Before
    public void createDb() {
        Log.e(TAG, "테스트 시작");
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, MemoDatabase.class).build();
        memoDao = db.memoDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testDBfine() throws Exception {
        Memo m1, m2, m3;
        final long m1Id;
        final long m2Id;
        List<String> uriList = new ArrayList<>();

        // 메모 저장 insert
        {
            Log.e(TAG, "메모 저장");
            m1 = new Memo("테스트1 타이틀", "테스트1 내용", uriList, System.currentTimeMillis());
            m1Id = memoDao.insert(m1);
            m2 = new Memo("테스트2 타이틀", "테스트2 내용", uriList, System.currentTimeMillis());
            m2Id = memoDao.insert(m2);
            final CountDownLatch lock = new CountDownLatch(1);
            final LiveData<List<Memo>> allMemos = memoDao.getAll();
            Observer<List<Memo>> observer = new Observer<List<Memo>>() {
                @Override
                public void onChanged(List<Memo> memos) {
                    Assert.assertNotNull(memos);
                    Assert.assertTrue(memos.size() == 2);
                    allMemos.removeObserver(this);
                    Log.e(TAG, "현재 메모 리스트 = " + memos.toString());
                    lock.countDown();
                }
            };
            allMemos.observeForever(observer);
            lock.await(1, TimeUnit.DAYS);
            Log.e(TAG, "메모 저장 완료!");
        }

        // 메모 갯수 세기 getCount
        {
            Log.e(TAG, "메모 갯수 세기");
            final CountDownLatch lock = new CountDownLatch(1);
            final LiveData<Integer> count = memoDao.getCount();
            Observer<Integer> observer = new Observer<Integer>() {
                @Override
                public void onChanged(Integer cnt) {
                    Assert.assertNotNull(cnt);
                    Assert.assertEquals(2, cnt.intValue());
                    count.removeObserver(this);
                    Log.e(TAG, "현재 메모의 수 = " + cnt);
                    lock.countDown();
                }
            };
            count.observeForever(observer);
            lock.await(1, TimeUnit.DAYS);
            Log.e(TAG, "메모 갯수 세기 완료");
        }

        // 저장된 메모 찾기 find
        {
            Log.e(TAG, "저장된 메모 찾기");
            m3 = memoDao.find((int) m1Id);

            Log.e(TAG, "찾은 메모 = " + m3.toString());
            Assert.assertNotNull(m3);
            Assert.assertEquals(m1.getTitle(), m3.getTitle());
            Assert.assertEquals(m1.getContent(), m3.getContent());
            Assert.assertEquals(m1.getDate(), m3.getDate());
            Assert.assertEquals(m1.getImageUris(), m3.getImageUris());
            Log.e(TAG, "저장된 메모 찾기 완료");
        }

        // 메모 제거 delete
        {
            Log.e(TAG, "메모 제거");
            Log.e(TAG, "제거할 메모 = " + m3.toString());
            memoDao.delete(m3);
            final CountDownLatch lock = new CountDownLatch(1);
            final LiveData<List<Memo>> allMemos = memoDao.getAll();
            Observer<List<Memo>> observer = new Observer<List<Memo>>() {
                @Override
                public void onChanged(List<Memo> memos) {
                    Assert.assertNotNull(memos);
                    Assert.assertTrue(memos.size() == 1);
                    allMemos.removeObserver(this);
                    Log.e(TAG, "현재 메모 리스트 = " + memos.toString());
                    lock.countDown();
                }
            };
            allMemos.observeForever(observer);
            lock.await(1, TimeUnit.DAYS);
            Log.e(TAG, "메모 제거 완료");
        }

        // 메모 수정 update
        {
            Log.e(TAG, "메모 수정");
            final String updated = "modify";
            m2.setId((int) m2Id);
            m2.setTitle(updated);
            memoDao.update(m2);
            final CountDownLatch lock = new CountDownLatch(1);
            final LiveData<List<Memo>> allMemos = memoDao.getAll();
            Observer<List<Memo>> observer = new Observer<List<Memo>>() {
                @Override
                public void onChanged(List<Memo> memos) {
                    Assert.assertNotNull(memos);
                    Assert.assertEquals(updated, memos.get(0).getTitle());
                    allMemos.removeObserver(this);
                    Log.e(TAG, "현재 메모 리스트 = " + memos.toString());
                    lock.countDown();
                }
            };
            allMemos.observeForever(observer);
            lock.await(1, TimeUnit.DAYS);
            Log.e(TAG, "메모 수정 완료");
        }

        // LiveMemo list 확인 findLive
        {
            Log.e(TAG, "라이브 메모 찾기");
            final CountDownLatch lock = new CountDownLatch(1);
            final LiveData<Memo> liveMemo = memoDao.findLive((int) m2Id);
            Observer<Memo> observer = new Observer<Memo>() {
                @Override
                public void onChanged(Memo memo) {
                    Assert.assertNotNull(memo);
                    Assert.assertEquals("modify", memo.getTitle());
                    liveMemo.removeObserver(this);
                    Log.e(TAG, "찾은 live 메모 = " + memo.toString());
                    lock.countDown();
                }
            };
            liveMemo.observeForever(observer);
            lock.await(1, TimeUnit.DAYS);
            Log.e(TAG, "라이브 메모 찾기 완료");
        }
    }
}
