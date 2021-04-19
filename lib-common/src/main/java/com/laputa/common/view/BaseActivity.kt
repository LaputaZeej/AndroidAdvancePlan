package com.laputa.common.view

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.laputa.xrouter.api.XRouter

/**
 * Author by xpl, Date on 2021/4/19.
 */
abstract class BaseActivity(open val layoutResId: Int) : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)
    }

}

abstract class RouteActivity(layoutResId: Int, private val route: Boolean = true) :
    BaseActivity(layoutResId) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (route) {
            XRouter.load(this)
        }
    }
}