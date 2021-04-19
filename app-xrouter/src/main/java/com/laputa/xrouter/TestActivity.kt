package com.laputa.xrouter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.laputa.xrouter.annotations.XRoute
import kotlinx.android.synthetic.main.activity_test.*

@XRoute(path = "/app/TestActivity")
class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        btn.setOnClickListener {
            MainActivity.navigation(this,"xxx","123456",18)
        }
    }


}