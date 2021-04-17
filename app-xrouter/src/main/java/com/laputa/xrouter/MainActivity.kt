package com.laputa.xrouter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.laputa.xrouter.annotations.XField
import com.laputa.xrouter.annotations.XRoute

@XRoute(path = "/app/MainActivity")
class MainActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_main)
        val msg = "name = $name , pwd = $pwd , age= $age"
        showInfo(msg)
    }

    private fun showInfo(msg: String) {
        findViewById<TextView>(R.id.tv_info).append("$msg\n")
    }
}