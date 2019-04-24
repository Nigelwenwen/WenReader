package com.nigel.wenreader.db.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;

import com.nigel.wenreader.App;
import com.nigel.wenreader.db.dao.BookChapterDao;
import com.nigel.wenreader.db.dao.BookRecordDao;
import com.nigel.wenreader.db.dao.CollectBookDao;
import com.nigel.wenreader.db.entity.BookChapterEntity;
import com.nigel.wenreader.db.entity.BookRecordEntity;
import com.nigel.wenreader.db.entity.CollectBookEntity;

/**
 * @author nigel
 * @description
 * @since 2018-09-16
 */
@Database(entities = {BookRecordEntity.class,CollectBookEntity.class,BookChapterEntity.class},version = 9)
public abstract class BookDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "BookReader-db";

    private volatile static BookDatabase sInstance;

    public abstract BookRecordDao bookRecordDao();
    public abstract CollectBookDao collectBookDao();
    public abstract BookChapterDao bookChapterDao();

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE collectbooks "
                    + " ADD COLUMN position INTEGER");
        }
    };


    public static BookDatabase getInstance() {
        if (sInstance == null) {
            synchronized (BookDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(App.getContext(),BookDatabase.class,DATABASE_NAME)
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return sInstance;
    }
}
