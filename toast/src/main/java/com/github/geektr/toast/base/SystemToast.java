package com.github.geektr.toast.base;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.LayoutRes;

import java.lang.reflect.Field;

/**
 * 作者：唐瑞
 * 邮件：tangrui@baletu.com
 * 日期：2020/3/6
 * 描述：系统的Toast，使用装饰者模式包装调用系统Toast实现
 */
public class SystemToast implements Toasty {
    private Context context;
    private Toast toast;

    public SystemToast(Context context) {
        this.context = context.getApplicationContext();
        toast = new Toast(this.context);

        // 修复Android7.1Toast的bug
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
            hook(toast);
        }
    }

    @Override
    public void setView(@LayoutRes int viewResId) {
        setView(View.inflate(context, viewResId, null));
    }

    @Override
    public void setView(View view) {
        toast.setView(view);
    }

    @Override
    public View getView() {
        return toast.getView();
    }

    @Override
    public void setDuration(int duration) {
        toast.setDuration(duration);
    }

    @Override
    public int getDuration() {
        return toast.getDuration();
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        toast.setGravity(gravity, xOffset, yOffset);
    }


    @Override
    public int getGravity() {
        return toast.getGravity();
    }

    @Override
    public int getXOffset() {
        return toast.getXOffset();
    }

    @Override
    public int getYOffset() {
        return toast.getYOffset();
    }

    @Override
    public void setText(int resId) {
        toast.setText(resId);
    }

    @Override
    public void setText(CharSequence s) {
        toast.setText(s);
    }

    @Override
    public void cancel() {
        toast.cancel();
    }

    @Override
    public void cancelOnNextShow() {
    }

    @Override
    public void show() {
        toast.show();
    }

    private void hook(Toast toast) {
        // 反射 Toast 中的字段
        try {
            // 获取 mTN 字段对象
            Field mTNField = toast.getClass().getDeclaredField("mTN");
            mTNField.setAccessible(true);
            Object mTN = mTNField.get(this);

            // 获取 mTN 中的 mHandler 字段对象
            Field mHandlerField = mTNField.getType().getDeclaredField("mHandler");
            mHandlerField.setAccessible(true);
            final Handler mHandler = (Handler) mHandlerField.get(mTN);
            // 偷梁换柱
            if (!(mHandler instanceof SafeHandler)) {
                mHandlerField.set(mTN, new SafeHandler(mHandler));
            }
        } catch (Exception ignored) {
        }
    }

    private static final class SafeHandler extends Handler {

        private Handler mHandler;

        private SafeHandler(Handler handler) {
            mHandler = handler;
        }

        @Override
        public void handleMessage(Message msg) {
            // 捕获这个异常，避免程序崩溃
            try {
                /*
                 目前发现在 Android 7.1 主线程被阻塞之后弹吐司会导致崩溃，可使用 Thread.sleep(5000) 进行复现
                 查看源码得知 Google 已经在 Android 8.0 已经修复了此问题
                 主线程阻塞之后 Toast 也会被阻塞，Toast 因为超时导致 Window Token 失效
                 */
                mHandler.handleMessage(msg);
            } catch (WindowManager.BadTokenException ignored) {
                // android.view.WindowManager$BadTokenException:
                // Unable to add window -- token android.os.BinderProxy is not valid; is your activity running?
            }
        }
    }
}
