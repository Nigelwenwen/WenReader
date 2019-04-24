package com.nigel.wenreader.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.nigel.wenreader.R;
import com.nigel.wenreader.db.entity.CollectBookEntity;
import com.nigel.wenreader.model.local.FileBean;
import com.nigel.wenreader.model.repository.BookRepository;
import com.nigel.wenreader.utils.Constant;
import com.nigel.wenreader.utils.FileLoader;
import com.nigel.wenreader.utils.FileUtils;
import com.nigel.wenreader.utils.MD5Utils;
import com.nigel.wenreader.utils.StringUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ScanFileViewModel extends BaseFileViewModel {
    private static final int ALL_BOOK_FILE = 1;
    private CompositeDisposable mDisposable;

    public ScanFileViewModel(@NonNull Application application) {
        super(application);
    }
    /**
     * 获取媒体库中所有的书籍文件
     * <p>
     * 暂时只支持 TXT
     *
     * @param activity
     */
    public void getAllBookFile(FragmentActivity activity) {
        // 将文件的获取处理交给 LoaderManager。
        if(mDisposable==null){
            mDisposable=new CompositeDisposable();
        }
        LoaderManager.getInstance(activity)
                .initLoader(ALL_BOOK_FILE, null, new MediaLoaderCallbacks(activity));
    }
    /**
     * Loader 回调处理
     */
    private class MediaLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {
        WeakReference<Context> mContext;

        MediaLoaderCallbacks(Context context) {
            mContext = new WeakReference<>(context);
        }

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new FileLoader(mContext.get());
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, final Cursor data) {
            Disposable disposable=Observable.create((ObservableOnSubscribe<List<FileBean>>) emitter
                    -> emitter.onNext(parseData(data)))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(list -> {
                        mFileBeans.setValue(list);
                    });
            mDisposable.add(disposable);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        }
    }

    private static List<FileBean> parseData(Cursor cursor){
        List<FileBean> files = new ArrayList<>();
        // 判断是否存在数据
        if (cursor == null) {
            // TODO:当媒体库没有数据的时候，需要做相应的处理
            // 暂时直接返回空数据
            return null;
        }
        // 重复使用Loader时，需要重置cursor的position；
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            String path;

            path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
            // 路径无效
            if (TextUtils.isEmpty(path)) {
                continue;
            } else {
                File file= new File(path);
                if (file.isDirectory() || !file.exists()){
                    continue;
                }
                else {
                    FileBean fileBean=new FileBean();
                    fileBean.setFileName(file.getName());
                    fileBean.setFilePath(path);
                    fileBean.setFileDate(StringUtils.dateConvert(file.lastModified(), Constant.FORMAT_FILE_DATE));
                    fileBean.setFileSize(FileUtils.getFileSize(file.length()));
                    fileBean.setAdded(BookRepository.getInstance().isAddedBook(MD5Utils.strToMd5By32(path)));
                    files.add(fileBean);
                }
            }
        }
        return files;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(mDisposable!=null){
            mDisposable.dispose();
        }
    }
}
