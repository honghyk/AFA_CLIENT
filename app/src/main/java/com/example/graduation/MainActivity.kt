package com.example.graduation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import androidx.lifecycle.Observer
import com.example.util.DeviceUtil
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import timber.log.Timber

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

            vm.renderable?.let {
                Timber.d("ModelRender exists")
                val anchor = hitResult.createAnchor()
                anchorNode = AnchorNode(anchor).apply {
                    setParent(arFragment.arSceneView.scene)
                }

                TransformableNode(arFragment.transformationSystem).apply {
                    setParent(anchorNode)
                    renderable = it
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

    companion object {
        const val AR_FRAGMENT_TAG = "ArFragment"
    }
}