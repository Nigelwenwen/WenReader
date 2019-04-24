package com.nigel.wenreader.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.nigel.wenreader.App;

/**
 * 单例模式
 */
public class SharedPreUtils {
    private static volatile SharedPreUtils sInstance;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private SharedPreUtils() {
        mSharedPreferences =PreferenceManager.getDefaultSharedPreferences(App.getContext());
        mEditor = mSharedPreferences.edit();
    }

    public static SharedPreUtils getInstance() {
        if(sInstance == null){
            synchronized (SharedPreUtils.class){
                if (sInstance == null){
                    sInstance = new SharedPreUtils();
                }
            }
        }
        return sInstance;
    }

    public String getString(String key){
        return mSharedPreferences.getString(key,"");
    }

    public void putString(String key,String value){
        mEditor.putString(key,value);
        mEditor.apply();
    }

    public void putInt(String key,int value){
        mEditor.putInt(key, value);
        mEditor.apply();
    }

    public void putBoolean(String key,boolean value){
        mEditor.putBoolean(key, value);
        mEditor.apply();
    }

    public int getInt(String key,int def){
        return mSharedPreferences.getInt(key, def);
    }

    public boolean getBoolean(String key,boolean def){
        return mSharedPreferences.getBoolean(key, def);
    }
}
