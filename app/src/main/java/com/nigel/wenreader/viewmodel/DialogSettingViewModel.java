package com.nigel.wenreader.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.nigel.wenreader.App;
import com.nigel.wenreader.R;
import com.nigel.wenreader.adapter.PageStyleAdapter;
import com.nigel.wenreader.model.local.PreSettingsManager;
import com.nigel.wenreader.widget.page.PageLoader;
import com.nigel.wenreader.widget.page.PageStyle;

import java.util.ArrayList;
import java.util.List;

public class DialogSettingViewModel extends AndroidViewModel {
    private PreSettingsManager mPreSettingsManager;

    private MutableLiveData<Integer> mPageMode=new MutableLiveData<>();
    private MutableLiveData<PageStyle> mPageStyle=new MutableLiveData<>();
    private MutableLiveData<Integer> mBrightness=new MutableLiveData<>();
    private MutableLiveData<Integer> mTextSize=new MutableLiveData<>();

    private MutableLiveData<Boolean> isBrightnessAuto=new MutableLiveData<>();
    private MutableLiveData<Boolean> isTextDefault=new MutableLiveData<>();

    private List<Integer> colors=new ArrayList<>();


    public DialogSettingViewModel(@NonNull Application application) {
        super(application);
        initData();
    }

    private void initData() {
        mPreSettingsManager=PreSettingsManager.getInstance();

        mPageMode.setValue(mPreSettingsManager.getPageMode());
        mPageStyle.setValue(mPreSettingsManager.getPageStyle());
        mBrightness.setValue(mPreSettingsManager.getBrightness());
        mTextSize.setValue(mPreSettingsManager.getTextSize());
        isTextDefault.setValue(mPreSettingsManager.isDefaultTextSize());
        isBrightnessAuto.setValue(mPreSettingsManager.isBrightnessAuto());
        colors.add(getColor(R.color.read_bg_1));
        colors.add(getColor(R.color.read_bg_2));
        colors.add(getColor(R.color.read_bg_3));
        colors.add(getColor(R.color.read_bg_4));
        colors.add(getColor(R.color.read_bg_5));
    }

    private int getColor(int color){
        return ContextCompat.getColor(App.getContext(),color);
    }

    public MutableLiveData<Integer> getPageMode() {
        return mPageMode;
    }

    public MutableLiveData<PageStyle> getPageStyle() {
        return mPageStyle;
    }

    public MutableLiveData<Integer> getBrightness() {
        return mBrightness;
    }

    public MutableLiveData<Integer> getTextSize() {
        return mTextSize;
    }

    public MutableLiveData<Boolean> getIsBrightnessAuto() {
        return isBrightnessAuto;
    }

    public MutableLiveData<Boolean> getIsTextDefault() {
        return isTextDefault;
    }

    public void setBrightness(MutableLiveData<Integer> brightness) {
        mBrightness = brightness;
    }

    public void setTextSize(MutableLiveData<Integer> textSize) {
        mTextSize = textSize;
    }

    public List<Integer> getColors() {
        return colors;
    }

}
