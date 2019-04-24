package com.nigel.wenreader.ui.fragment;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.nigel.wenreader.R;
import com.nigel.wenreader.databinding.DialogFragmentReadBinding;
import com.nigel.wenreader.model.local.PreSettingsManager;
import com.nigel.wenreader.ui.activity.ReadActivity;
import com.nigel.wenreader.utils.BrightnessUtils;
import com.nigel.wenreader.utils.ScreenUtils;
import com.nigel.wenreader.viewmodel.DialogSettingViewModel;
import com.nigel.wenreader.widget.page.PageLoader;
import com.nigel.wenreader.widget.page.PageStyle;

public class ReadSettingDialogFragment extends DialogFragment {
    private static final String TAG = "ReadSettingDialogFragme";
    private DialogFragmentReadBinding mBinding;
    private ReadActivity mActivity;
    private PageLoader mPageLoader;
    private DialogSettingViewModel mViewModel;
    private PreSettingsManager mSettingsManager;
    private static final int DEFAULT_TEXT_SIZE = 20;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingsManager=PreSettingsManager.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding=DataBindingUtil.inflate(inflater,R.layout.dialog_fragment_read,container,false);
        mBinding.setLifecycleOwner(this);
        mActivity=(ReadActivity) getActivity();
        mPageLoader=(PageLoader) getArguments().get("pageLoader");
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel= ViewModelProviders.of(this)
                .get(DialogSettingViewModel.class);
        mBinding.setViewModel(mViewModel);
        mBinding.setPresenter(new SettingPresenter());
        subscribeUi();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window=getDialog().getWindow();
        //必须设置这个，下面的属性才有用
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        WindowManager.LayoutParams lp=window.getAttributes();
        lp.width=WindowManager.LayoutParams.MATCH_PARENT;
        lp.height=WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.dimAmount=0f;
        lp.windowAnimations=R.style.BottomDialogAnimation;
        window.setAttributes(lp);
    }

    private void subscribeUi(){
        mViewModel.getIsBrightnessAuto().observe(this, aBoolean -> {
            if (aBoolean){
                BrightnessUtils.setScreenBrightness(mActivity, BrightnessUtils.getScreenBrightness(mActivity));
            }else {
                BrightnessUtils.setScreenBrightness(mActivity, mViewModel.getBrightness().getValue());
            }
            mSettingsManager.setAutoBrightness(aBoolean);
        });

        mViewModel.getIsTextDefault().observe(this,aBoolean -> {
            if(aBoolean){
                int fontSize = ScreenUtils.dpToPx(DEFAULT_TEXT_SIZE);
                mViewModel.getTextSize().setValue(fontSize);
            }
        });

        mViewModel.getTextSize().observe(this,size->{
            mPageLoader.setTextSize(size);
        });

        mViewModel.getPageMode().observe(this,pageMode ->{
            mPageLoader.setPageMode(pageMode);
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        ReadActivity activity=(ReadActivity)getActivity();
        if (activity!=null)
            activity.hideSystemBar();
        mActivity.changeBgColor();
        Log.d(TAG, "onDismiss: dismiss");
    }

    public boolean isBrightFollowSystem() {
        return mViewModel.getIsBrightnessAuto().getValue();
    }

    public class SettingPresenter{
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            if (mViewModel.getIsBrightnessAuto().getValue()&&fromUser) {
                mViewModel.getIsBrightnessAuto().setValue(false);
            }
            //设置当前 Activity 的亮度
            BrightnessUtils.setScreenBrightness(mActivity, progress);
            //存储亮度的进度条
            mSettingsManager.setBrightness(progress);
        }

        public void fontMinusClick(View view){
            if(mViewModel.getIsTextDefault().getValue()){
                mViewModel.getIsTextDefault().setValue(false);
            }
            if(mViewModel.getTextSize().getValue()>0){
                mViewModel.getTextSize().setValue(mViewModel.getTextSize().getValue()-1);
            }
        }

        public void fontPlusClick(View view){
            if(mViewModel.getIsTextDefault().getValue()){
                mViewModel.getIsTextDefault().setValue(false);
            }
            mViewModel.getTextSize().setValue(mViewModel.getTextSize().getValue()+1);
        }

        public void onBgItemClick(int position){
            mPageLoader.setPageStyle(PageStyle.values()[position]);
            mViewModel.getPageStyle().setValue(PageStyle.values()[position]);
            Log.d(TAG, "onBgItemClick: change");
        }
    }
}
