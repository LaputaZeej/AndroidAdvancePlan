package com.laputa.arouter.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * Author by xpl, Date on 2021/4/22.
 */
@Route(path = "/login/refreshToken")
class SingleService : IProvider {
    override fun init(context: Context?) {
        println("check")
    }

    fun refreshToken(){
        println("refreshToken")
    }
}