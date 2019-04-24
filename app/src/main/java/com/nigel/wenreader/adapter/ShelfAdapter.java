package com.nigel.wenreader.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.transition.Transition;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.nigel.wenreader.BR;
import com.nigel.wenreader.R;
import com.nigel.wenreader.adapter.view.BindingViewHolder;
import com.nigel.wenreader.databinding.ItemShelfBinding;
import com.nigel.wenreader.db.entity.CollectBookEntity;
import com.nigel.wenreader.ui.fragment.ShelfFragment;

public class ShelfAdapter extends BaseAdapter<CollectBookEntity> {
    private static final String TAG = "ShelfAdapter";
    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        ViewDataBinding binding=DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),R.layout.item_shelf,parent,false);
        binding.setVariable(BR.presenter,new ShelfFragment().getPresenter());
        return new BindingViewHolder<>(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder holder, int i) {
        ItemShelfBinding binding=(ItemShelfBinding) holder.getBinding();
        binding.setVariable(BR.matchMode,isBatchMode);
        binding.setVariable(BR.book,mList.get(i));
        binding.setVariable(BR.adapter,this);
        binding.setVariable(BR.isSelectAll,isSelectAll);
        binding.collBookCbSelected.setOnClickListener((view)->checkChanged(
                binding.collBookCbSelected,mList.get(i)));
        Log.d(TAG, "onBindViewHolder: "+isSelectAll);
        binding.executePendingBindings();
    }



    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public static class RecyclerViewContextInfo implements ContextMenu.ContextMenuInfo {
        private int mPosition = -1;

        public int getPosition() {
            return mPosition;
        }
    }


}
