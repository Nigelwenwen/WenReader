package com.nigel.wenreader.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.nigel.wenreader.db.entity.CollectBookEntity;

import java.util.List;

import io.reactivex.Single;

/**
 * @author 123
 * @description
 * @since 2018-09-21
 */
@Dao
public interface CollectBookDao {
    //插入或者替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCollectBook(CollectBookEntity collectBookEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCollectBooks(List<CollectBookEntity> entities);

    @Query("SELECT * FROM collectbooks ORDER BY lastRead DESC")
    LiveData<List<CollectBookEntity>> loadAllCollectBooksWithLiveData();

    @Query("SELECT * FROM collectbooks ORDER BY lastRead DESC")
    List<CollectBookEntity> loadAllCollectBooks();

    @Query("SELECT * FROM collectbooks WHERE _id=:id")
    CollectBookEntity getCollectBookById(String id);

    @Update
    void changePosition(List<CollectBookEntity> books);

    @Delete
    int deleteCollectBooks(List<CollectBookEntity> bookEntities);
}



