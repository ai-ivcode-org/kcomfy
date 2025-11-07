package org.ivcode.kcomfy.utils

import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.primaryConstructor


/**
 * Find the annotation instance of this annotation type on the target class (or null).
 *
 * This typed variant returns the actual annotation subtype [A] when called on
 * a specific annotation KClass (for example: `MyAnnotation::class.findOn(MyClass::class)`).
 */
fun <A : Annotation> KClass<A>.findOn(target: KClass<*>): A? =
    target.annotations.firstOrNull { this.java.isInstance(it) } as? A

/**
 * Create an instance of this KClass using one of several strategies:
 *  1) If the primary constructor exists and all parameters are optional, call it with defaults.
 *  2) Try kotlin.reflect.full.createInstance() which requires an accessible no-arg ctor.
 *  3) Fall back to Java reflection to invoke a no-arg constructor (may be private).
 *
 * Throws ReflectiveOperationException when no instantiation strategy succeeds.
 */
fun <T : Any> KClass<T>.createInstanceSafe(): T {
    // 1) primary constructor with all optional parameters
    val primary = this.primaryConstructor
    if (primary != null && primary.parameters.all { it.isOptional }) {
        return primary.callBy(emptyMap()) as T
    }

    // 2) kotlin createInstance (requires kotlin-reflect)
    try {
        return this.createInstance()
    } catch (_: Throwable) {
        // ignore and try Java fallback
    }

    // 3) Java reflection fallback
    val ctor = this.java.getDeclaredConstructor()
    ctor.isAccessible = true
    return ctor.newInstance() as T
}
