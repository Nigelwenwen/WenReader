package com.nigel.wenreader.widget.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.nigel.wenreader.App;
import com.nigel.wenreader.R;
import com.nigel.wenreader.db.entity.CollectBookEntity;
import com.nigel.wenreader.model.local.PreSettingsManager;
import com.nigel.wenreader.widget.animation.CoverPageAnim;
import com.nigel.wenreader.widget.animation.HorizonPageAnim;
import com.nigel.wenreader.widget.animation.NonePageAnim;
import com.nigel.wenreader.widget.animation.PageAnimation;
import com.nigel.wenreader.widget.animation.ScrollPageAnim;
import com.nigel.wenreader.widget.animation.SimulationPageAnim;
import com.nigel.wenreader.widget.animation.SlidePageAnim;

/**
 * 绘制页面显示内容
 */
public class PageView extends View {
    private static final String TAG = "PageView";

    //控件的宽高
    private int mHeight=0;
    private int mWidth=0;

    private int mStartX = 0;
    private int mStartY = 0;
    private boolean isMove = false;

    //初始背景
    private int mBgColor = ContextCompat.getColor(App.getContext(),R.color.read_bg_2);
    private boolean isPrepare;
    //翻页动画类
    private PageAnimation mPageAnim;
    //翻页模式
    private int mPageMode;
    // 是否允许点击
    private boolean canTouch = true;
    // 唤醒菜单的区域
    private RectF mCenterRect = null;
    // 动画监听类
    private PageAnimation.OnPageChangeListener mPageAnimListener = new PageAnimation.OnPageChangeListener() {
        @Override
        public boolean hasPrev() {
            return PageView.this.hasPrevPage();
        }

        @Override
        public boolean hasNext() {
            return PageView.this.hasNextPage();
        }

        @Override
        public void pageCancel() {
            PageView.this.pageCancel();
        }
    };

    //点击监听
    private TouchListener touchListener;
    //内容加载类
    private PageLoader mPageLoader;

    public PageView(Context context) {
        this(context,null);
    }

