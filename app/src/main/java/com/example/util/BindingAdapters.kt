package com.example.util

import android.net.Uri
import android.view.View
import androidx.databinding.BindingAdapter
import com.facebook.drawee.view.SimpleDraweeView

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("gone")
    fun setGone(v: View, gone: Boolean?) {
        if(gone != null) {
            v.visibility = if(gone) View.GONE else View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("actualImageUri")
    fun setActualImageUri(v: SimpleDraweeView, uri: String) {
        if(uri.isNotEmpty()) {
            v.setImageURI(uri, null)
        } else {
            v.setImageURI(null as Uri?, null)
        }
    }
}