# ComponentHelper

一款轻量级的组件助手库, 支持多Module间组件初始化及组件服务依赖注入

## how to use:

### 1. 组件初始化, 使用ComponentInitializator注解标注初始化类即可, 当调用ComponentHelper.init()时, 所有该注解标注的类都会被创建
```kotlin
@ComponentInitializator
class MainInit {
    init {
        //do something
    }
}
//也可以选择实现IComponent接口
@ComponentInitializator
class MainInit : IComponent {
    override fun onInit() {
        //do something
    }
}

//建议在application onCreate中调用ComponentHelper.init()
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ComponentHelper.init()
    }
}
```
### 2. 组件间服务调用, 使用ComponentService注解标注服务的实现类
```kotlin
//服务接口可选择继承IComponent, 如继承组件实现类创建时会被回调onInit
interface IHelloService : IComponent {
    fun sayHello()
}

@ComponentService()
class HelloService : IHelloService {
    override fun onInit() {
        //do something
    }
    override fun sayHello() {
        Log.i(TAG, "HelloService.hello")
    }
}

//可通过version指定服务版本
@ComponentService(version = "1.0")
class HelloService : IHelloService

//获取服务
val helloService by componentService<IHelloService>()
//or
val helloService = ComponentHelper.createComponentService(IHelloService::class.java, "1.0")
```

### 3. 依赖
```groovy
    maven { url 'https://jitpack.io' }
    //Project build.gradle
    classpath "com.github.qstumn.ComponentHepler:component-gradle-plugin:$release"
    //app Module
    apply plugin: 'q.rorbin.component'
    //组件Module build.gradle
    implementation "com.github.qstumn.ComponentHepler:component-annotation-compiler:$release"
    kapt "com.github.qstumn.ComponentHepler:component-annotation-compiler:$release"
```