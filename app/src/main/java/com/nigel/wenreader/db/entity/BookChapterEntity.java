package com.nigel.wenreader.db.entity;



import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by nigel on 2018-9-13.
 * 书的章节链接(作为下载的进度数据)
 * 同时作为网络章节和本地章节 (没有找到更好分离两者的办法)
 */
@Entity(tableName = "bookchapters",indices = {@Index(value = "bookId")})
public class BookChapterEntity implements Serializable {
    private static final long serialVersionUID = 56423411313L;
    /**
     * title : 第一章 他叫白小纯
     * link : http://read.qidian.com/chapter/rJgN8tJ_cVdRGoWu-UQg7Q2/6jr-buLIUJSaGfXRMrUjdw2
     * unreadble : false
     */
    @PrimaryKey
    @NonNull
    private String id;

    private String link;

    private String title;

    //所属的下载任务
    private String taskName;

    private boolean unreadble;

    //所属的书籍
    private String bookId;

    //本地书籍参数


    //在书籍文件中的起始位置
    private long start;

    //在书籍文件中的终止位置
    private long end;

    public BookChapterEntity(String id, String link, String title, String taskName,
                             boolean unreadble, String bookId, long start, long end) {
        this.id = id;
        this.link = link;
        this.title = title;
        this.taskName = taskName;
        this.unreadble = unreadble;
        this.bookId = bookId;
        this.start = start;
        this.end = end;
    }

    @Ignore
    public BookChapterEntity() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isUnreadble() {
        return unreadble;
    }

    public void setUnreadble(boolean unreadble) {
        this.unreadble = unreadble;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "BookChapterBean{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", title='" + title + '\'' +
                ", taskName='" + taskName + '\'' +
                ", unreadble=" + unreadble +
                ", bookId='" + bookId + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}