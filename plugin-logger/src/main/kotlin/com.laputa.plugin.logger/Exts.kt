package com.laputa.plugin.logger

/**
 * Author by xpl, Date on 2021/4/21.
 */
const val TAG = "mmmmmmmmmmmmmmmmmmmmmmm"

fun line(msg: String, prefix: String = "") {
    println("$prefix$TAG $msg $TAG")
}

fun line1(msg: String) {
    line(msg, "->")
}

fun tag(size: Int) = TAG.run {
    var r = this
    repeat(size) {
        r += TAG
    }
    r
}

// https://zhuanlan.zhihu.com/p/94498015?utm_source=wechat_timeline