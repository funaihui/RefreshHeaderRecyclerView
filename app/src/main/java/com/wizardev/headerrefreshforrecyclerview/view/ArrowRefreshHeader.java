package com.wizardev.headerrefreshforrecyclerview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wizardev.headerrefreshforrecyclerview.R;
import com.wizardev.headerrefreshforrecyclerview.interfaces.IRefreshHeader;
import com.wizardev.headerrefreshforrecyclerview.interfaces.OnRefreshListener;

import org.w3c.dom.Text;

import java.util.Date;

/**
 * Created by wizardev on 17-12-3.
 */

public class ArrowRefreshHeader extends LinearLayout implements IRefreshHeader {

    private LinearLayout mContentLayout;
    private ImageView mArrowImageView;
    private TextView mStatusTextView;
    private ProgressBar mProgressBar;
    private RotateAnimation mRotateUpAnim;
    private RotateAnimation mRotateDownAnim;
    private int mMeasuredHeight;
    private int mState;


    public ArrowRefreshHeader(Context context) {
        this(context, null);
    }

    public ArrowRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();//初始化视图
    }

    private void init() {
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 0);
        this.setLayoutParams(layoutParams);
        this.setPadding(0, 0, 0, 0);

        //将refreshHeader高度设置为0
        mContentLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.refresh_header_item, null);
        addView(mContentLayout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));

        //初始化控件
        mArrowImageView = findViewById(R.id.ivHeaderArrow);
        mStatusTextView = findViewById(R.id.tvRefreshStatus);
        mProgressBar = findViewById(R.id.refreshProgress);

        //初始化动画
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(200);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(200);
        mRotateDownAnim.setFillAfter(true);

        //将mContentLayout的LayoutParams高度和宽度设为自动包裹并重新测量
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();//获得测量后的高度
    }

    @Override
    public void onReset() {
        setState(STATE_NORMAL);
    }

    @Override
    public void onPrepare() {
        setState(STATE_RELEASE_TO_REFRESH);
    }

    @Override
    public void onRefreshing() {
        setState(STATE_REFRESHING);
    }

    @Override
    public void onMove(float offSet, float sumOffSet) {
        if (getVisibleHeight() > 0 || offSet > 0) {
            setVisibleHeight((int) offSet + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                if (getVisibleHeight() > mMeasuredHeight) {
                    onPrepare();
                } else {
                    onReset();
                }
            }
        }
    }

    @Override
    public boolean onRelease() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) {// not visible.
            isOnRefresh = false;
        }

        if (getVisibleHeight() > mMeasuredHeight && mState < STATE_REFRESHING) {
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn't shown fully. do nothing.
        if (mState == STATE_REFRESHING && height > mMeasuredHeight) {
            smoothScrollTo(mMeasuredHeight);
        }
        if (mState != STATE_REFRESHING) {
            smoothScrollTo(0);
        }

        if (mState == STATE_REFRESHING) {
            int destHeight = mMeasuredHeight;
            smoothScrollTo(destHeight);
        }

        return isOnRefresh;
    }

    @Override
    public void refreshComplete() {
        setState(STATE_DONE);//设置刷新的状态为已完成
        reset();

        //延迟200ms后复位
        new Handler().postDelayed(new Runnable() {
            public void run() {
                reset();
            }
        }, 200);
    }

    public void reset() {
        smoothScrollTo(0);
        setState(STATE_NORMAL);
    }

    @Override
    public View getHeaderView() {
        return this;
    }

    @Override
    public int getVisibleHeight() {
        LayoutParams lp = (LayoutParams) mContentLayout.getLayoutParams();
        return lp.height;
    }

    /**
     * 刷新的状态
     *
     * @param state
     */
    public void setState(int state) {
        //状态没有改变时什么也不做
        if (state == mState) return;
        switch (state) {
            case STATE_NORMAL:
                if (mState == STATE_RELEASE_TO_REFRESH) {
                    mArrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING) {
                    mArrowImageView.clearAnimation();
                }
                mStatusTextView.setText("下拉刷新");
                break;
            case STATE_RELEASE_TO_REFRESH:
                mArrowImageView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                if (mState != STATE_RELEASE_TO_REFRESH) {
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mRotateUpAnim);
                    mStatusTextView.setText("释放立即刷新");
                }
                break;
            case STATE_REFRESHING:
                mArrowImageView.clearAnimation();
                mArrowImageView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                smoothScrollTo(mMeasuredHeight);
                mStatusTextView.setText("正在刷新...");

                break;
            case STATE_DONE:
                mArrowImageView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.INVISIBLE);
                mStatusTextView.setText("刷新完成");
                break;
            default:
        }
        mState = state;//保存当前刷新的状态
    }

    /**
     * 下拉之后，当正在刷新的时候，将位置从下拉到的位置恢复规定的位置的动画
     *
     * @param destHeight 规定的高度
     */
    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        Log.i("wizardev", "smoothScrollTo: " + getVisibleHeight());
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    /**
     * 设置刷新头部可见的高度
     *
     * @param height
     */
    public void setVisibleHeight(int height) {
        if (height < 0) height = 0;
        LayoutParams lp = (LayoutParams) mContentLayout.getLayoutParams();
        lp.height = height;
        mContentLayout.setLayoutParams(lp);
    }


}
