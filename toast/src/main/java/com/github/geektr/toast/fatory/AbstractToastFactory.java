package com.github.geektr.toast.fatory;

import android.content.Context;

import com.github.geektr.toast.base.Toasty;


/**
 * 作者：唐瑞
 * 邮件：tangrui@baletu.com
 * 日期：2020/3/6
 */
public interface AbstractToastFactory {
    Toasty createToast(Context context);
}
