package com.example.graduation.extension

import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

fun File.toPart(name: String, type: String? = null): MultipartBody.Part {
    val mimeType = type ?: MimeTypeMap.getSingleton().let { mimeTypeMap ->
        val lowercasedExtension = extension.toLowerCase(Locale.US)
        if (mimeTypeMap.hasExtension(lowercasedExtension)) {
            mimeTypeMap.getMimeTypeFromExtension(lowercasedExtension)
        } else "*/*"
    }
    val subType = mimeType?.toMediaTypeOrNull()?.subtype
    return MultipartBody.Part.createFormData(name, this.name + ".$subType",
        this.asRequestBody(mimeType?.toMediaTypeOrNull()))
}