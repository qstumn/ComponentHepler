package q.rorbin.component.gradle.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import q.rorbin.component.gradle.plugin.transform.ComponentTransform

class ComponentPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val appExtension = target.extensions.getByType(AppExtension::class.java)
        appExtension.registerTransform(ComponentTransform())
    }
}