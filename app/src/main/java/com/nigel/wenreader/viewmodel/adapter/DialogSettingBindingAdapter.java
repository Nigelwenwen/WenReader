package com.nigel.wenreader.viewmodel.adapter;

import android.databinding.BindingAdapter;
import android.databinding.BindingConversion;
import android.databinding.InverseMethod;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.nigel.wenreader.R;
import com.nigel.wenreader.adapter.BaseAdapter;
import com.nigel.wenreader.adapter.PageStyleAdapter;
import com.nigel.wenreader.ui.fragment.ReadSettingDialogFragment;
import com.nigel.wenreader.widget.page.PageStyle;

import java.util.List;

import io.reactivex.annotations.Nullable;

public class DialogSettingBindingAdapter {
    private static final String TAG = "DialogSettingBindingAda";
    @BindingConversion
    public static String convertIntToString(int size){
        return String.valueOf(size);
    }

    @InverseMethod("idToPageMode")
    public static int pageModeToId(int pageMode){
        switch(pageMode){
            case 0:
                return R.id.read_setting_simulation;
            case 1:
                return R.id.read_setting_cover;
            case 2:
                return R.id.read_setting_scroll;
            case 3:
                return R.id.read_setting_none;
            default:
                return R.id.read_setting_simulation;
        }
    }

    public static int idToPageMode(int id){
        switch(id){
            case  R.id.read_setting_simulation:
                return 0;
            case R.id.read_setting_cover:
                return 1;
            case  R.id.read_setting_scroll:
                return 2;
            case R.id.read_setting_none:
                return 3;
            default:
                return 0;
        }

    }

    @BindingAdapter(value = {"list"})
    public static void loadList(RecyclerView recyclerView, List<Integer> list){
        PageStyleAdapter adapter=(PageStyleAdapter) recyclerView.getAdapter();
        adapter.refreshItems(list);
    }

    @BindingAdapter(value = {"checked"})
    public static void setChecked(RecyclerView recyclerView,PageStyle pageStyle){
        PageStyleAdapter adapter=(PageStyleAdapter) recyclerView.getAdapter();
        Log.d(TAG, "setChecked: getAdapter"+(adapter==null?"null":"not null"));
        adapter.setPageStyleChecked(pageStyle);
    }

    @BindingAdapter(value = {"onItemClick"})
    public static void setOnItemClick(RecyclerView recyclerView, BaseAdapter.OnItemClickListener listener){
        Handler handler=new Handler();
        handler.post(() -> {
            PageStyleAdapter adapter=(PageStyleAdapter) recyclerView.getAdapter();
            Log.d(TAG, "setOnItemClick: getAdapter"+(adapter==null?"null":"not null"));
            if(adapter==null){
                return;
            }
            adapter.setListener(listener);
        });

    }
}
