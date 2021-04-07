package q.rorbin.component

import android.app.Application

/**
 * @author changhai.qiu
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ComponentHelper.init()
    }
}