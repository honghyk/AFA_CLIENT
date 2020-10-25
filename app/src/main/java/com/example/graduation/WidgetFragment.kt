package com.example.graduation

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.graduation.databinding.FragmentWidgetBinding
import com.example.graduation.extension.setThrottledOnClickListener
import com.example.graduation.extension.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber


class WidgetFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentWidgetBinding

//    private val GLTF_ASSET =
//        "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf";

//    private val GLTF_ASSET = "https://poly.googleusercontent.com/downloads/0BnDT3T1wTE/85QOHCZOvov/Mesh_Beagle.gltf"

//    private val GLTF_ASSET = "https://grad-project-s3.s3.ap-northeast-2.amazonaws.com/sofa.gltf"

    private val GLTF_ASSET =
        "https://grad-project-s3.s3.ap-northeast-2.amazonaws.com/lerhamn-chair-light.gltf"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_widget, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = this
        initViews()
    }

    private fun initViews() {
        with(binding) {

            clear.setThrottledOnClickListener {
                viewModel.clearModelSignal.value = true
            }

            openGallery.setThrottledOnClickListener {
                Intent().also {
                    it.type = "image/*"
                    it.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(it, GALLERY_REQ_CODE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_REQ_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val context = context ?: return

                    binding.imagePreview.setImageURI(data?.data, null)
                    viewModel.imageUri.value = data?.data

                    ModelRenderHelper.renderModelWithGLTF(context, GLTF_ASSET)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            showToast("Model rendering finished")
                            viewModel.renderable = it
                        }, {
                            showToast("Model rendering failed")
                            Timber.d(it)
                        })
                }
                RESULT_CANCELED -> {
                    showToast("Picking image canceled")
                }
            }
        }
    }

    companion object {
        const val TAG = "WidgetFragment"
        const val GALLERY_REQ_CODE = 12

        fun create() = WidgetFragment()
    }
}