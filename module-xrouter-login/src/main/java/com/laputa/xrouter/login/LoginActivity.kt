package com.laputa.xrouter.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.laputa.common.entity.Cat
import com.laputa.xrouter.annotations.XField
import com.laputa.xrouter.annotations.XRoute
import com.laputa.xrouter.api.XRouter
import kotlinx.android.synthetic.main.activity_login.*

@XRoute(path = "/login/LoginActivity")
class LoginActivity : AppCompatActivity() {

    @JvmField
    @XField
    var name: String? = null

    @JvmField
    @XField(name = "password")
    var pwd: String? = null

    @XField
    @JvmField
    var age: Int = 0

    @XField
    @JvmField
    var cat: Cat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        XRouter.load(this)
        val msg = "\nname = $name\npwd = $pwd\nage= $age\ncat=${cat}"
        showInfo(msg)

        btn_01.text = "Main"
        btn_01.setOnClickListener {
            XRouter.build("/main/MainActivity")
                .with("name", "from login")
                .with("password", "123456 from login")
                .with("age", 18)
                .navigation(this)
        }
        
        btn_02.text = "test"
        btn_02.setOnClickListener {
            XRouter.build("/app/TestActivity")
                .with("name", "zeeeeeej from login")
                .with("password", "123456 from login")
                .with("age", 18)
                .navigation(this)
        }
    }

    private fun showInfo(msg: String) {
        findViewById<TextView>(R.id.tv_info).append("$msg\n")
    }
}