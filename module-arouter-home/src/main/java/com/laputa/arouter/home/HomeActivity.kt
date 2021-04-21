package com.laputa.arouter.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.laputa.arouter.service.LoginService
import kotlinx.android.synthetic.main.activity_home.*

@Route(path = "/home/HomeActivity")
class HomeActivity : AppCompatActivity() {

    @Autowired
    @JvmField
    var name: String? = null

    @Autowired(name = "pwd")
    @JvmField
    var password: String? = null

    @Autowired(name = "/login/LoginService")
    @JvmField
    var loginService: LoginService? = null

    @Autowired(name = "/main/LoginService")
    lateinit var loginServiceImpl02: LoginService

    @Autowired(name = "/login/LoginService")
    lateinit var loginServiceImpl03: LoginService

    @Autowired(name = "/login/LoginService")
    @JvmField
    var loginServiceImpl04: LoginService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        ARouter.getInstance().inject(this)
        showInfo("[ HomeActivity ]")
        val msg = "name = $name\npassword = $password" +
                "\nloginService = $loginService " +
                "\nloginServiceImpl02=$loginServiceImpl02" +
                "\nloginServiceImpl03=$loginServiceImpl03" +
                "\nloginServiceImpl04=$loginServiceImpl04" +
                "-------"
        showInfo(msg)

        btn_01.apply {
            text = "调用Login服务"
            setOnClickListener {
                val token = loginService?.login(name ?: "", password ?: "", "")
                showInfo(token ?: "登录失败")
            }
        }
        btn_02.apply {
            text = "调用Login服务Impl 02"
            setOnClickListener {
                val login = loginServiceImpl02.login(name ?: "", password ?: "", "")
                showInfo(login ?: "登录失败")
            }
        }

        btn_03.apply {
            text = "调用Login服务Impl 03"
            setOnClickListener {
                val login = loginServiceImpl03.login(name ?: "", password ?: "", "")
                showInfo(login ?: "登录失败")
            }
        }

        btn_04.apply {
            text = "调用Login服务Impl 04"
            setOnClickListener {
                val login = loginServiceImpl04?.login(name ?: "", password ?: "", "")
                showInfo(login ?: "登录失败")
            }
        }
    }

    private fun showInfo(msg: String) {
        tv_info.append("\n$msg")
    }
}