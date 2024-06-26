package com.example.shaketorch

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.shaketorch.ui.theme.ShakeTorchTheme

class MainActivity : ComponentActivity() {
    private var shakeIndex: Int = 0
    private lateinit var cameraManager: CameraManager
    private lateinit var vibrationManager: VibratorManager
    private val torchState = FlashLightState { this.onTorchChanged() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.lateInit()
        this.drawCurrentState()
    }

    private fun lateInit() {
        val sensorListener = SensorEvent { this.onShake() }
        val sensorManager = (getSystemService(Context.SENSOR_SERVICE) as? SensorManager)
            ?: error("SENSOR_SERVICE is not a SensorManager!")

        sensorManager.registerListener(
            sensorListener,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )

        this.cameraManager =
            (getSystemService(Context.CAMERA_SERVICE)) as? CameraManager
                ?: error("CAMERA_SERVICE is not a CameraManager!")
        this.cameraManager.registerTorchCallback(this.torchState, null)

        this.vibrationManager =
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
                ?: error("VIBRATOR_MANAGER_SERVICE is not a VibratorManager")
    }

    private fun onShake() {
        this.shakeIndex += 1
        this.toggleTorch()
        this.drawCurrentState()
    }

    private fun onTorchChanged() {
        this.drawCurrentState()
    }

    private fun toggleTorch() {
        this.cameraManager.setTorchMode("0", !this.torchState.isOn())
        this.vibrate()
    }

    private fun vibrate() {
        this.vibrationManager.defaultVibrator.vibrate(
            VibrationEffect.createWaveform(
                longArrayOf(100, 50, 100),
                intArrayOf(VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE),
                -1
            )
        )
    }

    private fun drawCurrentState() {
        this.setContent {
            ShakeTorchTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val self = this
                    Column {
                        Text(text = "Shake event n°" + self.shakeIndex)
                        Text(text = "Torch is currently " + self.torchState.isOn())
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShakeTorchTheme {
        Text(text = "TODO understand what a greeting preview is")
    }
}