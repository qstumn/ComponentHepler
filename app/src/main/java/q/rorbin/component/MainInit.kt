package q.rorbin.component

import android.util.Log
import q.rorbin.component.annotation.ComponentInitializator
import q.rorbin.component.interfaces.IComponent

/**
 * @author changhai.qiu
 */
const val TAG = "qch"

@ComponentInitializator
class MainInit : IComponent {
    init {
        Log.i(TAG, "MainInit.constructor")
    }

    override fun onInit() {
        Log.i(TAG, "MainInit.onInit")
    }
}