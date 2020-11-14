package com.example.graduation.extension

import android.app.Activity
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.sceneform_assets.r

fun Activity.showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}