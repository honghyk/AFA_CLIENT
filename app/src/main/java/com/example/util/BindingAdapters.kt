package com.example.util

import android.view.View
import androidx.databinding.BindingAdapter

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("gone")
    fun setGone(v: View, gone: Boolean?) {
        if(gone != null) {
            v.visibility = if(gone) View.GONE else View.VISIBLE
        }
    }
}