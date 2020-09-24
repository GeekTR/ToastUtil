package com.github.geektr.toast.fatory;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.NotificationManagerCompat;

import com.github.geektr.toast.base.ActivitySupportToast;
import com.github.geektr.toast.base.ApplicationSupportToast;
import com.github.geektr.toast.base.SystemToast;
import com.github.geektr.toast.base.Toasty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;

/**
 * 作者：唐瑞
 * 邮件：tangrui@baletu.com
 * 日期：2020/3/6
 */
public class DefaultToastFactory implements AbstractToastFactory {
    private Boolean isMiui = null;

    @Override
    public Toasty createToast(Context context) {
        if (canDrawOverlays(context)) {
            return new ApplicationSupportToast(context);
        }

        if (isMiui == null) {
            isMiui = isMiui();
        }

        /**
         * miui做了适配，没有通知权限也可以显示系统Toast
         * 安卓10以前使用hack方式，可以在没有通知权限的时候使用系统Toast
         * 有通知权限可以直接显示系统Toast
         */
        if (isMiui
                || Build.VERSION.SDK_INT <= Build.VERSION_CODES.P
                || hasNotificationPermission(context)) {
            return new SystemToast(context);
        }

        // 为了能保证Toast显示出来，使用Activity的WindowManager创建一个Toast
        if (context instanceof Activity) {
            return new ActivitySupportToast((Activity) context);
        }

        // 走到这儿，Toast多半显示不出来
        return new SystemToast(context);
    }

    /**
     * 判断是否有悬浮窗权限
     *
     * @param context 上下文
     * @return 是否有悬浮窗权限
     */
    private boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Object appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsManager == null) {
                    return false;
                }

                Class<?> localClass = appOpsManager.getClass();
                Class[] arrayOfClass = new Class[3];
                arrayOfClass[0] = Integer.TYPE;
                arrayOfClass[1] = Integer.TYPE;
                arrayOfClass[2] = String.class;
                Method method = localClass.getDeclaredMethod("checkOp", arrayOfClass);
                method.setAccessible(true);
                Object[] arrayOfObject1 = new Object[3];
                arrayOfObject1[0] = 24;
                arrayOfObject1[1] = Binder.getCallingUid();
                arrayOfObject1[2] = context.getPackageName();
                int m = (Integer) method.invoke(appOpsManager, arrayOfObject1);
                return m == AppOpsManager.MODE_ALLOWED;
            } catch (Exception ignored) {
            }
        } else {
            return true;
        }

        return false;
    }

    /**
     * 判断用户是否具备通知栏权限
     *
     * @param context 上下文
     * @return 是否具备通知栏权限
     */
    private static boolean hasNotificationPermission(Context context) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        return manager.areNotificationsEnabled();
    }

    private static boolean isMiui() {
        String prop = getMiuiProp();
        return TextUtils.isEmpty(prop);
    }

    private static String getMiuiProp() {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name");
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }
}
