package com.nigel.wenreader.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nigel.wenreader.BR;
import com.nigel.wenreader.R;
import com.nigel.wenreader.adapter.view.BindingViewHolder;
import com.nigel.wenreader.widget.page.PageStyle;

public class PageStyleAdapter extends BaseAdapter<Integer> {
    private static final String TAG = "PageStyleAdapter";
    public static PageStyleAdapter getInstance(){
        Log.d(TAG, "getInstance: return");
        return new PageStyleAdapter();
    }

    private int checked;
    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ViewDataBinding binding=DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                R.layout.item_read_bg,viewGroup,false);
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder bindingViewHolder, int i) {
        int color=mList.get(i);
        bindingViewHolder.getBinding().setVariable(BR.color,color);
        bindingViewHolder.itemView.setOnClickListener(v->{
            mListener.OnItemClick(i);
            bindingViewHolder.getBinding().setVariable(BR.checked,true);
            notifyDataSetChanged();
        });
        if(checked==i){
            bindingViewHolder.getBinding().setVariable(BR.checked,true);
        }else {
            bindingViewHolder.getBinding().setVariable(BR.checked,false);
        }
        bindingViewHolder.getBinding().executePendingBindings();
    }

    public void setPageStyleChecked(PageStyle pageStyle){
        checked = pageStyle.ordinal();
    }

}
