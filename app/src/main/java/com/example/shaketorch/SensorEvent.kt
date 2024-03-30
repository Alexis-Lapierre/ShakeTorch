package com.example.shaketorch

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import kotlin.math.abs

class SensorEvent(val onShake: () -> Unit) : SensorEventListener {
    private val MIN_FORCE: Int = 10
    private val MIN_DIRECTION_CHANGE: Int = 3
    private val MAX_PAUSE_BETWEEN_DIRECTION_CHANGE: Int = 200
    private val MAX_TOTAL_DURATION_OF_SHAKE: Int = 400
    private val TIMEOUT_BETWEEN_ACTIVATIONS_IN_MILI: Long = 2_000

    private var mFirstDirectionChangeTime: Long = 0
    private var mLastDirectionChangeTime: Long = 0
    private var mDirectionChangeCount: Int = 0

    private var lastX: Float = 0f
    private var lastY: Float = 0f
    private var lastZ: Float = 0f

    private var lastShakeActivation: Long = 0

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if (sensorEvent == null) {
            error("sensorEvent.onSensorChanged should not be null")
        }

        // get time
        val now = System.currentTimeMillis()
        if ((this.lastShakeActivation + TIMEOUT_BETWEEN_ACTIVATIONS_IN_MILI) > now) {
            return
        }

        // Axis of the rotation sample, not normalized yet.
        val (x, y, z) = sensorEvent.values
        val totalMovement = abs(x + y + z - this.lastX - this.lastY - this.lastZ)
        if (totalMovement > MIN_FORCE) {


            // store first movement time
            if (mFirstDirectionChangeTime == 0L) {
                mFirstDirectionChangeTime = now
                mLastDirectionChangeTime = now
            }

            // check if the last movement was not long ago
            val lastChangeWasAgo = now - mLastDirectionChangeTime
            if (lastChangeWasAgo < MAX_PAUSE_BETWEEN_DIRECTION_CHANGE) {

                // store movement data
                mLastDirectionChangeTime = now
                mDirectionChangeCount++

                // store last sensor data
                this.lastX = x
                this.lastY = y
                this.lastZ = z

                // check how many movements are so far
                if (mDirectionChangeCount >= MIN_DIRECTION_CHANGE) {

                    // check total duration
                    val totalDuration = now - mFirstDirectionChangeTime
                    if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE) {
                        this.onShake()
                        this.resetShakeParameters()
                        this.lastShakeActivation = now
                    }
                }
            } else {
                resetShakeParameters()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //TODO Nothing to be done, I think
    }

    private fun resetShakeParameters() {
        this.mFirstDirectionChangeTime = 0
        this.mDirectionChangeCount = 0
        this.mLastDirectionChangeTime = 0
        this.lastX = 0f
        this.lastY = 0f
        this.lastZ = 0f
    }
}