package com.github.geektr.toast.base;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;



/**
 * 作者：唐瑞
 * 邮件：tangrui@baletu.com
 * 日期：2020/3/6
 * ActivitySupportToast和ApplicationSupportToast的父类
 */
public abstract class BaseSupportToast implements Toasty {
    protected static final int MESSAGE_CANCEL = 0;

    protected Context context;
    protected Handler weakHandler;
    protected View view;
    protected int duration = Toast.LENGTH_SHORT;
    protected int gravity;
    protected int xOffset;
    protected int yOffset;

    public BaseSupportToast(Context context) {
        this.context = context.getApplicationContext();
        weakHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == MESSAGE_CANCEL) {
                    cancel();
                }
                return true;
            }
        });
    }

    @Override
    public void setView(final int viewResId) {
        setView(View.inflate(context, viewResId, null));
    }

    @Override
    public void setView(View view) {
        if (null == view || null == view.findViewById(android.R.id.message)) {
            throw new IllegalArgumentException("View is null or message view not exists.");
        }
        this.view = view;
    }

    @Override
    public View getView() {
        return this.view;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int getDuration() {
        return this.duration;
    }

    @Override
    public void setGravity(int gravity, int xOffset, int yOffset) {
        this.gravity = gravity;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public int getGravity() {
        return this.gravity;
    }

    @Override
    public int getXOffset() {
        return this.xOffset;
    }

    @Override
    public int getYOffset() {
        return this.yOffset;
    }

    @Override
    public void setText(@StringRes final int resId) {
        setText(context.getString(resId));
    }

    @Override
    public void setText(CharSequence s) {
        if (null == view) {
            throw new IllegalStateException("Please setView first.");
        }
        TextView tvMessage = view.findViewById(android.R.id.message);
        tvMessage.setText(s);
    }

    @Override
    public void cancelOnNextShow() {
        cancel();
    }
}
