package com.laputa.arouter.login.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.laputa.arouter.service.LoginService
import com.laputa.arouter.service.getToken
import com.laputa.arouter.service.setToken

/**
 * Author by xpl, Date on 2021/4/19.
 */
@Route(path = "/login/LoginService", name = "impl01")
class LoginServiceImpl(override var token: String? = null) : LoginService {

    private var context: Context? = null
    override fun login(username: String, password: String, other: Any): String? {
        if (username == "LuBan" && password == "12345") {
            token = "[impl01]$username$password$other${System.currentTimeMillis()}".apply {
                context?.setToken(this)
            }

        }
        return token
    }

    override fun loginOut(): Boolean {
        token = ""
        context?.setToken("")
        return true
    }

    override fun isLogin(): Boolean = !context?.getToken().isNullOrEmpty()

    override fun init(context: Context?) {
        this.context = context?.applicationContext
    }
}