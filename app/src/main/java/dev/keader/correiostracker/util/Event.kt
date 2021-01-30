package dev.keader.correiostracker.util

import androidx.lifecycle.Observer

open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peek(): T = content
}

class EventObserver<T>(private val onEventUnhandled: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getIfNotHandled()?.let { value ->
            onEventUnhandled(value)
        }
    }
}
