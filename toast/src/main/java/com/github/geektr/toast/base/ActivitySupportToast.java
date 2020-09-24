package com.github.geektr.toast.base;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * 作者：唐瑞
 * 邮件：tangrui@baletu.com
 * 日期：2020/3/6
 * 描述：只能在Activity生命周期内显示的'假'Toast
 */
public class ActivitySupportToast extends BaseSupportToast {
    private WeakReference<Activity> activityRef;

    public ActivitySupportToast(Activity activity) {
        super(activity);
        this.activityRef = new WeakReference<>(activity);
    }

    /**
     * 取消toast显示
     */
    @Override
    public void cancel() {
        weakHandler.removeMessages(MESSAGE_CANCEL);
        if (null != view && view.getParent() != null) {
            Activity activity = activityRef.get();
            removeViewFromWindow(activity);
        }
    }

    private void removeViewFromWindow(Activity activity) {
        if (null != activity) {
            if (isActivityFinishing(activity)) return;
            activity.getWindowManager()
                    .removeView(view);
        }
    }

    /**
     * 显示Toast
     */
    @Override
    public void show() {
        Activity activity = activityRef.get();
        if (null != activity) {
            showToast(activity);
        }
    }

    /**
     * 通过Activity的WindowManager添加一个长的像Toast的View
     *
     * @param activity
     */
    private void showToast(Activity activity) {
        if (isActivityFinishing(activity)) return;

        final WindowManager windowManager = activity.getWindowManager();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.windowAnimations = android.R.style.Animation_Toast;
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = this.gravity;
        layoutParams.x = xOffset;
        layoutParams.y = yOffset;

        if (view.getParent() != null) {
            windowManager.removeView(view);
        }
        windowManager.addView(view, layoutParams);

        int durationTimeMills = this.duration == Toast.LENGTH_SHORT ? 2000 : 3500;
        weakHandler.sendEmptyMessageDelayed(MESSAGE_CANCEL, durationTimeMills);
    }

    private boolean isActivityFinishing(Activity activity) {
        return activity.isFinishing()
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed());
    }
}
