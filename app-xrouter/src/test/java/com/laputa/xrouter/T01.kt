package com.laputa.xrouter

import com.laputa.xrouter.annotations.XRoute
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class T01 {
    @Test
    fun t01() {

        XRoute::class.java.apply {
            println(this.canonicalName)
            println(this.name)
            println(this.simpleName)
            println(this.typeName)
        }



    }
}