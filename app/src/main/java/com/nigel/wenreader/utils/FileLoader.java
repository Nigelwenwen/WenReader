package com.nigel.wenreader.utils;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;

/**
 * 派生一个本地文件的CursorLoader,现在Google好像建议用LiveData做了
 */
public class FileLoader extends CursorLoader {
    private static final Uri FILE_URI = Uri.parse("content://media/external/file");
    private static final String SELECTION = MediaStore.Files.FileColumns.MIME_TYPE + " = ?";
    private static final String SEARCH_TYPE = "text/plain";
    private static final String SORT_ORDER = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC";
    private static final String[] FILE_PROJECTION = {
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME
    };
    public FileLoader(@NonNull Context context) {
        super(context);
        initLoader();
    }

    /**
     * 设置查找的参数
     */
    private void initLoader() {
        setUri(FILE_URI);
        setSelection(SELECTION);
        setSelectionArgs(new String[]{SEARCH_TYPE});
        setProjection(FILE_PROJECTION);
        setSortOrder(SORT_ORDER);
    }
}
