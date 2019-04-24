package com.nigel.wenreader.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.databinding.library.baseAdapters.BR;
import com.nigel.wenreader.R;
import com.nigel.wenreader.adapter.CatalogAdapter;
import com.nigel.wenreader.databinding.ActivityReadBinding;
import com.nigel.wenreader.db.entity.BookChapterEntity;
import com.nigel.wenreader.db.entity.CollectBookEntity;
import com.nigel.wenreader.model.local.PreSettingsManager;
import com.nigel.wenreader.model.repository.BookRepository;
import com.nigel.wenreader.ui.fragment.ReadSettingDialogFragment;
import com.nigel.wenreader.utils.BrightnessUtils;
import com.nigel.wenreader.utils.RxUtils;
import com.nigel.wenreader.utils.ScreenUtils;
import com.nigel.wenreader.utils.SystemBarUtils;
import com.nigel.wenreader.viewmodel.ReadViewModel;
import com.nigel.wenreader.widget.page.PageLoader;
import com.nigel.wenreader.widget.page.PageStyle;
import com.nigel.wenreader.widget.page.PageView;
import com.nigel.wenreader.widget.page.TxtChapter;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class ReadActivity extends AppCompatActivity {
    //内容加载类
    private ReadViewModel mViewModel;
    private static final String TAG = "ReadActivity";
    public static final String EXTRA_COLLECT_BOOK = "extra_collect_book";
    public static final String EXTRA_IS_COLLECTED = "extra_is_collected";
    public static final int LOCAL_READ=0;
    public static final int NET_READ=1;
    // 注册 Brightness 的 uri
    private final Uri BRIGHTNESS_URI =
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);

    /***************params*****************/
    private boolean isCollected = false; // isFromSDCard
    private boolean isNightMode=false;
    private boolean isFullScreen = false;
    private boolean isRegistered = false;
    private boolean menuVisiable=false;
    private String mBookId;
    private int bgColor;

    /*****************view******************/
    private Animation mTopInAnim;
    private Animation mTopOutAnim;
    private Animation mBottomInAnim;
    private Animation mBottomOutAnim;
    private CollectBookEntity mCollectBook;
    private ActivityReadBinding binding;
    private PageLoader mPageLoader;
    private CatalogAdapter mCatalogAdapter;
    private ReadSettingDialogFragment mReadSettingDialogFragment;
    private Handler mHandler=new Handler();

    //电池和时间更新的广播接收器
    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                int level=intent.getIntExtra("level",0);
                mPageLoader.updateBattery(level);
            }else if(intent.getAction().equals(Intent.ACTION_TIME_TICK)){
                mPageLoader.updateTime();
            }
        }
    };

    //应用中开了跟随模式，但是系统未开自动调节，则用Observer监听系统亮度变化
    private ContentObserver mBrightnessObserver=new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange,null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange);
            // 判断当前是否跟随屏幕亮度，如果不是则返回
            if (selfChange || !mReadSettingDialogFragment.isBrightFollowSystem()) return;

            // 如果系统亮度改变，则修改当前 Activity 亮度,此时不在UI线程中，使用handler
            mHandler.post(() -> {
                if (BRIGHTNESS_URI.equals(uri)) {
                    BrightnessUtils.setScreenBrightness(ReadActivity.this, BrightnessUtils.getScreenBrightness(ReadActivity.this));
                }
            });
        }
    };
    private CompositeDisposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=DataBindingUtil.setContentView(this,R.layout.activity_read);
        mViewModel= ViewModelProviders.of(ReadActivity.this).get(ReadViewModel.class);
        initData();
        initMenuAnim();
        initToolBar();
        initWidget();
        initCallBack();
        processLogic();
    }


    private void initData() {
        mCollectBook=getIntent().getParcelableExtra(EXTRA_COLLECT_BOOK);
        isCollected=getIntent().getBooleanExtra(EXTRA_IS_COLLECTED,false);
        isNightMode=PreSettingsManager.getInstance().isNightMode();
        isFullScreen=PreSettingsManager.getInstance().isFullScreen();
        bgColor=PreSettingsManager.getInstance().getPageStyle().getBgColor();
        mBookId=mCollectBook.get_id();
        binding.setVariable(BR.isNightMode,isNightMode);
        binding.setVariable(BR.title,mCollectBook.getTitle());
        binding.setVariable(BR.presenter,new Presenter());
        binding.setVariable(BR.bgColor,ContextCompat.getColor(this,bgColor));
    }

    private void initToolBar() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        //半透明化StatusBar
        SystemBarUtils.transparentStatusBar(this);
        Log.d(TAG, "initToolBar: initToolBar");
    }

    private void initWidget() {

        // 如果 API < 18 取消硬件加速
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            binding.pageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mCatalogAdapter=new CatalogAdapter();
        binding.chapterCatalog.setAdapter(mCatalogAdapter);
        //获取页面加载类
        mPageLoader=binding.pageView.getPageLoader(mCollectBook);
        Log.d(TAG, "initWidget: getPageLoader");
        //给页面设置PageLoader
        binding.setVariable(BR.pageLoader,mPageLoader);
        //创建DialogFragment
        mReadSettingDialogFragment = new ReadSettingDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putSerializable("pageLoader",mPageLoader);
        mReadSettingDialogFragment.setArguments(bundle);

        //设置电池、时间的更新广播
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver,intentFilter);

        if(PreSettingsManager.getInstance().isBrightnessAuto()){
            BrightnessUtils.setScreenBrightness(this,-1);
        }else {
            BrightnessUtils.setScreenBrightness(this,PreSettingsManager.getInstance().getBrightness());
        }

        //控制屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //隐藏statusBar
        binding.pageView.post(this::hideSystemBar);

        //初始化TopMenu
        initTopMenu();

        //初始化BottomMenu
        initBottomMenu();
    }

    private void showSystemBar() {
        //显示
        SystemBarUtils.showUnStableStatusBar(this);
        if (isFullScreen) {
            SystemBarUtils.showUnStableNavBar(this);
        }
    }

    public void hideSystemBar() {
        //隐藏StatusBar
        SystemBarUtils.hideStableStatusBar(this);
        if (isFullScreen) {
            //隐藏NavigationBar
            SystemBarUtils.hideStableNavBar(this);
        }
    }

    private void initTopMenu() {
        if (Build.VERSION.SDK_INT >= 19) {
            binding.toolbar.setPadding(0, ScreenUtils.getStatusBarHeight(), 0, 0);
        }
    }

    private void initBottomMenu() {
        //判断是否全屏
        if (isFullScreen) {
            //还需要设置mBottomMenu的底部高度
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.bottomMenu.getLayoutParams();
            params.bottomMargin = ScreenUtils.getNavigationBarHeight();
            binding.bottomMenu.setLayoutParams(params);
        } else {
            //设置mBottomMenu的底部距离
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.bottomMenu.getLayoutParams();
            params.bottomMargin = 0;
            binding.bottomMenu.setLayoutParams(params);
        }
    }

    private void initCallBack() {
        mPageLoader.setOnPageChangeListener(new PageLoader.OnPageChangeListener() {
            @Override
            public void onChapterChange(int pos) {
                mCatalogAdapter.setSelectedChapter(pos);
            }

            @Override
            public void requestChapters(List<TxtChapter> requestChapters) {

            }

            @Override
            public void onCategoryFinish(List<TxtChapter> chapters) {
//                for (TxtChapter chapter : chapters) {
//                    chapter.setTitle(StringUtils.convertCC(chapter.getTitle(), mPvPage.getContext()));
//                }
                mCatalogAdapter.refreshItems(chapters);
            }

            @Override
            public void onPageCountChange(int count) {

            }

            @Override
            public void onPageChange(int pos) {

            }
        });


        mCatalogAdapter.setListener(p->{
            Handler handler=new Handler();
            handler.post(()->binding.drawerlayout.closeDrawer(Gravity.START));
            //耦合分的不够开，动画效果会停顿
            mPageLoader.skipToChapter(p);
        });
    }

    private void toggleMenu(boolean hideStatusBar) {
        if(binding.toolbar.getVisibility()==View.VISIBLE){
            binding.toolbar.startAnimation(mTopOutAnim);
            binding.bottomMenu.startAnimation(mBottomOutAnim);
            binding.toolbar.setVisibility(View.GONE);
            binding.bottomMenu.setVisibility(View.GONE);
//            binding.readTip.setVisibility(View.GONE);
            mBottomOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(hideStatusBar){
                        hideSystemBar();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }else{
            showSystemBar();
            binding.toolbar.setVisibility(View.VISIBLE);
            binding.bottomMenu.setVisibility(View.VISIBLE);
            binding.toolbar.startAnimation(mTopInAnim);
            binding.bottomMenu.startAnimation(mBottomInAnim);
        }
    }

    private void initMenuAnim() {
        if(mTopInAnim!=null) return;

        mTopInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_in);
        mTopOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_top_out);
        mBottomInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_in);
        mBottomOutAnim = AnimationUtils.loadAnimation(this, R.anim.slide_bottom_out);

        mTopOutAnim.setDuration(200);
        mBottomOutAnim.setDuration(200);
    }

    private boolean hideReadMenu() {
//        hideSystemBar();
        if(binding.toolbar.getVisibility()==View.VISIBLE){
            toggleMenu(true);
            Log.d(TAG, "hideReadMenu: toolbar");
            return true;
        }
        return false;
    }

    private void registerBrightObserver(){
        try {
            if(mBrightnessObserver!=null){
                if(!isRegistered){
                    final ContentResolver contentResolver=getContentResolver();
                    contentResolver.unregisterContentObserver(mBrightnessObserver);
                    contentResolver.registerContentObserver(BRIGHTNESS_URI,false,mBrightnessObserver);
                    isRegistered=true;
                }
            }
        } catch (Throwable e) {
            Log.e(TAG, "registerBrightObserver: error"+e);
        }
    }

    private void unregisterBrightObserver(){
        try {
            if(mBrightnessObserver!=null){
                if(isRegistered){
                    getContentResolver().unregisterContentObserver(mBrightnessObserver);
                    isRegistered=false;
                }
            }
        } catch (Throwable e) {
            Log.e(TAG, "unregisterBrightObserver: error"+e );
        }
    }


    private void processLogic(){
        if (isCollected) {
            Disposable disposable = BookRepository.getInstance()
                    .getBookChaptersInRx(mBookId)
                    .compose(RxUtils::toSimpleSingle)
                    .subscribe(
                            (bookChapters, throwable) -> {
                                // 设置 CollBook
                                mPageLoader.getCollectBook().setBookChapterList(bookChapters);
                                // 刷新章节列表
                                mPageLoader.refreshChapterList();

//                                // 如果是网络小说并被标记更新的，则从网络下载目录
////                                if (mCollectBook.isUpdate() && !mCollectBook.isLocal()) {
////                                    mPresenter.loadCategory(mBookId);
////                                }
                            }
                    );
            addDisposable(disposable);
        }
    }
    protected void addDisposable(Disposable d){
        if (mDisposable == null){
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(d);
    }

    public void showCatalog(List<BookChapterEntity> bookChapters) {
        mPageLoader.getCollectBook().setBookChapterList(bookChapters);
        mPageLoader.refreshChapterList();

        // 如果是目录更新的情况，那么就需要存储更新数据
        if (mCollectBook.isUpdate() && isCollected) {
            Disposable disposable=BookRepository.getInstance().saveBookChapterList(bookChapters)
                    .compose(RxUtils::toSimpleMaybe)
                    .subscribe(longList -> Log.d(TAG, "accept: save "+longList.size()+" books"));
            addDisposable(disposable);
        }
    }

    public void changeBgColor(){
        binding.setVariable(BR.bgColor,ContextCompat.getColor(ReadActivity.this,
                PreSettingsManager.getInstance().getPageStyle().getBgColor()));
        binding.executePendingBindings();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onStart() {
        super.onStart();
        registerBrightObserver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCollected) {
            mPageLoader.saveRecord();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterBrightObserver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (mDisposable != null){
            mDisposable.dispose();
        }
        mPageLoader.closeBook();
        mPageLoader = null;
    }

    public CollectBookEntity getCollectBook() {
        return mCollectBook;
    }

    public class Presenter{

        public Presenter() {
            Log.d(TAG, "Presenter: create");
        }

        public void finish(View view){
           ReadActivity.this.finish();
        }

        public void catalogClick(View view){
            if(mCatalogAdapter.getItemCount()>0){
                binding.chapterCatalog.scrollToPosition(mPageLoader.getChapterPos());
            }
            toggleMenu(true);
            binding.drawerlayout.openDrawer(Gravity.START);
        }

        public void nightModeClick(View view){
            isNightMode=!isNightMode;
            binding.setVariable(BR.isNightMode,isNightMode);
            mPageLoader.setNightMode(isNightMode);
        }

        public void preClick(View view){
            if (mPageLoader.skipPreChapter()) {
                mCatalogAdapter.setSelectedChapter(mPageLoader.getChapterPos());
            }
        }

        public void nextClick(View view){
            if (mPageLoader.skipNextChapter()) {
                mCatalogAdapter.setSelectedChapter(mPageLoader.getChapterPos());
            }
        }

        public void briefClick(View view){
//            BookDetailActivity.startActivity(this, mBookId);
        }

        public void communityClick(View view){
//            Intent intent = new Intent(this, CommunityActivity.class);
//            startActivity(intent);
        }

        public void dialogClick(View view){
            toggleMenu(false);
            mReadSettingDialogFragment.show(getSupportFragmentManager(),"settingDialog");
        }

        public PageView.TouchListener touchListener=new PageView.TouchListener() {
            @Override
            public boolean onTouch() {
                return !hideReadMenu();
            }

            @Override
            public void center() {
                toggleMenu(true);
            }

            @Override
            public void prePage() {
            }

            @Override
            public void nextPage(){
            }

            @Override
            public void cancel() {
            }
        };
    }
}
