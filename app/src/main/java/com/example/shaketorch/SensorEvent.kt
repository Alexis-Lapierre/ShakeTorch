package com.example.shaketorch

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

class SensorEvent(val onShake: () -> Unit) : SensorEventListener {
    override fun onSensorChanged(event: SensorEvent?) {
        this.onShake()
        // TODO Still a lot work to be done
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //TODO("Not yet implemented")
    }
}