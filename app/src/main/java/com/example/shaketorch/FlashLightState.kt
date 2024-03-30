package com.example.shaketorch

import android.hardware.camera2.CameraManager

class FlashLightState : CameraManager.TorchCallback() {
    private var torchState: Boolean = false

    override fun onTorchModeChanged(cameraId: String, enable: Boolean) {
        this.torchState = enable
    }

    fun isOn(): Boolean {
        return this.torchState
    }
}