package io.github.singlerr.semaphore.client.gui

import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KProperty

class Memoize<T, R>(supplier: (T) -> R) {

    private val cache: SimpleLazyInitializer<T, R> = SimpleLazyInitializer(supplier)

    operator fun getValue(thisRef: Any?, property: KProperty<*>) = { arg: T -> cache.get(arg) }
}

private class SimpleLazyInitializer<T, R>(val supplier: (T) -> R) {

    private val cachedValue: AtomicReference<R> = AtomicReference()

    private fun initialize(arg: T): R = supplier(arg)

    fun get(arg: T): R {
        return cachedValue.get() ?: initialize(arg).also { cachedValue.set(it) }
    }
}
