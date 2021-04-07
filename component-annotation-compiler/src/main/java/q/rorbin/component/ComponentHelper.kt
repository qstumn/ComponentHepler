package q.rorbin.component

import q.rorbin.component.interfaces.IComponent
import java.lang.RuntimeException
import java.util.concurrent.ConcurrentHashMap

/**
 * @author changhai.qiu
 */
object ComponentHelper {

    private val serviceManager by lazy { ComponentServiceManager() }
    private val serviceCache by lazy { ConcurrentHashMap<String, Any>() }

    fun init() {
        ComponentInitializatorManager().invokeAllComponentInitializator()
    }

    inline fun <reified I> createComponentService(version: String = "main"): I {
        return createComponentService(I::class.java, version)
    }

    fun <I> createComponentService(serviceInterface: Class<I>, version: String = "main"): I {
        val key = "${serviceInterface.name}$$$version"
        var service = serviceCache[key] as? I
        if (service != null) {
            return service
        }
        service = serviceManager.getService(serviceInterface.name, version)
            ?.runCatching { Class.forName(this).newInstance() as? I }
            ?.getOrNull()
            ?: throw RuntimeException("${serviceInterface.name} implementation class not found")
        if (service is IComponent) {
            service.onInit()
        }
        serviceCache[key] = service as Any
        return service
    }
}

inline fun <reified I> componentService(version: String = "main"): Lazy<I> = lazy {
    val interfaceClass = I::class.java
    ComponentHelper.createComponentService(interfaceClass, version)
}