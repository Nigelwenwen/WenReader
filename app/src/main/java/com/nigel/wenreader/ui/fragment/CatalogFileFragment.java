package com.nigel.wenreader.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nigel.wenreader.R;
import com.nigel.wenreader.adapter.FileSystemAdapter;
import com.nigel.wenreader.ui.activity.LocalFileActivity;
import com.nigel.wenreader.viewmodel.CatalogFileViewModel;

import java.io.File;

public class CatalogFileFragment extends BaseFileFragment implements FileSystemAdapter.DirChangeListener {

    private static final String TAG = "CatalogFileFragment";
    CatalogFileViewModel mViewModel;
    private RecyclerView mRecyclerView;
    LocalFileActivity mActivity;
    TextView mPath;
    TextView mBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=(LocalFileActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalog_file, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPath=view.findViewById(R.id.file_category_tv_path);
        mBack=view.findViewById(R.id.file_category_tv_back_last);
        mRecyclerView=view.findViewById(R.id.file_category_rv_content);
        mAdapter=new FileSystemAdapter();
        //监听选择个数
        mAdapter.getCheckedCount().observe(this,
                integer -> mActivity.checkedCountChange(integer));
        mAdapter.setDirChangeListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel=ViewModelProviders.of(mActivity).get(CatalogFileViewModel.class);
        subscribeToUI();
    }

    private void subscribeToUI() {
        mViewModel.getPathData().observe(this, s ->
                mPath.setText(getString(R.string.file_path,s)));
        mViewModel.getFileData().observe(this,list->{
            mAdapter.refreshItems(list);
            Log.d(TAG, "subscribeToUI: "+list.size());
        });
        mViewModel.getIsTreeChange().observe(this, isChange -> {
            if (isChange){
                mActivity.changeTree();
                mViewModel.setIsTreeChange(false);
            }
        });
        mViewModel.getScrollOffset().observe(this,offset->{
            mRecyclerView.scrollBy(0,offset);
        });
        mBack.setOnClickListener(v -> mViewModel.popFileTree(mRecyclerView.computeVerticalScrollOffset()));
    }

    @Override
    public void onDirSelected(File file) {
        int offset=mRecyclerView.computeVerticalScrollOffset();
        mViewModel.pushFileTree(offset,mAdapter.getList(),file);
    }
}
