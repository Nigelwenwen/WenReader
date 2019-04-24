package com.nigel.wenreader.adapter;

import android.arch.lifecycle.MutableLiveData;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.nigel.wenreader.adapter.view.BindingViewHolder;
import com.nigel.wenreader.db.entity.CollectBookEntity;

import java.util.List;

/**
 * 装饰者模式，给没有头尾的Adapter加上头尾
 * 其实也可以做成继承，但是从来没怎么用过装饰者模式，就想用用看
 * 但好像我写的有点烂，notify必须要在装饰者里触发，不然被装饰者是不会notify的，不知道会不会有性能问题
 * @param <T>
 */
public class HeaderAndFooterWrapper<T> extends BaseAdapter<T>{

    private static final String TAG = "HeaderAndFooterWrapper";
    private static final int BASE_ITEM_TYPE_HEADER = 100000;
    private static final int BASE_ITEM_TYPE_FOOTER = 200000;

    private BaseAdapter<T> mInnerAdapter;

    private SparseArrayCompat<ItemView> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<ItemView> mFootViews = new SparseArrayCompat<>();

    public HeaderAndFooterWrapper(BaseAdapter<T> adapter) {
        mInnerAdapter = adapter;
    }



    @SuppressWarnings("ConstantConditions")
    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null)
        {
            Log.d(TAG, "onCreateViewHolder: head");
            ViewDataBinding binding=mHeaderViews.get(viewType).onCreateView(parent);
            return new BindingViewHolder<>(binding);
        } else if (mFootViews.get(viewType) != null) {
            Log.d(TAG, "onCreateViewHolder: foot");
            ViewDataBinding binding=mFootViews.get(viewType).onCreateView(parent);
            return new BindingViewHolder<>(binding);
        }
        Log.d(TAG, "onCreateViewHolder: InnerAdapter");
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, int position) {
        if (isHeaderViewPos(position))
        {
            return;
        }
        if (isFooterViewPos(position))
        {
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeaderViewPos(position))
        {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position))
        {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return mInnerAdapter.getItemViewType(position - getHeadersCount());
    }

    private boolean isHeaderViewPos(int position) {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position) {
        return position >= getHeadersCount() + getRealItemCount();
    }

    public void addHeaderView(ItemView view) {
        mHeaderViews.put(mHeaderViews.size() + BASE_ITEM_TYPE_HEADER, view);
    }

    public void addFootView(ItemView view) {
        mFootViews.put(mFootViews.size() + BASE_ITEM_TYPE_FOOTER,view);
    }

    private int getHeadersCount() {
        return mHeaderViews.size();
    }

    private int getFootersCount() {
        return mFootViews.size();
    }

    private int getRealItemCount() {
        return mInnerAdapter.getItemCount();
    }

    @Override
    public void refreshItems(List<T> list) {
        mInnerAdapter.refreshItems(list);
        notifyDataSetChanged();
    }

    @Override
    public boolean isBatchMode() {
        return mInnerAdapter.isBatchMode();
    }

    @Override
    public void setBatchMode(boolean batchMode) {
        mInnerAdapter.setBatchMode(batchMode);
        notifyDataSetChanged();
    }

    @Override
    public List<T> getList() {
        return mInnerAdapter.getList();
    }

    @Override
    public void setListener(OnItemClickListener listener) {
        mInnerAdapter.setListener(listener);
    }

    @Override
    public void selectAll(boolean isChecked) {
        mInnerAdapter.selectAll(isChecked);
        notifyDataSetChanged();
    }

    @Override
    public MutableLiveData<Integer> getCheckedCount() {
        return mInnerAdapter.getCheckedCount();
    }

    @Override
    public List<T> getCheckedItems() {
        return mInnerAdapter.getCheckedItems();
    }

    @Override
    public void removeItems(List<T> items) {
        mInnerAdapter.removeItems(items);
        notifyDataSetChanged();
    }

    @Override
    public T removeItem(int position){
        T t=mInnerAdapter.removeItem(position-getHeadersCount());
        notifyItemRemoved(position);
        return t;
    }

    @Override
    public int getItemCount() {
        return getHeadersCount()+getFootersCount()+getRealItemCount();
    }

    public interface ItemView{
        ViewDataBinding onCreateView(ViewGroup parent);
    }
}

