package com.nigel.wenreader.utils;

import android.support.annotation.StringRes;

import com.nigel.wenreader.App;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author nigel
 * @description
 * @since 2018-09-15
 */
public class StringUtils {

    private static final String TAG = "StringUtils";
    private static final int HOUR_OF_DAY = 24;
    private static final int DAY_OF_YESTERDAY = 2;
    private static final int TIME_UNIT = 60;

    //将时间转换成日期
    public static String dateConvert(long time,String pattern){
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static String getString(@StringRes int id){
        return App.getContext().getResources().getString(id);
    }

    public static String getString(@StringRes int id, Object... formatArgs){
        return App.getContext().getResources().getString(id,formatArgs);
    }

    /**
     * 将文本中的半角字符，转换成全角字符
     * @param input
     * @return
     */
    public static String halfToFull(String input)
    {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++)
        {
            if (c[i] == 32) //半角空格
            {
                c[i] = (char) 12288;
                continue;
            }
            //根据实际情况，过滤不需要转换的符号
            //if (c[i] == 46) //半角点号，不转换
            // continue;

            if (c[i]> 32 && c[i]< 127)    //其他符号都转换为全角
                c[i] = (char) (c[i] + 65248);
        }
        return new String(c);
    }
}
