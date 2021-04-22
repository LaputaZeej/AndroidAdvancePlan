package com.laputa.arouter.service.impl

import android.os.Handler
import android.os.Looper
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback

/**
 * Author by xpl, Date on 2021/4/22.
 */
abstract class NavigationCallbackAdapter : NavigationCallback {

    override fun onFound(postcard: Postcard?) {

    }

    override fun onLost(postcard: Postcard?) {

    }

    override fun onArrival(postcard: Postcard?) {

    }

    override fun onInterrupt(postcard: Postcard?) {

    }




}