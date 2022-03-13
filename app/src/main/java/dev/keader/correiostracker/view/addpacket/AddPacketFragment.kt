package dev.keader.correiostracker.view.addpacket

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.MainActivity
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.databinding.FragmentAddPacketBinding
import dev.keader.correiostracker.model.EventObserver

@AndroidEntryPoint
class AddPacketFragment : Fragment() {
    private val uiViewModel: UIViewModel by activityViewModels()
    private val addPacketViewModel: AddPacketViewModel by viewModels()
    private lateinit var binding: FragmentAddPacketBinding
    private val navController
        get() = findNavController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAddPacketBinding.inflate(inflater, container, false)
        uiViewModel.setBottomNavVisibility(false)
        binding.addPacketViewModel = addPacketViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        addPacketViewModel.handleClipboardOnResume(clipboard.primaryClip)
    }

    override fun onDestroy() {
        uiViewModel.setBottomNavVisibility(true)
        super.onDestroy()
    }

    private fun setupObservers() {
        addPacketViewModel.eventBackButtonNavigation.observe(viewLifecycleOwner, EventObserver {
            navController.popBackStack()
        })

        addPacketViewModel.eventInputCodeError.observe(viewLifecycleOwner, EventObserver {
            binding.inputCode.error = getString(R.string.add_code_error_message)
            showInvalidInputsWarning()
        })

        addPacketViewModel.eventInputNameError.observe(viewLifecycleOwner, EventObserver {
            binding.inputName.error = getString(R.string.add_description_error_code)
            showInvalidInputsWarning()
        })

        addPacketViewModel.eventInputSuccess.observe(viewLifecycleOwner, EventObserver {
            binding.progressBar.isVisible = true
            getSnack(msg = getString(R.string.tracking_product), colorSrc = R.color.secondaryColor)
                ?.show()
        })

        addPacketViewModel.eventAddTrackFail.observe(viewLifecycleOwner, EventObserver {
            binding.progressBar.isVisible = false
            getSnack(msg = it, colorSrc = R.color.errorColor)?.show()
        })

        addPacketViewModel.eventAddTrackSuccess.observe(viewLifecycleOwner, EventObserver {
            binding.progressBar.isVisible = false
            getSnack(msg = getString(R.string.track_add_success), colorSrc = R.color.sucessColor)
                ?.show()
            addPacketViewModel.startRefreshWorker(requireActivity())
            navController.popBackStack()
        })

        addPacketViewModel.eventQR.observe(viewLifecycleOwner, EventObserver {
            val directions = AddPacketFragmentDirections.actionAddPacketFragmentToCaptureFragment()
            navController.navigate(directions)
        })

        uiViewModel.qrCodeResult.observe(viewLifecycleOwner, EventObserver {
            addPacketViewModel.addCodeByQR(it)
        })

        binding.iconBackAdd.setOnClickListener {
            navController.popBackStack()
        }

        binding.inputCode.setEndIconOnClickListener {
            addPacketViewModel.handleQRButton()
        }
    }

    private fun showInvalidInputsWarning() {
        getSnack(msg = getString(R.string.add_product_error_message), colorSrc = R.color.errorColor)
            ?.show()
    }

    private fun showTutorial() {
        TapTargetView.showFor(requireActivity(),
            TapTarget.forView(binding.inputCode,
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

    private fun getSnack(msg: String, duration: Int = Snackbar.LENGTH_SHORT, colorSrc: Int): Snackbar? {
        val myActivity = activity as? MainActivity
        val color = ContextCompat.getColor(requireContext(), colorSrc)
        return myActivity?.getSnackInstance(msg, duration)?.setBackgroundTint(color)
    }
}
