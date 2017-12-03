package com.wizardev.headerrefreshforrecyclerview.interfaces;

import android.view.View;

/**
 * Created by wizardev on 17-12-3.
 */

public interface IRefreshHeader {
    int STATE_NORMAL = 0;//正常状态
    int STATE_RELEASE_TO_REFRESH = 1;//下拉的状态
    int STATE_REFRESHING = 2;//正在刷新的状态
    int STATE_DONE = 3;//刷新完成的状态

    void onReset();

    /**
     * 处于可以刷新的状态，已经过了指定距离
     */
    void onPrepare();

    /**
     * 正在刷新
     */
    void onRefreshing();

    /**
     * 下拉移动
     */
    void onMove(float offSet, float sumOffSet);

    /**
     * 下拉松开
     */
    boolean onRelease();

    /**
     * 下拉刷新完成
     */
    void refreshComplete();

    /**
     * 获取HeaderView
     */
    View getHeaderView();

    /**
     * 获取Header的显示高度
     */
    int getVisibleHeight();
}
