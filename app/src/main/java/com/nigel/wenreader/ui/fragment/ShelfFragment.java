package com.nigel.wenreader.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nigel.wenreader.BR;
import com.nigel.wenreader.R;
import com.nigel.wenreader.adapter.HeaderAndFooterWrapper;
import com.nigel.wenreader.adapter.ShelfAdapter;
import com.nigel.wenreader.databinding.FragmentShelfBinding;
import com.nigel.wenreader.db.entity.CollectBookEntity;
import com.nigel.wenreader.ui.activity.ReadActivity;
import com.nigel.wenreader.viewmodel.BookShelfViewModel;
import com.nigel.wenreader.widget.itemDecoration.DividerItemDecoration;
import com.nigel.wenreader.widget.recyclerView.RecyclerViewWithContextMenu;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.Navigation;


public class ShelfFragment extends Fragment {
    private static final String TAG = "ShelfFragment";
    FragmentShelfBinding mBinding;
    BookShelfViewModel mViewModel;
    HeaderAndFooterWrapper<CollectBookEntity> mAdapter;
    Context mContext;
    FragmentActivity mActivity;
    ActionMode mActionMode;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_action_bar_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    removeBooks();
                    mode.finish();
                    return true;
                case R.id.done_all:
                    if(!item.isChecked()){
                        item.setChecked(true);
                        item.setIconTintList(getResources().getColorStateList(R.color.light_red,null));
                        mAdapter.selectAll(true);
                    }else{
                        item.setChecked(false);
                        item.setIconTintList(getResources().getColorStateList(R.color.black,null));
                        mAdapter.selectAll(false);
                    }
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            mAdapter.setBatchMode(false);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getContext();
        mActivity=getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding=DataBindingUtil.inflate(inflater,R.layout.fragment_shelf,container,false);
        mBinding.setVariable(BR.presenter,new Presenter());
        mBinding.setLifecycleOwner(this);
        setRecyclerView();
        registerForContextMenu(mBinding.shelfRecyclerView);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel=ViewModelProviders.of(mActivity).get(BookShelfViewModel.class);
        if(mViewModel.isFirst()){
            mBinding.swipeRefresh.setRefreshing(true);
            mViewModel.setFirst(false);
        }
        subscribeToModel();
    }

    private void subscribeToModel() {
        mViewModel.getShelfBooks().observe(this, collectBookEntities ->{
            mAdapter.refreshItems(collectBookEntities);
            if(mBinding.swipeRefresh.isRefreshing()){
                mBinding.swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void setRecyclerView(){
        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(
                mContext,LinearLayout.VERTICAL);
        mBinding.shelfRecyclerView.addItemDecoration(dividerItemDecoration);
        mAdapter=new HeaderAndFooterWrapper<>(new ShelfAdapter());
        mAdapter.addFootView(new FootView());
        mBinding.shelfRecyclerView.setHasFixedSize(true);
        mBinding.shelfRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater=getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        RecyclerViewWithContextMenu.RecyclerViewContextInfo info =
                (RecyclerViewWithContextMenu.RecyclerViewContextInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.top:
                Toast.makeText(mContext, "置顶", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.cache:
                Toast.makeText(mContext, "缓存", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete:
                removeBook(info.getPosition());
                return true;
            case R.id.batch_manage:
                if (mActionMode != null) {
                    return false;
                }
                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = getActivity().startActionMode(mActionModeCallback);
                if(!mAdapter.isBatchMode()){
                    mAdapter.setBatchMode(true);
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void removeBooks(){
        List<CollectBookEntity> checkedBooks=mAdapter.getCheckedItems();
        Log.d(TAG, "removeBooks: "+checkedBooks.size());
        if(checkedBooks!=null||checkedBooks.size()==0){
            mAdapter.removeItems(checkedBooks);
            mViewModel.deleteShelfBooks(checkedBooks);
        }
    }

    private void removeBook(int position){
        CollectBookEntity entity=mAdapter.removeItem(position);
        List<CollectBookEntity> entities=new ArrayList<>();
        entities.add(entity);
        mViewModel.deleteShelfBooks(entities);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mActionMode!=null){
            mActionMode.finish();
            mActionMode=null;
        }
    }

    class FootView implements HeaderAndFooterWrapper.ItemView{

        @Override
        public ViewDataBinding onCreateView(ViewGroup parent) {
            ViewDataBinding mFootBinding=DataBindingUtil.inflate(LayoutInflater.from(
                    parent.getContext()),R.layout.footer_book_shelf,parent,false);
            mFootBinding.setVariable(BR.presenter,new Presenter());
            return mFootBinding;
        }
    }

    public Presenter getPresenter(){
        return new Presenter();
    }

    public class Presenter{
        public void footClick(){
            Toast.makeText(mContext, "footClick", Toast.LENGTH_SHORT).show();
        }
        public void refresh(){
            mViewModel.refreshShelfBooks();
            mBinding.swipeRefresh.setRefreshing(false);
            Toast.makeText(mContext, "小说已更新", Toast.LENGTH_SHORT).show();
        }
        public void openReadActivity(View view,CollectBookEntity book){
            Bundle bundle=new Bundle();
            bundle.putParcelable(ReadActivity.EXTRA_COLLECT_BOOK,book);
            bundle.putBoolean(ReadActivity.EXTRA_IS_COLLECTED,true);
            Navigation.findNavController(view).navigate(R.id.openReadAction,bundle);
        }
    }
}
