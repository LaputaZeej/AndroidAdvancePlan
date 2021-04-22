package com.laputa.arouter.service.impl

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.service.DegradeService

/**
 * Author by xpl, Date on 2021/4/22.
 */

@Route(path = "/login/DegradeService")
class DegradeServiceImpl : DegradeService {
    override fun init(context: Context?) {
    }

    override fun onLost(context: Context?, postcard: Postcard?) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context!!, "找不到${postcard?.path}降级处理", Toast.LENGTH_SHORT).show()
        }
    }
}