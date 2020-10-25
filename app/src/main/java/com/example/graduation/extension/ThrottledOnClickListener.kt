package com.example.graduation.extension

import android.os.SystemClock
import android.view.View

abstract class ThrottledOnClickListener(private val throttleInMillis: Long = 500L) :
    View.OnClickListener {

    private var lastClicked = 0L

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastClicked < throttleInMillis) {
            return
        }

        lastClicked = SystemClock.elapsedRealtime()
        onThrottledClick(v)
    }

    abstract fun onThrottledClick(v: View)
}

fun View.setThrottledOnClickListener(throttleInMillis: Long = 500L,
                                     clickAction: (View) -> Unit) {
    setOnClickListener(object : ThrottledOnClickListener(throttleInMillis) {
        override fun onThrottledClick(v: View) {
            clickAction.invoke(v)
        }
    })
}