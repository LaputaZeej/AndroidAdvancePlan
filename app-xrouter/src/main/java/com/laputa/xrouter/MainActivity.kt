package com.laputa.xrouter

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.os.bundleOf
import com.laputa.common.entity.Cat
import com.laputa.common.view.RouteActivity
import com.laputa.xrouter.annotations.XField
import com.laputa.xrouter.annotations.XRoute
import com.laputa.xrouter.api.RouterManager
import com.laputa.xrouter.api.XRouter
import kotlinx.android.synthetic.main.activity_main.*

@XRoute(path = "/main/MainActivity")
class MainActivity : RouteActivity(R.layout.activity_main) {
    // 注意JvmField vs JvmStatic
    // @XField
    // lateinit var name: String

    @JvmField
    @XField
    var name: String? = null

    @JvmField
    @XField(name = "password")
    var pwd: String? = null

    @XField
    @JvmField
    var age: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val msg = "\nname = $name\npwd = $pwd\nage= $age"
        showInfo(msg)
        btn_login.setOnClickListener {
            XRouter.build("/login/LoginActivity")
                .with("name", "zeeeej")
                .with("password", "123456")
                .with("age", 18)
                .with("cat", Cat("LuBan", 18))
                .navigation(this)
        }

        btn_home.setOnClickListener {
            XRouter.build("/Home/HomeActivity")
                .with("name", "zeeeej")
                .with("password", "123456")
                .with("age", 18)
                .navigation(this)
        }
    }

    private fun showInfo(msg: String) {
        findViewById<TextView>(R.id.tv_info).append("$msg\n")
    }

    companion object {
        @JvmStatic
        fun navigation(activity: Activity, name: String, password: String, age: Int) {
            XRouter.build("/main/MainActivity")
                .with(bundleOf("name" to name, "password" to password, "age" to age))
                .navigation(activity)
        }
    }
}