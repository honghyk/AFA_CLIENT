package com.example.graduation

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

object ModelRenderHelper {
    private val modelSubject = BehaviorSubject.create<ModelRenderable>()

    fun renderModelWithGLTF(context: Context, url: String): Observable<ModelRenderable> {
        val start = System.currentTimeMillis()

        ModelRenderable.builder()
            .setSource(
                context, RenderableSource.builder().setSource(
                    context,
                    Uri.parse(url),
                    RenderableSource.SourceType.GLTF2
                ).build()
            )
            .setRegistryId(url)
            .build()
            .thenAccept {
                Timber.d("model rendered successfully: %s", url)
                Timber.d("model rendering time: ${System.currentTimeMillis() - start}ms")

                modelSubject.onNext(it)
            }
            .exceptionally {
                modelSubject.onError(it)
                null
            }

        return modelSubject.hide()
    }

}