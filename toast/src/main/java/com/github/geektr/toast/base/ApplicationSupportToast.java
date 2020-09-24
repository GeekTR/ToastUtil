package com.github.geektr.toast.base;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * 作者：唐瑞
 * 邮件：tangrui@baletu.com
 * 日期：2020/3/6
 */
public class ApplicationSupportToast extends BaseSupportToast {
    private static final String TAG = "ApplicationSupportToast";

    public ApplicationSupportToast(Context context) {
        super(context);
    }

    /**
     * 取消Toast的显示
     */
    @Override
    public void cancel() {
        if (null != view && null != view.getParent()) {
            final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.removeView(view);
        }
    }

    /**
     * 显示Toast
     */
    @Override
    public void show() {
        showToast();
    }

    /**
     * 通过Application的WindowManager添加一个长的像Toast的View
     * tips：仅可在具有悬浮窗权限的情况下使用
     */
    private void showToast() {
        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        }
        layoutParams.windowAnimations = android.R.style.Animation_Toast;
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = this.gravity;
        layoutParams.x = xOffset;
        layoutParams.y = yOffset;

        if (view.getParent() != null) {
            windowManager.removeView(view);
        }

        try {
            windowManager.addView(view, layoutParams);
        } catch (WindowManager.BadTokenException ignored) {
            Log.e(TAG, "Try to show ApplicationSupportToast but permission denied.");
        }

        int durationTimeMills = this.duration == Toast.LENGTH_SHORT ? 2000 : 3500;
        weakHandler.sendEmptyMessageDelayed(MESSAGE_CANCEL, durationTimeMills);
    }
}
