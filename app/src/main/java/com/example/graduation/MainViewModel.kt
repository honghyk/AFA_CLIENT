package com.example.graduation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.ar.sceneform.rendering.ModelRenderable

class MainViewModel: ViewModel() {

    var renderable: ModelRenderable? = null
        set(value) {
            field = value
            if(value == null) {
                modelDeletable.value = false
            }
        }

    val modelDeletable = MutableLiveData<Boolean>(false)

    val imageUri = MutableLiveData<Uri?>(null)

    val clearModelSignal = MutableLiveData<Boolean>(false)

    fun clear() {
        renderable = null
        imageUri.value = null
    }
}