package com.hanasmarcin.kivracoffee.utils

/**
 * Class that represent events that can be observed on LiveData only once - e. g. errors
 */
class Event<T>(
    private val value: T
) {
    private var isHandled = false

    fun getContentIfNotHandled(): T? {
        return if (!isHandled) {
            isHandled = true
            value
        } else null
    }

    fun peek() = value
}