package com.github.geektr.toast;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;

import com.github.geektr.toast.fatory.AbstractToastFactory;
import com.github.geektr.toast.fatory.DefaultToastFactory;
import com.github.geektr.toast.style.IToastStyle;
import com.github.geektr.toast.style.ToastTextStyle;
import com.github.geektr.toast.style.ToastWithIconStyle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者：唐瑞
 * 邮件：tangrui@baletu.com
 * 日期：2020/3/6
 */
public class ToastUtil {
    public final static int TOAST_TEXT = 0;
    public final static int TOAST_SUCCESS = 1;
    public final static int TOAST_ERROR = 2;
    public final static int TOAST_WANING = 3;

    private static ToastManager toastManager;

    private static IToastStyle toastTextStyle;
    private static IToastStyle toastSuccessStyle;
    private static IToastStyle toastWarningStyle;
    private static IToastStyle toastErrorStyle;

    @IntDef({TOAST_TEXT, TOAST_SUCCESS, TOAST_ERROR, TOAST_WANING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ToastStyle {
    }

    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    /**
     * 初始化Toast，请在Toast的onCreate中调用
     *
     * @param application 你懂的
     */
    public static void init(Application application) {
        toastManager = ToastManager.getInstance();
        toastManager.init(application);
        toastManager.setToastFactory(new DefaultToastFactory());

        toastTextStyle = new ToastTextStyle(application);
        toastSuccessStyle = new ToastWithIconStyle();
        toastWarningStyle = toastSuccessStyle;
        toastErrorStyle = toastSuccessStyle;
    }

    /**
     * 设置toast的创建工厂，可自定义Toast的显示逻辑
     *
     * @param toastFactory toast工厂
     */
    public static void setToastFactory(AbstractToastFactory toastFactory) {
        toastManager.setToastFactory(toastFactory);
    }

    /**
     * 显示警告类型的Toast
     *
     * @param message 消息
     */
    public static void showWarningToast(String message) {
        showWarningToast(message, Toast.LENGTH_SHORT);
    }

    /**
     * 显示警告类型的Toast
     *
     * @param message  消息
     * @param duration 时长 {@link Toast#LENGTH_SHORT}
     *                 {@link Toast#LENGTH_LONG}
     */
    public static void showWarningToast(String message, @Duration int duration) {
        showToast(message, duration, TOAST_WANING);
    }

    /**
     * 显示错误类型的Toast
     *
     * @param message 消息
     */
    public static void showErrorToast(String message) {
        showErrorToast(message, Toast.LENGTH_SHORT);
    }

    /**
     * 显示错误类型的Toast
     *
     * @param message  消息
     * @param duration 时长 {@link Toast#LENGTH_SHORT}
     *                 {@link Toast#LENGTH_LONG}
     */
    public static void showErrorToast(String message, @Duration int duration) {
        showToast(message, duration, TOAST_ERROR);
    }

    /**
     * 显示成功类型的Toast
     *
     * @param message 消息
     */
    public static void showSuccessToast(String message) {
        showSuccessToast(message, Toast.LENGTH_SHORT);
    }

    /**
     * 显示成功类型的Toast
     *
     * @param message  消息
     * @param duration 时长 {@link Toast#LENGTH_SHORT}
     *                 {@link Toast#LENGTH_LONG}
     */
    public static void showSuccessToast(String message, @Duration int duration) {
        showToast(message, duration, TOAST_SUCCESS);
    }

    /**
     * 显示文本类型的Toast
     *
     * @param message 消息
     */
    public static void showTextToast(String message) {
        showTextToast(message, Toast.LENGTH_SHORT);
    }

    /**
     * 显示文本类型的Toast
     *
     * @param message  消息
     * @param duration 时长 {@link Toast#LENGTH_SHORT}
     *                 {@link Toast#LENGTH_LONG}
     */
    public static void showTextToast(String message, @Duration int duration) {
        showToast(message, duration, TOAST_TEXT);
    }

    /**
     * 显示Toast
     *
     * @param message    消息
     * @param duration   时长 {@link Toast#LENGTH_SHORT}
     *                   {@link Toast#LENGTH_LONG}
     * @param toastStyle toast的风格 {@link ToastUtil#TOAST_TEXT}
     *                   {@link ToastUtil#TOAST_SUCCESS}
     *                   {@link ToastUtil#TOAST_ERROR}
     *                   {@link ToastUtil#TOAST_WANING}
     */
    public static void showToast(String message, @Duration int duration, @ToastStyle int toastStyle) {
        checkToastManage();

        IToastStyle iToastStyle = getIToastStyle(toastStyle);
        toastManager.createNext()
                .setView(getViewIdByStyle(toastStyle))
                .setGravity(iToastStyle.getGravity(), iToastStyle.getXOffset(), iToastStyle.getYOffset())
                .setDuration(duration)
                .setText(message)
                .show();
    }

    public static void setToastTextStyle(IToastStyle toastTextStyle) {
        ToastUtil.toastTextStyle = toastTextStyle;
    }

    public static void setToastSuccessStyle(IToastStyle toastSuccessStyle) {
        ToastUtil.toastSuccessStyle = toastSuccessStyle;
    }

    public static void setToastWarningStyle(IToastStyle toastWarningStyle) {
        ToastUtil.toastWarningStyle = toastWarningStyle;
    }

    public static void setToastErrorStyle(IToastStyle toastErrorStyle) {
        ToastUtil.toastErrorStyle = toastErrorStyle;
    }

    private static IToastStyle getIToastStyle(@ToastStyle int toastStyle) {
        IToastStyle iToastStyle;
        switch (toastStyle) {
            case TOAST_SUCCESS: {
                iToastStyle = toastSuccessStyle;
                break;
            }
            case TOAST_ERROR: {
                iToastStyle = toastErrorStyle;
                break;
            }
            case TOAST_WANING: {
                iToastStyle = toastWarningStyle;
                break;
            }
            case TOAST_TEXT:
            default: {
                iToastStyle = toastTextStyle;
                break;
            }
        }
        return iToastStyle;
    }

    @LayoutRes
    private static int getViewIdByStyle(@ToastStyle int toastStyle) {
        int viewId;
        switch (toastStyle) {
            case TOAST_SUCCESS: {
                viewId = R.layout.baseui_toast_success;
                break;
            }
            case TOAST_ERROR: {
                viewId = R.layout.baseui_toast_error;
                break;
            }
            case TOAST_WANING: {
                viewId = R.layout.baseui_toast_warning;
                break;
            }
            case TOAST_TEXT:
            default: {
                viewId = R.layout.baseui_toast_text;
                break;
            }
        }
        return viewId;
    }

    private static void checkToastManage() {
        if (null == toastManager) {
            throw new IllegalStateException("Please init first.");
        }
    }
}
