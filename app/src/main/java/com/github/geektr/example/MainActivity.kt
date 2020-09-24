package com.github.geektr.example

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.geektr.example.databinding.ActivityMainBinding
import com.github.geektr.toast.ToastUtil
import com.github.geektr.toast.Utils
import com.github.geektr.toast.base.ActivitySupportToast
import com.github.geektr.toast.base.SystemToast
import com.github.geektr.toast.style.IToastStyle

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit  var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.btErrorToast.setOnClickListener(this)
        viewBinding.btTextToast.setOnClickListener(this)
        viewBinding.btWarningToast.setOnClickListener(this)
        viewBinding.btSuccessToast.setOnClickListener(this)
        viewBinding.btCustomLocation.setOnClickListener(this)
        viewBinding.btCustomFactory.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.bt_text_toast -> {
                ToastUtil.showTextToast("我是 text toast.")
            }
            R.id.bt_success_toast -> {
                ToastUtil.showSuccessToast("我是 success toast.")
            }
            R.id.bt_error_toast -> {
                ToastUtil.showErrorToast("我是 error toast.")
            }
            R.id.bt_warning_toast -> {
                ToastUtil.showWarningToast("我是 warning toast.")
            }
            R.id.bt_custom_location -> {
                // 这是全局配置，一般应该在初始化之后马上调用
                // 在activity中使用可能会造成内存泄露
                // 这里是为了演示用法
                ToastUtil.setToastSuccessStyle(object : IToastStyle {
                    override fun getGravity(): Int {
                        return Gravity.BOTTOM
                    }

                    override fun getYOffset(): Int {
                        return  0
                    }

                    override fun getXOffset(): Int {
                        return Utils.dpToPx(50f).toInt()
                    }
                })
                ToastUtil.showSuccessToast("我是自定义位置之后的Success Toast.")
            }
            R.id.bt_custom_factory -> {
                
                /**
                 * 自定义Toast的Factory
                 * 一般在Application中使用
                 * 设置之后全局生效
                 * 在activity中使用可能会造成内存泄露
                 * 此处为了使用是为了演示用法
                 */
                /**
                 * 自定义Toast的Factory
                 * 一般在Application中使用
                 * 设置之后全局生效
                 * 在activity中使用可能会造成内存泄露
                 * 此处为了使用是为了演示用法
                 */
                ToastUtil.setToastFactory { context ->
                    if (context is Activity) {
                        ActivitySupportToast(context as Activity?)
                    } else {
                        SystemToast(context)
                    }
                }
                ToastUtil.showTextToast("我是自定Toast的Factory，我只能在当前Activity显示")
            }
        }
    }
}