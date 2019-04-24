package com.nigel.wenreader.utils;

import android.app.Activity;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

/**
 * 亮度调节工具
 */
public class BrightnessUtils {
    private static final String TAG = "BrightnessUtils";

    /**
     * 判断亮度自动调节是否开启
     * @param activity
     * @return
     */
    public static boolean isAutoBrightness(Activity activity){
        boolean isAuto=false;
        try {
            isAuto=Settings.System.getInt(activity.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE)
                    ==Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return isAuto;
    }

    /**
     * 获取屏幕亮度，无法获取自动模式下的屏幕亮度，所以设为 -1
     * @param activity
     * @return value:0~255
     */
    public static int getScreenBrightness(Activity activity){
        int value=0;
        if(isAutoBrightness(activity)){
            value=-1;
        }else {
            try {
                value=Settings.System.getInt(activity.getContentResolver(),Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * window当前屏幕亮度调节，不会影响其他地方的亮度
     * lp.screenBrightness的范围为0~1f
     * @param activity
     * @param brightness
     */
    public static void setScreenBrightness(Activity activity,int brightness){
        Window window=activity.getWindow();
        WindowManager.LayoutParams lp=window.getAttributes();
        if(brightness==-1){
            lp.screenBrightness= WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        }else{
            lp.screenBrightness=(brightness<0?1:brightness)/255f;
        }
        window.setAttributes(lp);
    }
}