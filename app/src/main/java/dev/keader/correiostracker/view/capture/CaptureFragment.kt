package dev.keader.correiostracker.view.capture

import android.Manifest
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.databinding.FragmentCaptureBinding
import dev.keader.correiostracker.util.EventObserver
import timber.log.Timber
import java.util.concurrent.Executors

@AndroidEntryPoint
class CaptureFragment : Fragment() {
    private lateinit var binding: FragmentCaptureBinding
    private lateinit var codeDetector: TrackingCodeDetectionProcessor
    private val viewModel by viewModels<CaptureViewModel>()
    private val uiViewModel by activityViewModels<UIViewModel>()

    private val requestInitialPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) initializeCamera()
        else viewModel.onPermissionsDenied()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentCaptureBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiViewModel.onQrCodeDetected.observe(viewLifecycleOwner, EventObserver {
            val player = MediaPlayer.create(requireContext(), R.raw.scan_complete)
            player.setVolume(0.35f, 0.35f)
            player.start()
            findNavController().popBackStack()
        })
    }

    override fun onStart() {
        super.onStart()
        codeDetector = TrackingCodeDetectionProcessor(uiViewModel)
        codeDetector.start()
    }

    override fun onResume() {
        super.onResume()
        requestInitialPermissions.launch(Manifest.permission.CAMERA)
    }

    private fun initializeCamera() {
        val provider = ProcessCameraProvider.getInstance(requireContext())

        provider.addListener({
            val cameraProvider = provider.get()
            val preview = Preview.Builder()
                .build()

            preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

            val selector = CameraSelector.DEFAULT_BACK_CAMERA

            val rotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requireContext().display!!.rotation
            } else {
                requireActivity().windowManager.defaultDisplay.rotation
            }

            Timber.d("Rotation: $rotation")

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(Executors.newSingleThreadExecutor(), codeDetector)
                }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(viewLifecycleOwner, selector, preview, imageAnalysis)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onStop() {
        super.onStop()
        codeDetector.stop()
    }

}