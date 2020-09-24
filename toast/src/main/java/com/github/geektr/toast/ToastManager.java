package com.github.geektr.toast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;


import com.github.geektr.toast.base.Toasty;
import com.github.geektr.toast.fatory.AbstractToastFactory;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 作者：唐瑞
 * 邮件：tangrui@baletu.com
 * 日期：2020/3/6
 */
final class ToastManager {

    private boolean initialized = false;
    private Application application;
    private WeakReference<Activity> topActivityReference = new WeakReference<>(null);

    private Toasty lastToast;
    private Toasty nextToast;
    private AbstractToastFactory toastFactory;

    private ToastManager() {
    }

    void setToastFactory(AbstractToastFactory factory) {
        this.toastFactory = factory;
    }

    private static class InstanceHolder {
        private static ToastManager instance = new ToastManager();
    }

    static ToastManager getInstance() {
        return InstanceHolder.instance;
    }

    void init(Application application) {
        if (initialized) {
            return;
//            throw new IllegalStateException("Don't initialize twice!");
        }

        this.application = application;
        listenTopActivity(application);
        hookToast();

        initialized = true;
    }

    ToastManager createNext() {
        if (null == toastFactory) {
            throw new IllegalStateException("Please set toast factory first.");
        }

        if (nextToast == null) {
            Context context = topActivityReference.get();
            if (null == context) {
                context = application;
            }
            nextToast = toastFactory.createToast(context);
        }
        return this;
    }

    /**
     * 自定义Toast显示的位置
     */
    ToastManager setGravity(int gravity, int xOffset, int yOffset) {
        checkNextToast();
        nextToast.setGravity(gravity, xOffset, yOffset);
        return this;
    }

    /**
     * 自定义Toast显示的View
     */
    ToastManager setView(@LayoutRes int viewId) {
        checkNextToast();

        setView(View.inflate(application, viewId, null));
        return this;
    }

    /**
     * 设置Toast的显示时长
     */
    ToastManager setDuration(int duration) {
        checkNextToast();

        nextToast.setDuration(duration);
        return this;
    }

    /**
     * 自定义Toast显示的View
     */
    ToastManager setView(View view) {
        checkNextToast();

        if (null == view || view.findViewById(android.R.id.message) == null) {
            throw new IllegalArgumentException("View is null or message view is not exists.");
        }
        nextToast.setView(view);
        return this;
    }

    /**
     * 设置Toast显示的文字
     */
    ToastManager setText(CharSequence text) {
        checkNextToast();
        nextToast.setText(text);
        return this;
    }

    /**
     * 设置Toast显示的文字
     * @param textResId
     * @return
     */
    ToastManager setText(@StringRes int textResId) {
        checkNextToast();
        nextToast.setText(textResId);
        return this;
    }

    /**
     * 显示Toast
     */
    void show() {
        if (lastToast != null) {
            lastToast.cancelOnNextShow();
            lastToast = null;
        }

        if (null != nextToast) {
            nextToast.show();

            lastToast = nextToast;
            nextToast = null;
        }
    }

    void cancel() {
        if (lastToast != null) {
            lastToast.cancel();
            lastToast = null;
        }
    }

    private void checkNextToast() {
        if (null == nextToast) {
            throw new IllegalStateException("Please create next first.");
        }
    }

    @SuppressLint({"DiscouragedPrivateApi", "PrivateApi"})
    private void hookToast() {
        // 安卓10此hack方式失效
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            return;
        }

        // 通过反射对INotificationManager进行动态代理
        // 使得enqueueToast方法第一次参数总是 'android'
        // 以此来冒充系统Toast,在我们无通知栏权限时也能显示Toast
        // 此方式在Android10失效
        try {
            Method getServiceMethod = Toast.class.getDeclaredMethod("getService");
            getServiceMethod.setAccessible(true);

            final Object iNotificationManager = getServiceMethod.invoke(null);
            Class iNotificationManagerCls = Class.forName("android.app.INotificationManager");
            Object iNotificationManagerProxy = Proxy.newProxyInstance(Toast.class.getClassLoader(), new Class[]{iNotificationManagerCls}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    // 强制使用系统Toast
                    // 华为p20 pro上为enqueueToastEx
                    if ("enqueueToast".equals(method.getName())
                            || "enqueueToastEx".equals(method.getName())) {
                        args[0] = "android";
                    }
                    return method.invoke(iNotificationManager, args);
                }
            });
            Field sServiceFiled = Toast.class.getDeclaredField("sService");
            sServiceFiled.setAccessible(true);
            sServiceFiled.set(null, iNotificationManagerProxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听Activity生命周期的回掉，保存最顶部的Activity的引用
     * 方便在ActivitySupportToast显示的时候使用
     * @param application
     */
    private void listenTopActivity(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                topActivityReference = new WeakReference<>(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (activity != topActivityReference.get()) {
                    topActivityReference = new WeakReference<>(activity);
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {
                if (activity != topActivityReference.get()) {
                    topActivityReference = new WeakReference<>(activity);
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }
}
