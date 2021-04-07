package q.rorbin.component

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private val helloService by componentService<IHelloService>("1.0")
    private val mainService by componentService<IMainService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        helloService.sayHello()
        mainService.sayHello()
    }
}