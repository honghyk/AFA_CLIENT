package com.example.graduation

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class WidgetFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var binding: FragmentWidgetBinding
    private lateinit var progressDialog: ProgressDialog

    private val dispose = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_widget, container, false)
        binding.vm = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.lifecycleOwner = this
        initViews()
        observeViewModel()
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
        progressDialog = ProgressDialog(requireContext()).apply {
            setMessage(getString(R.string.loading))
            isIndeterminate = true
            setCancelable(false)
        }
    }

    private fun observeViewModel() {
        val context = context ?: return

        viewModel.observeImageUri()
            .filter { it.isNotEmpty() }
            .flatMapSingle {
                var start = 0L
                progressDialog.show()
                viewModel.observeUploadImage(context, it)
                    .doOnSubscribe { start = System.currentTimeMillis() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess { Timber.d("network call success: ${System.currentTimeMillis() - start}ms") }
                    .doOnError {
                        viewModel.imageUri = ""
                        progressDialog.dismiss()
                        activity?.showToast("Network error occurred")
                    }
            }
            .flatMap {
                ModelRenderHelper.renderModelWithGLTF(context, it)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError {
                        viewModel.imageUri = ""
                        progressDialog.dismiss()
                        activity?.showToast("Model rendering failed")
                    }
                    .retry()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.renderable = it
                activity?.showToast("Model rendering finished")
                progressDialog.dismiss()
            }, { Timber.d(it) })
            .addTo(dispose)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GALLERY_REQ_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    viewModel.imageUri = data?.data.toString()
                }
                RESULT_CANCELED -> {
                    activity?.showToast("Picking image canceled")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dispose.dispose()
    }

    companion object {
        const val TAG = "WidgetFragment"
        const val GALLERY_REQ_CODE = 12

        fun create() = WidgetFragment()
    }
}