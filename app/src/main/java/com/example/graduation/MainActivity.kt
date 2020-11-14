package com.example.graduation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import androidx.lifecycle.Observer
import com.example.util.DeviceUtil
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import timber.log.Timber
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModels()

    private var anchorNode: AnchorNode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!DeviceUtil.checkIsSupportDevice(this)) {
            return
        }

        initArFragment()
        initWidgetFragment()
    }

    private fun initArFragment() {
        val fm = supportFragmentManager

        fm.commitNow {
            add(R.id.ar_fragment_container, ArFragment(), AR_FRAGMENT_TAG)
        }

        val arFragment = requireNotNull(fm.findFragmentByTag(AR_FRAGMENT_TAG) as? ArFragment)

        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            Timber.d("arFragment tapArPlane called")

            vm.renderable?.let { model ->
                Timber.d("ModelRender exists")
                val anchor = hitResult.createAnchor()
                anchorNode = AnchorNode(anchor).apply {
                    setParent(arFragment.arSceneView.scene)
                }

                TransformableNode(arFragment.transformationSystem).apply {
                    vm.actualScale.value = getActualScale(model, worldScale)
                    addTransformChangedListener { new, original ->
                        if(new.worldScale != original.worldScale) {
                            vm.actualScale.value = getActualScale(model, worldScale)
                        }
                    }
                    renderable = model
                    setParent(anchorNode)
                    vm.modelDeletable.value = true
                    select()
                }
            }
        }

        vm.clearModelSignal.observe(this, Observer {
            if(it) {
                removeAnchorNode(arFragment, anchorNode)
                vm.clearModelSignal.value = false
            }
        })
    }

    private fun removeAnchorNode(arFragment: ArFragment, nodeToRemove: AnchorNode?) {
        nodeToRemove?.let {
            arFragment.arSceneView.scene.removeChild(nodeToRemove)
            nodeToRemove.apply {
                anchor?.detach()
                setParent(null)
                nodeToRemove.renderable = null
                vm.clear()
            }
        }
    }

    private fun initWidgetFragment() {
        supportFragmentManager.commit {
            add(R.id.widget_fragment_container, WidgetFragment.create(), WidgetFragment.TAG)
        }
    }

    private fun getActualScale(model: Renderable, worldScale: Vector3): Vector3 {
        val modelScale = (model.collisionShape as Box).size
        return Vector3(
            ((modelScale.x * worldScale.x * 100).roundToInt() / 100.0f),
            ((modelScale.y * worldScale.y * 100).roundToInt() / 100.0f),
            ((modelScale.z * worldScale.z * 100).roundToInt() / 100.0f)
        )
    }

    companion object {
        const val AR_FRAGMENT_TAG = "ArFragment"
    }
}