package com.laputa.arouter.service

import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * Author by xpl, Date on 2021/4/19.
 */

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.getToken(): String {
    return getSharedPreferences("sp_token", Context.MODE_PRIVATE).getString("token", "") ?: ""
}

fun Context.setToken(token: String) {
    getSharedPreferences("sp_token", Context.MODE_PRIVATE).edit().putString("token", token).apply()
}

fun i(msg: String, tag: String = "Route-Demo") {
    Log.i(tag, msg)
}

fun tryCatching(block: () -> Unit, toast: ((String) -> Unit)? = null) {
    try {
        block.invoke()
    } catch (e: Throwable) {
        toast?.invoke(e.localizedMessage ?: "error")
    }
}

fun Context.tryCatching(block: () -> Unit) =
    tryCatching(block) {
        toast(it)
    }
