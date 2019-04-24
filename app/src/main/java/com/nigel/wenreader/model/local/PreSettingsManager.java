package com.nigel.wenreader.model.local;

import android.preference.PreferenceManager;

import com.nigel.wenreader.utils.ScreenUtils;
import com.nigel.wenreader.utils.SharedPreUtils;
import com.nigel.wenreader.widget.page.PageMode;
import com.nigel.wenreader.widget.page.PageStyle;


public class PreSettingsManager {

    public static final String SHARED_READ_BG = "shared_read_bg";
    public static final String SHARED_READ_BRIGHTNESS = "shared_read_brightness";
    public static final String SHARED_READ_IS_BRIGHTNESS_AUTO = "shared_read_is_brightness_auto";
    public static final String SHARED_READ_TEXT_SIZE = "shared_read_text_size";
    public static final String SHARED_READ_IS_TEXT_DEFAULT = "shared_read_text_default";
    public static final String SHARED_READ_PAGE_MODE = "shared_read_mode";
    public static final String SHARED_READ_NIGHT_MODE = "shared_night_mode";
    public static final String SHARED_READ_VOLUME_TURN_PAGE = "shared_read_volume_turn_page";
    public static final String SHARED_READ_FULL_SCREEN = "shared_read_full_screen";
    public static final String SHARED_READ_CONVERT_TYPE = "shared_read_convert_type";

    private static volatile PreSettingsManager sInstance;
    private SharedPreUtils mSharedPreUtils;

    private PreSettingsManager() {
        mSharedPreUtils = SharedPreUtils.getInstance();
    }

    public static PreSettingsManager getInstance() {
        if(sInstance==null){
            synchronized (PreferenceManager.class){
                if (sInstance == null) {
                    sInstance=new PreSettingsManager();
                }
            }
        }
        return  sInstance;
    }


    public void setPageStyle(PageStyle pageStyle) {
        mSharedPreUtils.putInt(SHARED_READ_BG, pageStyle.ordinal());
    }

    public void setBrightness(int progress) {
        mSharedPreUtils.putInt(SHARED_READ_BRIGHTNESS, progress);
    }

    public void setAutoBrightness(boolean isAuto) {
        mSharedPreUtils.putBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, isAuto);
    }

    public void setDefaultTextSize(boolean isDefault) {
        mSharedPreUtils.putBoolean(SHARED_READ_IS_TEXT_DEFAULT, isDefault);
    }

    public void setTextSize(int textSize) {
        mSharedPreUtils.putInt(SHARED_READ_TEXT_SIZE, textSize);
    }

    public void setPageMode(int mode) {
        mSharedPreUtils.putInt(SHARED_READ_PAGE_MODE, mode);
    }

    public void setNightMode(boolean isNight) {
        mSharedPreUtils.putBoolean(SHARED_READ_NIGHT_MODE, isNight);
    }

    public int getBrightness() {
        return mSharedPreUtils.getInt(SHARED_READ_BRIGHTNESS, 40);
    }

    public boolean isBrightnessAuto() {
        return mSharedPreUtils.getBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, false);
    }

    public int getTextSize() {
        return mSharedPreUtils.getInt(SHARED_READ_TEXT_SIZE, ScreenUtils.spToPx(20));
    }

    public boolean isDefaultTextSize() {
        return mSharedPreUtils.getBoolean(SHARED_READ_IS_TEXT_DEFAULT, false);
    }

    public int getPageMode() {
        int mode = mSharedPreUtils.getInt(SHARED_READ_PAGE_MODE, PageMode.SIMULATION);
        return mode;
    }

    public PageStyle getPageStyle() {
        int style = mSharedPreUtils.getInt(SHARED_READ_BG, PageStyle.BG_0.ordinal());
        return PageStyle.values()[style];
    }

    public boolean isNightMode() {
        return mSharedPreUtils.getBoolean(SHARED_READ_NIGHT_MODE, false);
    }

    public void setVolumeTurnPage(boolean isTurn) {
        mSharedPreUtils.putBoolean(SHARED_READ_VOLUME_TURN_PAGE, isTurn);
    }

    public boolean isVolumeTurnPage() {
        return mSharedPreUtils.getBoolean(SHARED_READ_VOLUME_TURN_PAGE, false);
    }

    public void setFullScreen(boolean isFullScreen) {
        mSharedPreUtils.putBoolean(SHARED_READ_FULL_SCREEN, isFullScreen);
    }

    public boolean isFullScreen() {
        return mSharedPreUtils.getBoolean(SHARED_READ_FULL_SCREEN, true);
    }

    public void setConvertType(int convertType) {
        mSharedPreUtils.putInt(SHARED_READ_CONVERT_TYPE, convertType);
    }

    public int getConvertType() {
        return mSharedPreUtils.getInt(SHARED_READ_CONVERT_TYPE, 0);
    }
}
