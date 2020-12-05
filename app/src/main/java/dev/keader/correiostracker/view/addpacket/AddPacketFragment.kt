package dev.keader.correiostracker.view.addpacket

import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiosapi.Correios
import dev.keader.correiostracker.CaptureActivity
import dev.keader.correiostracker.MainActivity
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.databinding.FragmentAddPacketBinding
import dev.keader.correiostracker.work.RefreshTracksWorker

@AndroidEntryPoint
class AddPacketFragment : Fragment() {

    private val uiViewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentAddPacketBinding
    private val addPacketViewModel: AddPacketViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        uiViewModel.setBottomNavVisibility(View.GONE)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_packet, container, false)
        binding.addPacketViewModel = addPacketViewModel

        addPacketViewModel.eventCancelButtonNavigation.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().popBackStack()
                addPacketViewModel.handleCancelButtonEventFinish()
            }
        })

        // Triggered by OK button
        addPacketViewModel.eventCheckInputs.observe(viewLifecycleOwner, Observer { checkInputs ->
            if (checkInputs) {
                val code = binding.trackEditText.text.toString().toUpperCase()
                val observation = binding.descriptionEditText.text.toString()

                if (validateInputs(code, observation)) {
                    addPacketViewModel.handleCheckOK(code, observation)
                    binding.progressBar.visibility = View.VISIBLE
                    getSnack(getString(R.string.tracking_product))
                        ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                        ?.show()
                } else {
                    getSnack(getString(R.string.add_product_error_message))
                        ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.errorColor))
                        ?.show()
                    addPacketViewModel.handleCheckFail()
                }
            }
        })

        // Return of API
        addPacketViewModel.eventAddTrack.observe(viewLifecycleOwner, Observer { success ->
            if (success != null) {
                binding.progressBar.visibility = View.GONE

                if (success) {
                    getSnack(getString(R.string.track_add_success))
                        ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.sucessColor))
                        ?.show()

                    RefreshTracksWorker.startWorker(requireNotNull(activity).application)
                    findNavController().popBackStack()
                } else {
                    getSnack(getString(R.string.track_add_fail))
                        ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.errorColor))
                        ?.show()
                }
                addPacketViewModel.handleAddEventFinish()

            }
        })

        addPacketViewModel.eventQR.observe(viewLifecycleOwner, Observer { clicked ->
            if (clicked) {
                scanQRCode()
                addPacketViewModel.qRCodeEventFinished()
            }
        })

        uiViewModel.qrCodeResult.observe(viewLifecycleOwner, Observer { result ->
            result?.let { qr ->
                if (Correios.isValidCode(qr))
                    binding.trackEditText.setText(qr)
                uiViewModel.finishQrCode()
            }
        })

        binding.backImage.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun getSnack(string: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar? {
        val activity = activity
        if (activity is MainActivity)
            return activity.getSnackInstance(string, duration)
        return null
    }


    private fun validateInputs(code: String, observation: String): Boolean {
        var error = false

        // Check code
        if (!Correios.isValidCode(code)) {
            binding.trackEditText.error = getString(R.string.add_code_error_message)
            error = true
        } else
            binding.trackEditText.error = null

        // Check product name
        if (observation.isEmpty()) {
            binding.descriptionEditText.error = getString(R.string.add_description_error_code)
            error = true
        } else
            binding.descriptionEditText.error = null

        return !error
    }

    private fun scanQRCode() {
        val integrator = IntentIntegrator(activity).apply {
            captureActivity = CaptureActivity::class.java
            setOrientationLocked(false)
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            setPrompt(getString(R.string.qr_read))
        }
        integrator.initiateScan()
    }

    override fun onDestroyView() {
        uiViewModel.setBottomNavVisibility(View.VISIBLE)
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        val clipboard = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip
        clipData?.let {
            if (clipboard.primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_PLAIN) == true) {
                val text = clipData.getItemAt(0).text.toString().trim()
                if (Correios.isValidCode(text))
                    binding.trackEditText.setText(text)
            }
        }
    }
}
