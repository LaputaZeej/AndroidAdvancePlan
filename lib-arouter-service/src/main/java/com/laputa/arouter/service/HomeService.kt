package com.laputa.arouter.service

import com.alibaba.android.arouter.facade.template.IProvider

/**
 * Author by xpl, Date on 2021/4/19.
 */
interface HomeService : IProvider {

    fun info(id: String): String

    fun edit(id: String, value: String): Boolean
}