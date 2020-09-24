package com.github.geektr.toast;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Utils {
    private static DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();

    /**
     * dp转换为px
     * @param dpValue 待转换的dp值
     */
    public static float dpToPx(float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, displayMetrics);
    }
}
