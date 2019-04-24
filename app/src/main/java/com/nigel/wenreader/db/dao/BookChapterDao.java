package com.nigel.wenreader.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.nigel.wenreader.db.entity.BookChapterEntity;

import java.util.List;

/**
 * @author 123
 * @description
 * @since 2018-09-22
 */
@Dao
public interface BookChapterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBookChapter(BookChapterEntity bookChapterEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertBookChapters(List<BookChapterEntity> entities);

    @Query("SELECT * FROM bookchapters WHERE bookId=:bookId")
    List<BookChapterEntity> loadBookChapterList(String bookId);

    @Query("DELETE FROM bookchapters WHERE bookId=:bookId")
    void deleteChapterList(String bookId);
}
