package com.nigel.wenreader.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nigel.wenreader.R;
import com.nigel.wenreader.adapter.FileSystemAdapter;
import com.nigel.wenreader.ui.activity.LocalFileActivity;
import com.nigel.wenreader.viewmodel.ScanFileViewModel;

public class ScanFileFragment extends BaseFileFragment {
    private static final String TAG = "ScanFileFragment";
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LocalFileActivity mActivity;
    private ScanFileViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mActivity=(LocalFileActivity) getActivity();
        return inflater.inflate(R.layout.fragment_scan_file, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView=view.findViewById(R.id.scan_recycler_view);
        mAdapter=new FileSystemAdapter();
        Log.d(TAG, "onViewCreated: mAdapter created");
        //监听选择个数
        mAdapter.getCheckedCount().observe(this,
                integer -> mActivity.checkedCountChange(integer));
        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout=view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.light_red);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel=ViewModelProviders.of(mActivity).get(ScanFileViewModel.class);
        mViewModel.getAllBookFile(mActivity);
        subscribeToUI();
    }

    private void subscribeToUI() {
        mViewModel.getFileBeans().observe(this, files -> {
            if (files.isEmpty()||files==null){
                Toast.makeText(getContext(), "暂无数据", Toast.LENGTH_LONG).show();
            }
            else {
                mAdapter.refreshItems(files);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "scanFile: "+files.size());
            mSwipeRefreshLayout.setRefreshing(false);
        });
    }
}
