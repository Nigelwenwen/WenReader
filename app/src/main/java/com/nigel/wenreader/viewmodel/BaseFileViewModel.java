package com.nigel.wenreader.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.nigel.wenreader.db.entity.CollectBookEntity;
import com.nigel.wenreader.model.local.FileBean;
import com.nigel.wenreader.model.repository.BookRepository;
import com.nigel.wenreader.utils.MD5Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BaseFileViewModel extends AndroidViewModel {
    protected MutableLiveData<List<FileBean>> mFileBeans=new MutableLiveData<>();
    public BaseFileViewModel(@NonNull Application application) {
        super(application);
    }

    public void deleteFiles(List<FileBean> fileBeans){
        List<File> files=new ArrayList<>();
        //删除选中的文件
        for (FileBean fileBean:fileBeans){
            files.add(new File(fileBean.getFilePath()));
        }
        for (File file : files){
            if (file.exists()){
                file.delete();
            }
        }
    }

    public void addToShelf(List<FileBean> fileBeans){
        //转换成CollBook,并存储
        List<CollectBookEntity> collBooks = convertCollBook(fileBeans);
        BookRepository.getInstance().saveCollectBooks(collBooks);
    }

    protected List<CollectBookEntity> convertCollBook(List<FileBean> fileBeans) {
        List<CollectBookEntity> books=new ArrayList<>();
        for (FileBean fileBean:fileBeans){
            CollectBookEntity book=new CollectBookEntity();
            book.set_id(MD5Utils.strToMd5By32(fileBean.getFilePath()));
            book.setTitle(fileBean.getFileName());
            book.setUpdated(fileBean.getFileDate());
            book.setLocal(true);
            book.setCover(fileBean.getFilePath());
            book.setPosition(1);
            books.add(book);
        }
        return books;
    }

    public MutableLiveData<List<FileBean>> getFileBeans() {
        return mFileBeans;
    }
}
