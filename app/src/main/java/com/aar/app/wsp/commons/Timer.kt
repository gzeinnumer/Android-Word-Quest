package com.aar.app.wsp.commons

import android.os.Handler
import android.os.SystemClock
import android.util.Log
import java.util.*
import java.util.Timer

/**
 * Created by abdularis on 14/04/17.
 */
class Timer(interval: Long) {

    private val timeoutListeners: MutableList<OnTimeoutListener>
    private val stoppedListeners: MutableList<OnStoppedListener>
    private val startedListeners: MutableList<OnStartedListener>
    var isStarted: Boolean
        private set
    private var startTime: Long
    private var mElapsedTime: Long
    private val interval: Long
    private var timer: Timer?
    private val handler: Handler
    private val runnable: Runnable

    fun start() {
        if (isStarted) return
        val task: TimerTask = object : TimerTask() {
            override fun run() {
                handler.post(runnable)
            }
        }
        isStarted = true
        startTime = SystemClock.uptimeMillis()
        timer = Timer()
        timer?.scheduleAtFixedRate(task, interval, interval)
        callStartedListener()
    }

    fun stop() {
        if (!isStarted) return
        timer?.cancel()
        timer = null
        isStarted = false
        mElapsedTime = SystemClock.uptimeMillis() - startTime
        callStoppedListeners(mElapsedTime)
        Log.v("MyTimer", "stop called")
    }

    fun addOnTimeoutListener(listener: OnTimeoutListener) {
        timeoutListeners.add(listener)
    }

    fun addOnStoppedListener(listener: OnStoppedListener) {
        stoppedListeners.add(listener)
    }

    fun addOnStartedListener(listener: OnStartedListener) {
        startedListeners.add(listener)
    }

    private fun callTimeoutListeners(elapsedTime: Long) {
        for (listener in timeoutListeners) listener.onTimeout(elapsedTime)
    }

    private fun callStoppedListeners(elapsedTime: Long) {
        for (listener in stoppedListeners) listener.onStopped(elapsedTime)
    }

    private fun callStartedListener() {
        for (listener in startedListeners) listener.onStarted()
    }

    interface OnTimeoutListener {
        fun onTimeout(elapsedTime: Long)
    }

    interface OnStoppedListener {
        fun onStopped(elapsedTime: Long)
    }

    interface OnStartedListener {
        fun onStarted()
    }

    init {
        timeoutListeners = arrayListOf()
        stoppedListeners = arrayListOf()
        startedListeners = arrayListOf()
        isStarted = false
        startTime = 0L
        mElapsedTime = 0L
        this.interval = if (interval > 0) interval else 1000
        handler = Handler()
        timer = null
        runnable = Runnable {
            mElapsedTime = SystemClock.uptimeMillis() - startTime
            callTimeoutListeners(mElapsedTime)
        }
    }
}