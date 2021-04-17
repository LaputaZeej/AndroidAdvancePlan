package com.laputa.xrouter.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.laputa.xrouter.annotations.XField
import com.laputa.xrouter.annotations.XRoute

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}