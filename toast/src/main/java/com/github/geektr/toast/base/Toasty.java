package com.github.geektr.toast.base;

import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;

/**
 * 作者：唐瑞
 * 邮件：tangrui@baletu.com
 * 日期：2020/3/6
 */
public interface Toasty {
    void setView(@LayoutRes int viewResId);

    void setView(View view);

    View getView();

    void setDuration(int duration);

    int getDuration();

    void setGravity(int gravity, int xOffset, int yOffset);

    int getGravity();

    int getXOffset();

    int getYOffset();

    void setText(@StringRes int resId);

    void setText(CharSequence s);

    void cancel();

    /**
     * 下一个toast显示的的时候调用
     * 由Toast实际情况判断是否需要调用cancel
     * 系统Toast不调用cancel
     */
    void cancelOnNextShow();

    void show();
}
