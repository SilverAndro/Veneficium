package net.vene.magic.handling

class DataContainer<out T>(val key: String, private val data: T) {
    fun get(): T {
        return data
    }
}