    public PageView(Context context,  @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PageView(Context context,  @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d(TAG, "PageView: create");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: w:"+w+"h:"+h);
        mWidth=w;
        mHeight=h;
        isPrepare = true;

        if (mPageLoader != null) {
            mPageLoader.prepareDisplay(w, h);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPageAnim.abortAnim();
        mPageAnim.clear();

        mPageLoader = null;
        mPageAnim = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制背景
        canvas.drawColor(mBgColor);
        //绘制动画
        mPageAnim.draw(canvas);
        Log.d(TAG, "onDraw: ondraw");

    }

    /**
     * 绘制当前页
     * @param isUpdate
     */
    public void drawCurPage(boolean isUpdate) {
        if (!isPrepare) {
            Log.d(TAG, "drawCurPage: not prepare");
            return;
        }
        if (!isUpdate){
            Log.d(TAG, "drawCurPage: isUpdate:false");
            if (mPageAnim instanceof ScrollPageAnim) {
                ((ScrollPageAnim) mPageAnim).resetBitmap();
            }
        }
        mPageLoader.drawPage(getNextBitmap(), isUpdate);
    }

    /**
     * 绘制下一页
     */
    public void drawNextPage() {
        if (!isPrepare) return;

        if (mPageAnim instanceof HorizonPageAnim) {
            ((HorizonPageAnim) mPageAnim).changePage();
        }
        mPageLoader.drawPage(getNextBitmap(), false);
    }

    public Bitmap getNextBitmap() {
        if (mPageAnim == null) return null;
        return mPageAnim.getNextBitmap();
    }

    public Bitmap getBgBitmap() {
        if (mPageAnim == null){
            Log.d(TAG, "getBgBitmap: is null");
            return null;
        }
        return mPageAnim.getBgBitmap();
    }

    void setPageMode(int pageMode) {
        mPageMode=pageMode;
        //视图未初始化的时候，禁止调用
        if (mWidth == 0 || mHeight == 0){
            Log.d(TAG, "setPageMode: 未初始化");
            return;
        }
        switch(mPageMode) {
            case PageMode.SIMULATION:
                mPageAnim=new SimulationPageAnim(mWidth, mHeight, this, mPageAnimListener);
                break;
            case PageMode.COVER:
                mPageAnim = new CoverPageAnim(mWidth, mHeight, this, mPageAnimListener);
                break;
            case PageMode.SLIDE:
                mPageAnim = new SlidePageAnim(mWidth, mHeight, this, mPageAnimListener);
                break;
            case PageMode.NONE:
                mPageAnim = new NonePageAnim(mWidth, mHeight, this, mPageAnimListener);
                break;
            case PageMode.SCROLL:
                mPageAnim = new ScrollPageAnim(mWidth, mHeight, 0,
                        mPageLoader.getMarginHeight(), this, mPageAnimListener);
                break;
            default:
                mPageAnim = new SimulationPageAnim(mWidth, mHeight, this, mPageAnimListener);
        }
        Log.d(TAG, "setPageMode: PageView"+mPageAnim);
    }

    public boolean autoPrevPage() {
        //滚动暂时不支持自动翻页
        if (mPageAnim instanceof ScrollPageAnim) {
            return false;
        } else {
            startPageAnim(PageAnimation.Direction.PRE);
            return true;
        }
    }

    public boolean autoNextPage() {
        if (mPageAnim instanceof ScrollPageAnim) {
            return false;
        } else {
            startPageAnim(PageAnimation.Direction.NEXT);
            return true;
        }
    }


    private void startPageAnim(PageAnimation.Direction direction) {
        if (touchListener == null) return;
        //是否正在执行动画
        abortAnimation();
        if (direction == PageAnimation.Direction.NEXT) {
            int x = mWidth;
            int y = mHeight;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
            //设置方向
            mPageAnim.setDirection(direction);
            if (!hasNextPage()) {
                return;
            }
        } else {
            int x = 0;
            int y = mHeight;
            //初始化动画
            mPageAnim.setStartPoint(x, y);
            //设置点击点
            mPageAnim.setTouchPoint(x, y);
            mPageAnim.setDirection(direction);
            //设置方向方向
            if (!hasPrevPage()) {
                return;
            }
        }
        mPageAnim.startAnim();
        this.postInvalidate();
    }

    //如果滑动状态没有停止就取消状态，重新设置Anim的触碰点
    public void abortAnimation() {
        mPageAnim.abortAnim();
    }

    public void setBgColor(int bgColor) {
        mBgColor=bgColor;
        Log.d(TAG, "setBgColor: "+bgColor);
    }

    /**
     * 判断是否存在上一页
     *
     * @return
     */
    private boolean hasPrevPage() {
        touchListener.prePage();
        return mPageLoader.prev();
    }

    /**
     * 判断是否下一页存在
     *
     * @return
     */
    private boolean hasNextPage() {
        touchListener.nextPage();
        return mPageLoader.next();
    }

    private void pageCancel() {
        touchListener.cancel();
        mPageLoader.pageCancel();
    }

    /**
     * 获取 PageLoader
     *
     * @param collBook
     * @return
     */
    public PageLoader getPageLoader(CollectBookEntity collBook) {
        // 判是否已经存在
        if (mPageLoader != null) {
            return mPageLoader;
        }
        // 根据书籍类型，获取具体的加载器
        if (collBook.isLocal()) {
            mPageLoader = new LocalPageLoader(this, collBook);
        } else {
            mPageLoader = new NetPageLoader(this, collBook);
        }
        // 判断是否 PageView 已经初始化完成
        if (mWidth != 0 || mHeight != 0) {
            // 初始化 PageLoader 的屏幕大小
            mPageLoader.prepareDisplay(mWidth, mHeight);
        }
        return mPageLoader;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (!canTouch&&event.getAction()!=MotionEvent.ACTION_DOWN){
            return true;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                isMove = false;
                canTouch = touchListener.onTouch();
                mPageAnim.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                // 判断是否大于最小滑动值。
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (!isMove) {
                    isMove = Math.abs(mStartX - event.getX()) > slop || Math.abs(mStartY - event.getY()) > slop;
                }

                // 如果滑动了，则进行翻页。
                if (isMove) {
                    mPageAnim.onTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isMove) {
                    //设置中间区域范围
                    if (mCenterRect == null) {
                        mCenterRect = new RectF(mWidth / 5, mHeight / 3,
                                mWidth * 4 / 5, mHeight * 2 / 3);
                    }

                    //是否点击了中间
                    if (mCenterRect.contains(x, y)) {
                        if (touchListener != null) {
                            touchListener.center();
                        }
                        return true;
                    }
                }
                mPageAnim.onTouchEvent(event);
                break;
        }
        return true;
    }

    public boolean isRunning() {
        if (mPageAnim == null) {
            return false;
        }
        return mPageAnim.isRunning();
    }

    @Override
    public void computeScroll() {
        //进行滑动
        mPageAnim.scrollAnim();
        super.computeScroll();
    }

    public void setTouchListener(TouchListener touchListener) {
        this.touchListener = touchListener;
        Log.d(TAG, "setTouchListener: over");
    }

    public boolean isPrepare() {
        return isPrepare;
    }

    public interface TouchListener {
        boolean onTouch();

        void center();

        void prePage();

        void nextPage();

        void cancel();
    }
}
