package com.nigel.wenreader.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nigel.wenreader.BR;
import com.nigel.wenreader.R;
import com.nigel.wenreader.adapter.view.BindingViewHolder;
import com.nigel.wenreader.databinding.ItemFileBinding;
import com.nigel.wenreader.model.local.FileBean;
import com.nigel.wenreader.utils.DiffCallBack;
import com.nigel.wenreader.utils.FileStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileSystemAdapter extends BaseAdapter<FileBean> {
    private static final String TAG = "FileSystemAdapter";
    private DirChangeListener mDirChangeListener;


    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        ViewDataBinding binding=DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),R.layout.item_file,parent,false);
        return new BindingViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, int i) {
        ItemFileBinding binding=(ItemFileBinding) holder.getBinding();
        FileBean fileBean=mList.get(i);
        binding.setVariable(BR.file,fileBean);
        binding.setVariable(BR.adapter,this);
        binding.setVariable(BR.isSelectAll,isSelectAll);
        if (fileBean.isFolder()) {
            binding.getRoot().setOnClickListener((view) -> {
                if (mDirChangeListener != null) {
                    mDirChangeListener.onDirSelected(new File(fileBean.getFilePath()));
                }
            });
        }else {
            binding.fileCbSelect.setOnClickListener((v -> checkChanged(binding.fileCbSelect,fileBean)));
        }
        binding.executePendingBindings();
    }

    @Override
    public void selectAll(boolean isChecked) {
        int count=0;
        for (Map.Entry<FileBean, Boolean> entry:mCheckMap.entrySet()){
            //必须是文件，必须没有被收藏
            if (!entry.getKey().isFolder() && !entry.getKey().isAdded()){
                entry.setValue(isChecked);
                if(isChecked){
                    count++;
                }
            }
        }
        mCheckedCount.setValue(count);
        isSelectAll=isChecked;
        notifyDataSetChanged();
    }

    @Override
    public void removeItems(List<FileBean> items) {
        super.removeItems(items);
        DiffUtil.DiffResult diffResult=DiffUtil.calculateDiff(
                new FileBeanDiff(mOldList,mList),false);
        diffResult.dispatchUpdatesTo(this);
    }

    public void setDirChangeListener(DirChangeListener dirChangeListener) {
        mDirChangeListener = dirChangeListener;
    }

    public interface DirChangeListener{
        void onDirSelected(File file);
    }

    class FileBeanDiff extends DiffCallBack<FileBean>{

        FileBeanDiff(List<FileBean> oldDatas, List<FileBean> newDatas) {
            super(oldDatas, newDatas);
        }

        @Override
        public boolean areContentsTheSame(int i, int i1) {
            FileBean oldFileBean=mOldDatas.get(i);
            FileBean newFileBean=mNewDatas.get(i1);
            return oldFileBean.getFileDate().equals(newFileBean.getFileDate());
        }
    }
}
