package com.nigel.wenreader.viewmodel.adapter;

import android.databinding.BindingAdapter;
import android.support.v4.widget.SwipeRefreshLayout;

public class SwipeRefreshBindingAdapter {
    private static final String TAG = "SwipeRefreshBindingAdap";
    @BindingAdapter(value = {"colorSchemeResources"})
    public static void setColor(SwipeRefreshLayout swipeRefreshLayout, int color){
        swipeRefreshLayout.setColorSchemeColors(color);
    }

    @BindingAdapter(value = {"onRefreshListener"})
    public static void setOnRefreshListener(SwipeRefreshLayout swipeRefreshLayout,SwipeRefreshLayout.OnRefreshListener listener){
        swipeRefreshLayout.setOnRefreshListener(listener);
    }
}
