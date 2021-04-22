package com.laputa.plan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.laputa.plan.logger.Logger
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i("laputa_logger", "onCreate()")
        btn.apply {
            text = "test Asm"
            setOnClickListener {
                testAsm()
            }
        }

        btn01.apply {
            text = "test Asm adapter"
            setOnClickListener {
                testAsmAdapter()
            }
        }

    }

    fun testAsm() {
        repeat(10) {
            Log.i("laputa_logger", "testAsm -> $it")
        }
    }


    @Logger
    fun testAsmAdapter() {
        repeat(10) {
            Thread.sleep(10)
            Log.i("laputa_logger", "testAsm -> $it")
        }
    }
}