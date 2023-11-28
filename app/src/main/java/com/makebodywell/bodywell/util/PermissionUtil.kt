package com.makebodywell.bodywell.util

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

class PermissionUtil {
    companion object {
        private val common = arrayOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        )

        val permissions1 = arrayOf(
            *common,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        @RequiresApi(Build.VERSION_CODES.S)
        val permissions2 = arrayOf(
            *common,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SCHEDULE_EXACT_ALARM
        )

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val permissions3 = arrayOf(
            *common,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.SCHEDULE_EXACT_ALARM,
            Manifest.permission.POST_NOTIFICATIONS
        )

        val cameraPermissions1 = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        @RequiresApi(Build.VERSION_CODES.S)
        val cameraPermissions2 = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val cameraPermissions3 = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    }
}