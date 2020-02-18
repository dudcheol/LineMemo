package com.example.linememo.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.linememo.db.dao.MemoDao;
import com.example.linememo.db.entity.Memo;

/**
 * Todo : apk 배포시 exportSchema = false 처리하여 스키마 버전 내역을 app에 포함시키지 않아야 한다
 */
@Database(entities = {Memo.class}, version = 1, exportSchema = false)
@TypeConverters({ImageUriConverter.class})
public abstract class MemoDatabase extends RoomDatabase {
    private static MemoDatabase INSTANCE;

    public abstract MemoDao memoDao();

    private static final Object sLock = new Object();

    public static MemoDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room
                        .databaseBuilder(context.getApplicationContext(), MemoDatabase.class, "memo-test-db-3")
                        .build();
            }
            return INSTANCE;
        }
    }
}
