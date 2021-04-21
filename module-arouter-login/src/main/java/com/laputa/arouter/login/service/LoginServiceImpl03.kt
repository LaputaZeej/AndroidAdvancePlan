package com.laputa.arouter.login.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.laputa.arouter.service.LoginService

/**
 * Author by xpl, Date on 2021/4/19.
 */
@Route(path = "/login/LoginService", name = "impl03")
class LoginServiceImpl03(override var token: String? = null) : LoginService {

    private var context: Context? = null
    override fun login(username: String, password: String, other: Any): String? {
        if (username == "LuBan" && password == "12345") {
            token = "[impl03]$username$password$other${System.currentTimeMillis()}"
        }
        return token
    }

    override fun loginOut(): Boolean {
        token = ""
        return true
    }

    override fun isLogin(): Boolean = !token.isNullOrEmpty()

    override fun init(context: Context?) {
        this.context = context?.applicationContext
    }
}