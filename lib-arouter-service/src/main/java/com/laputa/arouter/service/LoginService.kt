package com.laputa.arouter.service

import com.alibaba.android.arouter.facade.template.IProvider

/**
 * Author by xpl, Date on 2021/4/19.
 */
interface LoginService : IProvider {
    var token: String?
    fun login(username: String, password: String, other: Any): String?
    fun loginOut(): Boolean
    fun isLogin():Boolean
}