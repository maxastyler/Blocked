package com.maxtyler.blocked.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class Timer(private val scope: CoroutineScope) {
    object Timeout

    private var job: Job? = null

    private val _events: MutableSharedFlow<Timeout> = MutableSharedFlow()
    val events = _events.asSharedFlow()

    val started
        get() = job?.isActive ?: false

    /**
     * Start the timer with the given time in milliseconds
     * @param time The time for the timeout in milliseconds
     * @param repeating If true, repeat the timeout
     * @return
     */
    fun start(time: Long, repeating: Boolean = false) {
        stop()
        if (repeating) {
            job = scope.launch {
                while (true) {
                    delay(time)
                    _events.emit(Timeout)
                }
            }
        } else {
            job = scope.launch {
                delay(time)
                _events.emit(Timeout)
            }
        }
    }

    /**
     * Stop the timer if it's currently started
     */
    fun stop() = job?.run { this.cancel() }
}