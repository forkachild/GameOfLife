package com.suhel.gameoflife

import android.os.Handler
import kotlin.jvm.Volatile
import android.os.Looper
import java.lang.Exception
import kotlin.jvm.Synchronized

abstract class SimpleTimer : Thread() {
    @get:Synchronized
    @set:Synchronized
    @Volatile
    var isRunning = true

    @get:Synchronized
    @set:Synchronized
    @Volatile
    var isPaused = false

    @get:Synchronized
    @set:Synchronized
    @Volatile
    var delay: Long = 1000

    private val mainThread = Handler(Looper.getMainLooper())

    override fun run() {
        while (isRunning) {
            try {
                while (isPaused) {
                }
                sleep(delay)
                mainThread.post { tick() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    abstract fun tick()
}