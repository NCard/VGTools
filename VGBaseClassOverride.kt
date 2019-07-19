package vgTools

import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.Method

fun Any.propertyDescriptors(): Array<PropertyDescriptor> {
    val beanInfo = Introspector.getBeanInfo(this.javaClass)
    return beanInfo.propertyDescriptors
}

inline fun Any.foreachProperty(action: (key: String, value: Any?, setter: Method?) -> Unit) {

    fun String.isClass(): Boolean = this.compareTo(other = "class", ignoreCase = true) == 0

    propertyDescriptors().forEach {
        val key = it.name
        val getter = it.readMethod
        val setter = it.writeMethod
        val value = getter?.invoke(this)

        if (key.isClass()) return@forEach

        action(key, value, setter)
    }
}

@Throws(Exception::class)
fun Any.toMap(): MutableMap<String, Any?> {
    val map = mutableMapOf<String, Any?>()
    this.foreachProperty { key, value, _ ->
        map[key] = value
    }
    return map
}

@Throws(Exception::class)
fun Any.setValueByMap(map: MutableMap<String, Any?>) {
    this.foreachProperty { key, _, setter ->
        if (map[key] == null) return@foreachProperty
        else setter?.invoke(this, map[key])
    }
}

fun List<Any>.toMap(): Map<String, ClassDetail> {
    return this.map { this.indexOf(it) to it }.toMap()
}