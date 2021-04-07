package q.rorbin.component

import android.util.Log
import q.rorbin.component.annotation.ComponentService
import q.rorbin.component.interfaces.IComponent

/**
 * @author changhai.qiu
 */
interface IHelloService {
    fun sayHello()
}

interface IMainService : IComponent {
    fun sayHello()
}

@ComponentService(version = "1.0")
class HelloService : IHelloService {
    override fun sayHello() {
        Log.i(TAG, "HelloService.hello")
    }
}

@ComponentService
class MainService : IMainService {
    override fun sayHello() {
        Log.i(TAG, "MainService.hello")
    }

    override fun onInit() {
        Log.i(TAG, "MainService.onInit")
    }
}