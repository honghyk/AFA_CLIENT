package com.example.util

import android.net.Uri
import android.text.Html
import android.view.View
import android.widget.TextView
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

    @JvmStatic
    @BindingAdapter("textHtml")
    fun setTextHtml(v: TextView, html: String?) {
        if (html == null) {
            v.text = ""
        } else {
            v.text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        }
    }
}