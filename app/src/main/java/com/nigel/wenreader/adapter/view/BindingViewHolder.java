package com.nigel.wenreader.adapter.view;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    private T binding;


    public BindingViewHolder(@NonNull T binding) {
        super(binding.getRoot());
        this.binding=binding;
    }

    public BindingViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public T getBinding() {
        return binding;
    }

}
