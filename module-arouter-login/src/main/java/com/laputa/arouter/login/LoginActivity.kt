package com.laputa.arouter.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.laputa.arouter.service.LoginService
import com.laputa.arouter.service.ParcelableBean
import kotlinx.android.synthetic.main.activity_login.*

@Route(path = "/login/LoginActivity")
class LoginActivity : AppCompatActivity() {
    @Autowired
    @JvmField
    var name: String? = null

    @Autowired(name = "pwd")
    @JvmField
    var password: String? = null

    @Autowired
    @JvmField
    var loginService: LoginService? = null

    @Autowired(name = "parcelableBean")
    @JvmField
    var a: ParcelableBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ARouter.getInstance().inject(this)
        showInfo("[ HomeActivity ]")
        val msg = "name = $name\npassword = $password\nloginService = $loginService\na=$a"
        showInfo(msg)

        btn_01.apply {
            text = "登录"
            setOnClickListener {
                ARouter.getInstance().build("/login/LoginActivity")
                    .with(bundleOf("name" to "LuBan", "pwd" to "12345"))
                    .navigation()
            }
        }
        btn_02.apply {
            text = "Home"
            setOnClickListener {
                ARouter.getInstance().build("/home/HomeActivity")
                    .with(bundleOf("name" to "LuBan", "pwd" to "12345"))
                    .navigation()
            }
        }
    }

    private fun showInfo(msg: String) {
        tv_info.append("\n$msg")
    }
}