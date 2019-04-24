package com.nigel.wenreader.adapter;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.nigel.wenreader.adapter.view.BindingViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BaseAdapter<T> extends RecyclerView.Adapter<BindingViewHolder> {
    private static final String TAG = "BaseAdapter";
    List<T> mList = new ArrayList<>();
    List<T> mOldList=new ArrayList<>();
    //记录item是否被选中的Map,
    HashMap<T, Boolean> mCheckMap = new HashMap<>();
    OnItemClickListener mListener;
    boolean isBatchMode;
    boolean isSelectAll;
    MutableLiveData<Integer> mCheckedCount = new MutableLiveData<>();



    public BaseAdapter() {
        mCheckedCount.setValue(0);
    }

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: BaseAdapter");
        return new BindingViewHolder(new View(null));
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, int i) {

    }

    public void refreshItems(List<T> list) {
        mList.clear();
        mList.addAll(list);
        mCheckMap.clear();
        for (T t : list) {
            mCheckMap.put(t, false);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List<T> getList() {
        return mList;
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }

    public void selectAll(boolean isChecked) {
        int count = 0;
        for (Map.Entry<T, Boolean> entry : mCheckMap.entrySet()) {
            entry.setValue(isChecked);
            if (isChecked) {
                count++;
            }
        }
        mCheckedCount.setValue(count);
        isSelectAll = isChecked;
        notifyDataSetChanged();
    }

    private void selectOne(T one) {
        mCheckMap.put(one, true);
        mCheckedCount.setValue(mCheckedCount.getValue() + 1);
    }

    private void removeOne(T one) {
        mCheckMap.put(one, false);
        mCheckedCount.setValue(mCheckedCount.getValue() - 1);
    }

    public T removeItem(int position){
        return mList.remove(position);
    }

    public void removeItems(List<T> items){
        //删除在HashMap中的文件
        for (T item : items){
            mCheckMap.remove(item);
        }
        mCheckedCount.setValue(mCheckedCount.getValue()-items.size());
        //删除列表中的文件
        mOldList.clear();
        mOldList.addAll(mList);
        mList.removeAll(items);
    }

    public boolean isBatchMode() {
        return isBatchMode;
    }

    public void setBatchMode(boolean batcheMode) {
        isBatchMode = batcheMode;
        isSelectAll = false;
        for (Map.Entry<T, Boolean> entry : mCheckMap.entrySet()) {
            entry.setValue(false);
        }
        notifyDataSetChanged();
    }

    public MutableLiveData<Integer> getCheckedCount() {
        return mCheckedCount;
    }

    void checkChanged(CheckBox checkBox, T t) {
        if (checkBox.isChecked()) {
            selectOne(t);
            Log.d(TAG, "checkChanged: select");
        } else {
            removeOne(t);
            Log.d(TAG, "checkChanged: remove");
        }
    }

    public List<T> getCheckedItems(){
        List<T> items = new ArrayList<>();
        Set<Map.Entry<T, Boolean>> entrys = mCheckMap.entrySet();
        for (Map.Entry<T, Boolean> entry:entrys){
            if (entry.getValue()){
                items.add(entry.getKey());
            }
        }
        return items;
    }
}
