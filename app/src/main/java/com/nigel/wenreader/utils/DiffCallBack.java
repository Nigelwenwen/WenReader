package com.nigel.wenreader.utils;

import android.support.v7.util.DiffUtil;

import java.util.List;

public class DiffCallBack<T> extends DiffUtil.Callback {

    protected List<T> mOldDatas, mNewDatas;

    public DiffCallBack(List<T> oldDatas, List<T> newDatas) {
        mOldDatas = oldDatas;
        mNewDatas = newDatas;
    }

    @Override
    public int getOldListSize() {
        return mOldDatas != null ? mOldDatas.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewDatas != null ? mNewDatas.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return mOldDatas.get(i).equals(mNewDatas.get(i1));
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        return false;
    }
}
