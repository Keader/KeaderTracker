package dev.keader.correiostracker.view.capture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    private val uiViewModel by activityViewModels<UIViewModel>()

    private val requestInitialPermissions = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) initializeCamera()
        else onPermissionsDenied()
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

        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                initializeCamera()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showPermissionRationale()
            }
            else -> {
                requestInitialPermissions.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.permissions_camera_ask_permission)
            .setMessage(R.string.permissions_camera_ask_permission_message)
            .setCancelable(false)
            .setPositiveButton(R.string.permissions_camera_allow) { dialog, _ ->
                requestInitialPermissions.launch(Manifest.permission.CAMERA)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.permissions_camera_no_thanks) { dialog, _ ->
                dialog.dismiss()
                findNavController().popBackStack()
            }
            .create()
            .show()
    }

    private fun onPermissionsDenied() {
        if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Timber.d("User clicked to not ask again...")
            showPermissionPermanentlyDenied()
        } else {
            Timber.d("User didn't allow")
            Toast.makeText(requireContext(), R.string.permissions_denied_camera, Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun showPermissionPermanentlyDenied() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.permissions_denied_camera_ask_permission)
            .setMessage(R.string.permissions_denied_camera_ask_permission_message)
            .setCancelable(false)
            .setPositiveButton(R.string.permissions_camera_allow) { dialog, _ ->
                dialog.dismiss()

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton(R.string.permissions_camera_no_thanks) { dialog, _ ->
                dialog.dismiss()
                findNavController().popBackStack()
            }
            .create()
            .show()
    }

    private fun initializeCamera() {
        val provider = ProcessCameraProvider.getInstance(requireContext())

        provider.addListener({
            val cameraProvider = provider.get()
            val preview = Preview.Builder()
                .build()

            preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

            val selector = CameraSelector.DEFAULT_BACK_CAMERA

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