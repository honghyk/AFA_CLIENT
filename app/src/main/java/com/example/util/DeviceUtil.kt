package com.example.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import com.example.graduation.extension.showToast
import timber.log.Timber

object DeviceUtil {

    private const val MIN_OPENGL_VERSION = 3.0

    fun checkIsSupportDevice(activity: Activity): Boolean {
        val openGlVersionString =
            (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion

        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Timber.d("Sceneform requires OpenGL ES 3.0 later")
            activity.run {
                showToast("Sceneform requires OpenGL ES 3.0 later")
                finish()
            }
            return false
        }
        return true
    }
}