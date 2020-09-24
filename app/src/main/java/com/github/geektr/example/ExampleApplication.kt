package com.github.geektr.example

import android.app.Application
import com.github.geektr.toast.ToastUtil

class ExampleApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        ToastUtil.init(this)
    }
}