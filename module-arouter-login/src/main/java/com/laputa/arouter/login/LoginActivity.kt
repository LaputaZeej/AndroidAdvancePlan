package com.laputa.arouter.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.laputa.arouter.service.LoginService
import com.laputa.arouter.service.ParcelableBean
import com.laputa.arouter.service.exception.TokenException
import com.laputa.arouter.service.impl.NavigationCallbackAdapter
import com.laputa.arouter.service.toast
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
        showInfo("[ LoginActivity ] \n")
        val msg = "name = $name\npassword = $password\nloginService = $loginService\na=$a"
        showInfo(msg)

        btn_01.apply {
            text = "去Home"
            setOnClickListener {
                ARouter.getInstance().build("/home/HomeActivity")
                    .with(bundleOf("name" to "LuBan", "pwd" to "12345", "auto" to false))
                    .navigation(this@LoginActivity, object : NavigationCallbackAdapter() {
                        override fun onInterrupt(postcard: Postcard?) {
                            super.onInterrupt(postcard)
                            runOnUiThread {
                                if (postcard?.tag == TokenException().message) {
                                    AlertDialog.Builder(this@LoginActivity).setTitle("tips")
                                        .setMessage("请登录")
                                        .setNegativeButton("返回") { _, _ -> }
                                        .setPositiveButton("登录") { dialog, _ ->
                                            dialog.dismiss()
                                            ARouter.getInstance()
                                                .build("/login/LoginActivity")
                                                .navigation()
                                        }
                                        .create().show()
                                }
                            }
                        }
                    })
            }
        }
        btn_02.apply {
            text = "登录"
            setOnClickListener {
                val login = ARouter.getInstance().navigation(LoginService::class.java)
                    .login("LuBan", "12345", "")
                if (!login.isNullOrEmpty()) {
                    toast("登录成功！")
                    finish()
                } else {
                    toast("登录失败！")
                }
            }
        }
    }

    private fun showInfo(msg: String) {
        tv_info.append("\n$msg")
    }
}