package com.laputa.arouter.login.service

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.alibaba.android.arouter.launcher.ARouter
import com.laputa.arouter.service.exception.TokenException
import com.laputa.arouter.service.getToken

/**
 * Author by xpl, Date on 2021/4/22.
 */
@Interceptor(priority = 1)
class LoginInterceptor : IInterceptor {
    private var context: Context? = null
    private val handler = Handler(Looper.getMainLooper())

    private var auto: Boolean = true

    override fun init(context: Context?) {
        this.context = context

    }

    override fun process(postcard: Postcard?, callback: InterceptorCallback) {
        if (postcard != null
            && !postcard.path.startsWith("/login/LoginActivity")
            && context?.getToken().isNullOrEmpty()
        ) {
            callback.onInterrupt(TokenException())
            // todo 不能dialog
            auto = if (postcard.extras.containsKey("auto")) {
                postcard.extras.getBoolean("auto")
            } else {
                true
            }
            if (auto) {
                handler.post {
                    /*AlertDialog.Builder(context!!).setTitle("tips")
                        .setMessage("请登录")
                        .setPositiveButton("") { dialog, _ ->
                            dialog.dismiss()
                            ARouter.getInstance()
                                .build("/login/LoginActivity")
                                .navigation()
                        }
                        .create().show()*/
                    // todo 保存原来的信息，登录成功后进入。
                    ARouter.getInstance().build("/login/LoginActivity").navigation()
                }
            }
        } else {
            callback.onContinue(postcard)
        }
//        if (postcard?.path?.startsWith("/home/") == true) {
    }
}