package com.laputa.arouter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.laputa.arouter.service.LoginService
import com.laputa.arouter.service.ParcelableBean
import com.laputa.arouter.service.exception.TokenException
import com.laputa.arouter.service.getToken
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    @Autowired
    @JvmField
    var name: String? = null

    @Autowired(name = "pwd")
    @JvmField
    var password: String? = null

    @Autowired
    @JvmField
    var loginService: LoginService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initARouter()
        ARouter.getInstance().inject(this)
        showInfo("[ HomeActivity ]")
        val msg = "当前token ${getToken()}\n" +
                "name = $name\npassword = $password\nloginService = $loginService"
        showInfo(msg)
        btn_03.apply {
            text = "注册"
            setOnClickListener {
                initARouter()
            }
        }

        btn_04.apply {
            text = "注销"
            setOnClickListener {
                ARouter.getInstance().destroy()
            }
        }

        btn_01.apply {
            text = "去登录页面"
            setOnClickListener {
                ARouter.getInstance().build("/login/LoginActivity")
                    .with(
                        bundleOf(
                            "name" to "LuBan",
                            "pwd" to "12345",
                            "parcelableBean" to ParcelableBean("haha", 18)
                        )
                    )
                    .navigation()
            }
        }

        btn_02.apply {
            text = "去Home页面-弹窗提示"
            setOnClickListener {
                ARouter.getInstance().build("/home/HomeActivity")
                    .with(bundleOf("name" to "LuBan", "pwd" to "12345", "auto" to false))
                    .navigation(this@MainActivity, object : NavigationCallback {
                        override fun onFound(postcard: Postcard?) {
                        }

                        override fun onLost(postcard: Postcard?) {
                        }

                        override fun onArrival(postcard: Postcard?) {
                        }

                        override fun onInterrupt(postcard: Postcard?) {

                            runOnUiThread {
                                if (postcard?.tag == TokenException().message) {
                                    AlertDialog.Builder(this@MainActivity).setTitle("tips")
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

            btn_05.apply {
                text = "去Home页面-自动拦截"
                setOnClickListener {
                    ARouter.getInstance().build("/home/HomeActivity")
                        .with(bundleOf("name" to "LuBan", "pwd" to "12345"))
                        .navigation()
                }
            }

        }

    }

    private fun initARouter() {
        // 调试模式不是必须开启，但是为了防止有用户开启了InstantRun，但是
        // 忘了开调试模式，导致无法使用Demo，如果使用了InstantRun，必须在
        // 初始化之前开启调试模式，但是上线前需要关闭，InstantRun仅用于开
        // 发阶段，线上开启调试模式有安全风险，可以使用BuildConfig.DEBUG
        // 来区分环境
        ARouter.openDebug()
        ARouter.openLog()
        ARouter.init(application)
    }

    private fun showInfo(msg: String) {
        tv_info.append("\n$msg")
    }
}