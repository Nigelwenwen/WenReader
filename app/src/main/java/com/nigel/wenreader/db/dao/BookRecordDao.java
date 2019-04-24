package com.nigel.wenreader.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.nigel.wenreader.db.entity.BookRecordEntity;

/**
 * @author nigel
 * @description
 * @since 2018-09-16
 */
@Dao
public interface BookRecordDao {
    @Query("SELECT * FROM bookrecord WHERE bookId=:id")
    BookRecordEntity loadBookRecordById(String id);

    //插入或者替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookRecord(BookRecordEntity bookRecord);
}
