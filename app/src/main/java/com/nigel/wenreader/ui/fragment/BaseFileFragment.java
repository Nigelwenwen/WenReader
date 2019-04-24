package com.nigel.wenreader.ui.fragment;

import android.support.v4.app.Fragment;

import com.nigel.wenreader.adapter.FileSystemAdapter;

public class BaseFileFragment extends Fragment {
    protected FileSystemAdapter mAdapter;

    public FileSystemAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(FileSystemAdapter adapter) {
        mAdapter = adapter;
    }
}
