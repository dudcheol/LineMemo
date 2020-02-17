package com.example.linememo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Memo.class}, version = 1)
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
