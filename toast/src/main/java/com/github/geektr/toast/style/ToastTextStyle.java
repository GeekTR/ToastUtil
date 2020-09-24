package com.github.geektr.toast.style;

import android.content.Context;
import android.view.Gravity;

import com.github.geektr.toast.Utils;


/**
 * 作者：唐瑞
 * 邮件：tangrui@baletu.com
 * 日期：2020/3/6
 */
public class ToastTextStyle implements IToastStyle {
    private Context context;

    public ToastTextStyle(Context context) {
        this.context = context;
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
    }

    @Override
    public int getXOffset() {
        return 0;
    }

    @Override
    public int getYOffset() {
        return (int) Utils.dpToPx(50);
    }
}
