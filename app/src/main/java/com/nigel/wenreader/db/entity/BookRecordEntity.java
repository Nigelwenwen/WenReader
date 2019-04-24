package com.nigel.wenreader.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * @author nigel
 * @description
 * @since 2018-09-16
 */
@Entity(tableName = "bookrecord")
public class BookRecordEntity {
    //所属的书的id
    @NonNull
    @PrimaryKey
    private String bookId;
    //阅读到了第几章
    private int chapter;
    //当前的页码
    private int pagePos;

    public BookRecordEntity(String bookId, int chapter, int pagePos) {
        this.bookId = bookId;
        this.chapter = chapter;
        this.pagePos = pagePos;
    }

    @Ignore
    public BookRecordEntity() {
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getPagePos() {
        return pagePos;
    }

    public void setPagePos(int pagePos) {
        this.pagePos = pagePos;
    }
}
