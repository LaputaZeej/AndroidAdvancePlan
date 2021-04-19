package com.laputa.xrouter.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.laputa.xrouter.annotations.XField
import com.laputa.xrouter.annotations.XRoute
import com.laputa.xrouter.api.XRouter
import kotlinx.android.synthetic.main.activity_home.*

@XRoute(path = "/Home/HomeActivity")
class HomeActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_home)
        XRouter.load(this)
        val msg = "\nname = $name\npwd = $pwd\nage= $age"
        showInfo(msg)

        btn_01.text = "Main"
        btn_01.setOnClickListener {
            XRouter.build("/main/MainActivity")
                .with("name", "from home")
                .with("password", "123456 from home")
                .with("age", 18)
                .navigation(this)
        }


        btn_02.text = "登录"
        btn_02.setOnClickListener {
            XRouter.build("/login/LoginActivity")
                .with("name", "zeeeeeej from home")
                .with("password", "123456 from home")
                .with("age", 18)
                .navigation(this)
        }
    }

    private fun showInfo(msg: String) {
        findViewById<TextView>(R.id.tv_info).append("$msg\n")
    }
}