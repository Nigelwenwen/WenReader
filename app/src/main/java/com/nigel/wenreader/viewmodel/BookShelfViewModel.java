package com.nigel.wenreader.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nigel.wenreader.db.entity.CollectBookEntity;
import com.nigel.wenreader.model.repository.BookRepository;
import com.nigel.wenreader.utils.RxUtils;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.BiConsumer;

public class BookShelfViewModel extends AndroidViewModel {
    private static final String TAG = "BookShelfViewModel";
    private BookRepository mRepository;
    private LiveData<List<CollectBookEntity>> shelfBooks;
    private boolean isFirst=true;
    private CompositeDisposable mDisposable;


    public BookShelfViewModel(@NonNull Application application) {
        super(application);
        mRepository=BookRepository.getInstance();
        mDisposable=new CompositeDisposable();
        refreshShelfBooks();
    }

    public void refreshShelfBooks(){
        shelfBooks=mRepository.getCollectBooksWithLiveData();
    }

    /**
     * 可以将这个数目用Toast显示给用户，但我没做
     * @param entities
     */
    public void deleteShelfBooks(List<CollectBookEntity> entities){
        Disposable disposable=mRepository.deleteCollectBooks(entities)
                .compose(RxUtils::toSimpleSingle)
                .subscribe((integer, throwable) -> Log.d(TAG, "deleteShelfBooks: deleted "+integer+" books"));
        mDisposable.add(disposable);
    }

    public LiveData<List<CollectBookEntity>> getShelfBooks() {
        if(shelfBooks==null){
            refreshShelfBooks();
        }
        return shelfBooks;
    }

    public void setShelfBooks(LiveData<List<CollectBookEntity>> shelfBooks) {
        this.shelfBooks = shelfBooks;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(mDisposable!=null){
            mDisposable.dispose();
        }
    }
}
