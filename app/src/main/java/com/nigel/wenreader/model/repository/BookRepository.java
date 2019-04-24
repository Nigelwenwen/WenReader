package com.nigel.wenreader.model.repository;
/**
 * @description
 * @author nigel
 * @since 2018-09-17
 */

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.LongDef;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nigel.wenreader.App;
import com.nigel.wenreader.db.database.BookDatabase;
import com.nigel.wenreader.db.entity.BookChapterEntity;
import com.nigel.wenreader.db.entity.BookRecordEntity;
import com.nigel.wenreader.db.entity.CollectBookEntity;
import com.nigel.wenreader.utils.RxUtils;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;

import static android.support.constraint.Constraints.TAG;

/**
 * 单例模式
 * 获取书籍相关的数据库
 */
public class BookRepository {
    private static volatile BookRepository sInstance;
    private static BookDatabase mDatabase;


    public static BookRepository getInstance() {
        mDatabase= App.getDatabase();
        if (sInstance == null) {
            synchronized (BookRepository.class) {
                if (sInstance == null) {
                    sInstance = new BookRepository(mDatabase);
                }
            }
        }
        return sInstance;
    }

    private BookRepository(BookDatabase database) {
        mDatabase=database;
    }

    /***********************************************query******************************************/
    public LiveData<List<CollectBookEntity>> getCollectBooksWithLiveData(){
        LiveData<List<CollectBookEntity>> data=mDatabase.collectBookDao().loadAllCollectBooksWithLiveData();
        return data;
    }

    public List<CollectBookEntity> getCollectBooks(){
        return mDatabase.collectBookDao().loadAllCollectBooks();
    }

    public BookRecordEntity getBookRecord(String bookId){
        return mDatabase.bookRecordDao().loadBookRecordById(bookId);
    }

    public boolean isAddedBook(String id){
        return mDatabase.collectBookDao().getCollectBookById(id)!=null;
    }

    public Single<List<BookChapterEntity>> getBookChaptersInRx(String bookId){
        return Single.create(emitter -> emitter.onSuccess(mDatabase.bookChapterDao().loadBookChapterList(bookId)));
    }

    /***********************************************save******************************************/

    public void saveBookRecord(BookRecordEntity bookRecord){
        mDatabase.bookRecordDao().insertBookRecord(bookRecord);
    }

    public void saveCollectBooks(List<CollectBookEntity> entities){
        mDatabase.collectBookDao().insertCollectBooks(entities);
    }

    public void saveCollectBook(CollectBookEntity collectBook){
        new Thread(()-> mDatabase.collectBookDao().insertCollectBook(collectBook)).start();
    }

    //官网上说Dao类就能直接返回Maybe，但是我写了会报错说@Insert只能返回void 或者long相关类型，只能自己封装了
    public Maybe<List<Long>> saveBookChapterList(List<BookChapterEntity> bookChapterBeanList) {
        return Maybe.create(emitter -> emitter.onSuccess(
                mDatabase.bookChapterDao().insertBookChapters(bookChapterBeanList)));

    }

    /**********************************************delete******************************************/
    public Single<Integer> deleteCollectBooks(List<CollectBookEntity> entities){
        return Single.create(emitter-> {
            for(CollectBookEntity entity:entities){
                mDatabase.bookChapterDao().deleteChapterList(entity.get_id());
            }
            emitter.onSuccess(mDatabase.collectBookDao().deleteCollectBooks(entities));
        });
    }

}
