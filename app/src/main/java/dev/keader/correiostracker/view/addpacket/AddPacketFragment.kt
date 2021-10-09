package dev.keader.correiostracker.view.addpacket

import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiosapi.Correios
import dev.keader.correiostracker.MainActivity
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.databinding.FragmentAddPacketBinding
import dev.keader.correiostracker.model.EventObserver
import dev.keader.correiostracker.model.PreferencesManager
import dev.keader.correiostracker.work.RefreshTracksWorker
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddPacketFragment : Fragment() {
    private val uiViewModel: UIViewModel by activityViewModels()
    private val addPacketViewModel: AddPacketViewModel by viewModels()
    private lateinit var binding: FragmentAddPacketBinding
    private val navController
        get() = findNavController()
    @Inject
    lateinit var preferences : PreferencesManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        uiViewModel.setBottomNavVisibility(View.GONE)
        binding = FragmentAddPacketBinding.inflate(inflater, container, false)
        binding.addPacketViewModel = addPacketViewModel

        addPacketViewModel.eventBackButtonNavigation.observe(viewLifecycleOwner, EventObserver {
            navController.popBackStack()
        })

        // Triggered by OK button
        addPacketViewModel.eventCheckInputs.observe(viewLifecycleOwner, EventObserver {
            val code = binding.trackEditText.text.toString().uppercase()
            val observation = binding.descriptionEditText.text.toString()

            if (validateInputs(code, observation)) {
                val autoMove = preferences.getAutoMove()
                addPacketViewModel.handleCheckOK(code, observation)
                binding.progressBar.visibility = View.VISIBLE

                if (autoMove)
                    addPacketViewModel.handleAutoSave(code)

                getSnack(getString(R.string.tracking_product))
                    ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                    ?.show()
            } else {
                getSnack(getString(R.string.add_product_error_message))
                    ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.errorColor))
                    ?.show()
            }
        })

        // Return of API
        addPacketViewModel.eventAddTrack.observe(viewLifecycleOwner, EventObserver { success ->
            binding.progressBar.visibility = View.GONE
            if (success) {
                getSnack(getString(R.string.track_add_success))
                    ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.sucessColor))
                    ?.show()

                RefreshTracksWorker.startWorker(requireNotNull(activity).application, preferences)
                navController.popBackStack()
            } else {
                getSnack(getString(R.string.track_add_fail))
                    ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.errorColor))
                    ?.show()
            }
        })

        addPacketViewModel.eventQR.observe(viewLifecycleOwner, EventObserver {
            val directions = AddPacketFragmentDirections.actionAddPacketFragmentToCaptureFragment()
            navController.navigate(directions)
        })

        uiViewModel.qrCodeResult.observe(viewLifecycleOwner, EventObserver { qr ->
            if (Correios.validateCode(qr))
                binding.trackEditText.setText(qr)
        })

        binding.iconBackAdd.setOnClickListener {
            navController.popBackStack()
        }

        if (preferences.getScanIntro()) {
            addPacketViewModel.viewModelScope.launch {
                preferences.saveScanIntro(false)
            }
            showTutorial()
        }

        binding.lifecycleOwner = this
        return binding.root
    }

    private fun showTutorial() {
        TapTargetView.showFor(requireActivity(),
            TapTarget.forView(binding.qrView,
                getString(R.string.scan_title),
                getString(R.string.scan_description))
                .outerCircleColor(R.color.secondaryColor)
                .outerCircleAlpha(1.0f)
                .targetCircleColor(android.R.color.white)
                .titleTextSize(22)
                .descriptionTextSize(16)
                .descriptionTextAlpha(0.8f)
                .textColor(android.R.color.white)  // text and description
                .textTypeface(Typeface.SANS_SERIF)
                .dimColor(android.R.color.black)
                .drawShadow(true)
                .cancelable(true)
                .transparentTarget(true)
                .targetRadius(40),
            object : TapTargetView.Listener() {
                override fun onOuterCircleClick(view: TapTargetView?) {
                    super.onOuterCircleClick(view)
                    view?.dismiss(false)
                }
            })
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
        if (!Correios.validateCode(code)) {
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

    override fun onResume() {
        super.onResume()
        val currentCode = binding.trackEditText.text.toString()
        if (currentCode.isNotEmpty() && Correios.validateCode(currentCode))
            return

        val clipboard = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip
        clipData?.let {
            val code = clipData.getItemAt(0)?.text?.toString()?.trim() ?: return
            if (Correios.validateCode(code))
                binding.trackEditText.setText(code)
        }
    }

    override fun onDestroy() {
        uiViewModel.setBottomNavVisibility(View.VISIBLE)
        super.onDestroy()
    }
}
