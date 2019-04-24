package com.nigel.wenreader.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nigel.wenreader.BR;
import com.nigel.wenreader.R;
import com.nigel.wenreader.adapter.view.BindingViewHolder;
import com.nigel.wenreader.databinding.ItemCatalogBinding;
import com.nigel.wenreader.widget.page.TxtChapter;


/**
 * @author 123
 * @description
 * @since 2018-09-28
 */
public class CatalogAdapter extends BaseAdapter<TxtChapter>{
    private static final String TAG = "CatalogAdapter";

    private int selected=0;

    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        ViewDataBinding binding=DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),R.layout.item_catalog,parent,false);
        return new BindingViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, int i) {
        ItemCatalogBinding catalogbinding=(ItemCatalogBinding) (holder.getBinding());
        TxtChapter chapter=mList.get(i);
        holder.getBinding().setVariable(BR.chapter,chapter);
        holder.itemView.setOnClickListener(v -> mListener.OnItemClick(i));
        if(i==selected){
            catalogbinding.catalogChapter.setSelected(true);
        }else {
            catalogbinding.catalogChapter.setSelected(false);
        }
        holder.getBinding().executePendingBindings();
    }

    public void setSelectedChapter(int pos) {
        selected=pos;
        notifyDataSetChanged();
    }
}
