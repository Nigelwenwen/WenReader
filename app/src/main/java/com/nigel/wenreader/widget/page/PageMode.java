package com.nigel.wenreader.widget.page;

/**
 * 翻页动画的样式
 * 因为android中枚举占用内存会多点，所以简单常量就不用枚举了
 */
public class PageMode {
    //仿真
    public static final int SIMULATION=0;
    //覆盖
    public static final int COVER=1;
    //滑动
    public static final int SLIDE=2;
    //无
    public static final int NONE=3;
    //拖动
    public static final int SCROLL=4;
}
