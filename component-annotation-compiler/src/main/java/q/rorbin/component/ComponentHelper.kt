package q.rorbin.component

import q.rorbin.component.interfaces.IComponent
import java.lang.RuntimeException

/**
 * @author changhai.qiu
 */
object ComponentHelper {

    private val serviceManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ComponentServiceManager() }
    private val serviceCache by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { mutableMapOf<String, MutableMap<String, Any>>() }

    fun init() {
        ComponentInitializatorManager().invokeAllComponentInitializator()
    }

    inline fun <reified I> createComponentService(version: String = "main"): I {
        return createComponentService(I::class.java, version)
    }

    fun <I> createComponentService(serviceInterface: Class<I>, version: String = "main"): I {
        val exists = serviceCache.getOrPut(serviceInterface.name, { mutableMapOf() })
        var service = exists[version] as? I
        if (service == null) {
            synchronized(serviceManager) {
                if (service == null) {
                    val serviceImpl = serviceManager.getService(serviceInterface, version)
                        ?.runCatching { this.newInstance() as? I }
                        ?.getOrNull()
                        ?: throw RuntimeException("${serviceInterface.name} implementation class not found")
                    if (serviceImpl is IComponent) {
                        serviceImpl.onInit()
                    }
                    exists[version] = serviceImpl as Any
                    service = serviceImpl
                }
            }
        }
        return service!!
    }
}

inline fun <reified I> componentService(version: String = "main"): Lazy<I> = lazy {
    val interfaceClass = I::class.java
    ComponentHelper.createComponentService(interfaceClass, version)
}