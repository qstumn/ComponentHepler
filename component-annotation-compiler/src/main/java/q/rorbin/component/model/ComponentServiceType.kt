package q.rorbin.component.model

import java.util.*

/**
 * @author changhai.qiu
 */
class ComponentServiceType(val name: String, val version: String, val interfaces: List<String>) {
    override fun hashCode(): Int {
        return Objects.hash(name)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class.java != other::class.java) return false
        val that = other as ComponentServiceType
        return name == that.name
    }
}