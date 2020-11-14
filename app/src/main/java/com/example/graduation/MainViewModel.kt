package com.example.graduation

import android.content.Context
import android.os.FileUtils
import android.os.SystemClock
import androidx.core.net.toUri
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.graduation.api.Retrofit
import com.example.graduation.arch.BaseViewModel
import com.example.graduation.extension.toPart
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.net.URI

class MainViewModel : BaseViewModel() {

    var renderable: ModelRenderable? = null
        set(value) {
            field = value
            if (value == null) {
                modelDeletable.value = false
            }
        }

    val scale = Vector3(1.0f, 1.0f, 1.0f)

    val actualScale = MutableLiveData<Vector3>(Vector3())

    val modelDeletable = MutableLiveData<Boolean>(false)

    private val imageUriSubject = BehaviorSubject.create<String>()

    @get:Bindable
    var imageUri: String = ""
        set(value) {
            if(value != field) {
                field = value
                imageUriSubject.onNext(field)
                notifyPropertyChanged(BR.imageUri)
            }
        }

    val clearModelSignal = MutableLiveData<Boolean>(false)

    fun observeImageUri(): Observable<String> = imageUriSubject.hide()

    fun observeUploadImage(context: Context, uri: String): Single<String> {
        val imageApi = Retrofit.getImageApi()
        val file = createFileFromUri(context, uri)
        val type = getContentType(context, uri)

        return imageApi.uploadImage(
            userId = "random",
            image = file.toPart("image", type)
        ).doOnError { Timber.d(it) }
    }

    private fun createFileFromUri(context: Context, uri: String): File {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val file = File(context.cacheDir, "image_${SystemClock.uptimeMillis()}")
            val outputStream = FileOutputStream(file)
            val inputStream = context.contentResolver.openInputStream(uri.toUri())
                ?: throw Exception("InputStream null")
            FileUtils.copy(inputStream, outputStream)
            outputStream.flush()
            inputStream.close()
            outputStream.close()
            file
        } else {
            File(URI(uri))
        }
    }

    private fun getContentType(context: Context, uri: String): String? {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            context.contentResolver.getType(uri.toUri())
        } else {
            null
        }
    }

    fun clear() {
        renderable = null
        imageUri = ""
    }
}