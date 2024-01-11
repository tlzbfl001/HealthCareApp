package com.makebodywell.bodywell.util

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

class PermissionUtil {
    companion object {
        private val common = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        )

        val permission1 = arrayOf(
            *common,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        @RequiresApi(Build.VERSION_CODES.S)
        val permission2 = arrayOf(
            *common,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.SCHEDULE_EXACT_ALARM
        )

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val permission3 = arrayOf(
            *common,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.SCHEDULE_EXACT_ALARM,
            Manifest.permission.POST_NOTIFICATIONS
        )

        val cameraPermission1 = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        @RequiresApi(Build.VERSION_CODES.S)
        val cameraPermission2 = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val cameraPermission3 = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )

        val healthPermission = arrayOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }
}