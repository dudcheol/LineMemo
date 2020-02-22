package com.example.linememo.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.linememo.model.Memo;

import java.util.List;

@Dao
public interface MemoDao {
    @Query("SELECT * FROM Memo")
    LiveData<List<Memo>> getAll();

    @Query("SELECT COUNT(*) FROM Memo")
    LiveData<Integer> getCount();

    @Query("SELECT * FROM Memo WHERE id = :id")
    LiveData<Memo> findLive(int id);

    @Query("SELECT * FROM Memo WHERE id = :id")
    Memo find(int id);

    @Insert
    long insert(Memo memo);

    @Update
    void update(Memo memo);

    @Delete
    void delete(Memo memo);
}
