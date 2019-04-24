package com.nigel.wenreader.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.nigel.wenreader.BR;
import com.nigel.wenreader.R;
import com.nigel.wenreader.adapter.FileSystemAdapter;
import com.nigel.wenreader.adapter.QuickFragmentPagerAdapter;
import com.nigel.wenreader.databinding.ActivityLocalFileBinding;
import com.nigel.wenreader.model.local.FileBean;
import com.nigel.wenreader.ui.fragment.BaseFileFragment;
import com.nigel.wenreader.ui.fragment.CatalogFileFragment;
import com.nigel.wenreader.ui.fragment.ScanFileFragment;
import com.nigel.wenreader.viewmodel.BaseFileViewModel;
import com.nigel.wenreader.viewmodel.CatalogFileViewModel;
import com.nigel.wenreader.viewmodel.ScanFileViewModel;

import java.util.ArrayList;
import java.util.List;

public class LocalFileActivity extends AppCompatActivity {
    private static final String TAG = "LocalFileActivity";
    ScanFileFragment scanFileFragment;
    CatalogFileFragment catalogFileFragment;
    ActivityLocalFileBinding mBinding;
    private List<Fragment> mFragments=new ArrayList<>();
    private String[] titles={"智能导入","手机目录"};
    private BaseFileViewModel mViewModel;
    private ScanFileViewModel mScanFileViewModel;
    private CatalogFileViewModel mCatalogFileViewModel;
    Presenter scanFilePresenter;
    Presenter catalogFilePresenter;
    Presenter mCurentPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding=DataBindingUtil.setContentView(this,R.layout.activity_local_file);
        mBinding.setVariable(BR.count,"加入书架");
        mViewModel=ViewModelProviders.of(this).get(ScanFileViewModel.class);
        initToolBar();
        initViewModel();
        initViewPagerAndTabs();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initViewModel() {
        mScanFileViewModel=ViewModelProviders.of(this).get(ScanFileViewModel.class);
        mCatalogFileViewModel=ViewModelProviders.of(this).get(CatalogFileViewModel.class);
        mViewModel=mScanFileViewModel;
    }

    private void initPresenter() {
        scanFilePresenter=new Presenter(scanFileFragment);
        catalogFilePresenter=new Presenter(catalogFileFragment);
        Log.d(TAG, "initPresenter: mAdapter used");
        mBinding.setVariable(BR.presenter,scanFilePresenter);
    }

    private void initViewPagerAndTabs(){
        scanFileFragment = new ScanFileFragment();
        catalogFileFragment = new CatalogFileFragment();
        mFragments.add(scanFileFragment);
        mFragments.add(catalogFileFragment);
        QuickFragmentPagerAdapter sectionsPagerAdapter = new QuickFragmentPagerAdapter<>(
                getSupportFragmentManager(), mFragments, titles);
        mBinding.viewPager.setAdapter(sectionsPagerAdapter);
        mBinding.tabs.setupWithViewPager(mBinding.viewPager);
        initPresenter();
        mBinding.viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position!=0){
                    scanFilePresenter.close();
                    mCurentPresenter=catalogFilePresenter;
                    mBinding.setVariable(BR.presenter,catalogFilePresenter);
                    mViewModel=mCatalogFileViewModel;
                }else {
                    catalogFilePresenter.close();
                    mCurentPresenter=scanFilePresenter;
                    mBinding.setVariable(BR.presenter,scanFilePresenter);
                    mViewModel=mScanFileViewModel;
                }
            }
        });
    }

    private void initToolBar(){
        setSupportActionBar(mBinding.toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        mBinding.toolbar.setNavigationOnClickListener(
                (v) -> finish()
        );
    }

    public void checkedCountChange(int count){
        Log.d(TAG, "checkedCountChange: "+count);
        if(count==0){
            mBinding.setVariable(BR.isSelected,false);
            mBinding.setVariable(BR.count,"加入书架");
        }else {
            mBinding.setVariable(BR.isSelected,true);
            mBinding.setVariable(BR.count,"加入书架"+"("+count+")");
        }
    }

    public void changeTree(){
        if(mCurentPresenter!=null){
            mCurentPresenter.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanFilePresenter.close();
        catalogFilePresenter.close();
    }

    public class Presenter{
        private LocalFileActivity mActivity;
        private FileSystemAdapter mAdapter;
        private BaseFileFragment mBaseFileFragment;

        public Presenter(BaseFileFragment fileFragment) {
            mActivity = LocalFileActivity.this;
            mAdapter = fileFragment.getAdapter();
            mBaseFileFragment=fileFragment;
        }

        public void selectAll(boolean isChecked){
            if(mAdapter==null){
                mAdapter=mBaseFileFragment.getAdapter();
            }
            mAdapter.selectAll(isChecked);
        }

        public void delete(){
            new AlertDialog.Builder(mActivity)
                    .setTitle("删除文件")
                    .setMessage("确定删除文件吗?")
                    .setPositiveButton(getResources().getString(R.string.common_sure),
                            (dialog, which) -> {
                                //删除选中的文件
                                deleteFiles();
                                //提示删除文件成功
                                Toast.makeText(mActivity, "删除文件成功",
                                        Toast.LENGTH_SHORT).show();
                            })
                    .setNegativeButton(getResources().getString(R.string.common_cancel), null)
                    .show();
        }

        public void addToShelf(){
            List<FileBean> fileBeans = getCheckedFiles();
            mViewModel.addToShelf(fileBeans);
            mActivity.finish();
        }

        private void deleteFiles(){
            List<FileBean> fileBeans = getCheckedFiles();
            if(mAdapter==null){
                mAdapter=mBaseFileFragment.getAdapter();
            }
            mAdapter.removeItems(fileBeans);
            mViewModel.deleteFiles(fileBeans);
        }

        //获取被选中的文件列表
        private List<FileBean> getCheckedFiles(){
            if(mAdapter==null){
                mAdapter=mBaseFileFragment.getAdapter();
            }
            return mAdapter != null ? mAdapter.getCheckedItems() : null;
        }

        public void close(){
            mBinding.setVariable(BR.isChecked,false);
            if(mAdapter==null){
                mAdapter=mBaseFileFragment.getAdapter();
            }
            mAdapter.selectAll(false);
        }
    }
}
