package com.nigel.wenreader.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nigel.wenreader.model.local.FileBean;
import com.nigel.wenreader.model.repository.BookRepository;
import com.nigel.wenreader.utils.Constant;
import com.nigel.wenreader.utils.FileStack;
import com.nigel.wenreader.utils.FileUtils;
import com.nigel.wenreader.utils.MD5Utils;
import com.nigel.wenreader.utils.StringUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class CatalogFileViewModel extends BaseFileViewModel {
    private static final String TAG = "CatalogFileViewModel";
    private MutableLiveData<String> pathData=new MutableLiveData<>();
    private MutableLiveData<List<FileBean>> fileData=new MutableLiveData<>();
    private MutableLiveData<Boolean> isTreeChange=new MutableLiveData<>();
    private MutableLiveData<Integer> scrollOffset=new MutableLiveData<>();
    private FileStack mFileStack;
    public CatalogFileViewModel(@NonNull Application application) {
        super(application);
        mFileStack = new FileStack();
        initFileTree();
    }

    private void initFileTree() {
        File root = Environment.getExternalStorageDirectory();
        toggleFileTree(root);
    }

    public void toggleFileTree(File file){
        //路径名
        pathData.setValue(file.getPath());
        //获取数据
        File[] files = file.listFiles(new SimpleFileFilter());
        //转换成List
        List<File> rootFiles = Arrays.asList(files);
        //排序
        rootFiles.sort(new FileComparator());
        List<FileBean> fileBeans=new ArrayList<>();

        for (File file1:rootFiles){
            FileBean fileBean=new FileBean();
            fileBean.setFileName(file1.getName());
            fileBean.setFilePath(file1.getAbsolutePath());
            fileBean.setFileDate(StringUtils.dateConvert(file1.lastModified(), Constant.FORMAT_FILE_DATE));
            fileBean.setFileSize(FileUtils.getFileSize(file1.length()));
            fileBean.setAdded(BookRepository.getInstance().isAddedBook(MD5Utils.strToMd5By32(file1.getAbsolutePath())));
            fileBean.setFolder(file1.isDirectory());
            if(file1.isDirectory()){
                fileBean.setSubCount(file1.list().length);
            }
            fileBeans.add(fileBean);
//            Log.d(TAG, "toggleFileTree: isFolder:"+fileBean.isFolder()+" subCount:"+fileBean.getSubCount());
        }
        fileData.setValue(fileBeans);
        isTreeChange.setValue(true);
    }

    public void popFileTree(int offset){
        FileStack.FileSnapshot snapshot = mFileStack.pop();
        if (snapshot == null) return;
        pathData.setValue(snapshot.filePath);
        fileData.setValue(snapshot.files);
        scrollOffset.setValue(snapshot.scrollOffset - offset);
        isTreeChange.setValue(true);
    }

    public void pushFileTree(int offset,List<FileBean> files,File file){
        //保存当前信息。
        FileStack.FileSnapshot snapshot = new FileStack.FileSnapshot();
        snapshot.filePath = pathData.getValue();
        snapshot.files = new ArrayList<>(files);
        snapshot.scrollOffset = offset;
        mFileStack.push(snapshot);
        //切换下一个文件
        toggleFileTree(file);
    }

    public MutableLiveData<Integer> getScrollOffset() {
        return scrollOffset;
    }

    public MutableLiveData<Boolean> getIsTreeChange() {
        return isTreeChange;
    }

    public void setIsTreeChange(boolean isTreeChange) {
        this.isTreeChange.setValue(isTreeChange);
    }

    public MutableLiveData<String> getPathData() {
        return pathData;
    }

    public MutableLiveData<List<FileBean>> getFileData() {
        return fileData;
    }

    public class FileComparator implements Comparator<File> {
        @Override
        public int compare(File o1, File o2){
            if (o1.isDirectory() && o2.isFile()) {
                return -1;
            }
            if (o2.isDirectory() && o1.isFile()) {
                return 1;
            }
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    public class SimpleFileFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            if (pathname.getName().startsWith(".")){
                return false;
            }
            //文件夹内部数量为0
            if (pathname.isDirectory() && pathname.list().length == 0){
                return false;
            }

            //文件内容为空,或者不以txt为结尾
            if (!pathname.isDirectory() &&
                    (pathname.length() == 0 || !pathname.getName().endsWith(FileUtils.SUFFIX_TXT))){
                return false;
            }
            return true;
        }
    }
}
