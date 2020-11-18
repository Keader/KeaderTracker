package dev.keader.correiostracker.view.addpacket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dev.keader.correiosapi.Correios
import dev.keader.correiostracker.MainActivity
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.database.TrackingDatabase
import dev.keader.correiostracker.databinding.FragmentAddPacketBinding
import dev.keader.correiostracker.repository.TrackingRepository

class AddPacketFragment : Fragment() {

    private val uiViewModel: UIViewModel by activityViewModels()
    private lateinit var binding: FragmentAddPacketBinding
    private lateinit var addPacketViewModel: AddPacketViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        uiViewModel.setBottomNavVisibility(View.GONE)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_packet, container, false)

        val application = requireNotNull(activity).application
        val db = TrackingDatabase.getInstance(application).itemDatabaseDAO
        val repository = TrackingRepository(db)
        val viewModelFactory = AddPacketViewModelFactory(repository)
        addPacketViewModel = ViewModelProvider(this, viewModelFactory).get(AddPacketViewModel::class.java)
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
                val code = binding.trackEditText.text.toString()
                val observation = binding.descriptionEditText.text.toString()

                if (validateInputs(code, observation)) {
                    addPacketViewModel.handleCheckOK(code.toUpperCase(), observation)
                    binding.progressBar.visibility = View.VISIBLE
                    getSnack(getString(R.string.tracking_product))
                            ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                            ?.show()
                }
                else {
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
                    findNavController().popBackStack()
                }
                else {
                    getSnack(getString(R.string.track_add_fail))
                            ?.setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.errorColor))
                            ?.show()
                }
                addPacketViewModel.handleAddEventFinish()

            }
        })
        
        return binding.root
    }

    fun getSnack(string: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar? {
        val activity = activity
        if (activity is MainActivity)
            return activity.getSnackInstance(string, duration)
        return null
    }


    private fun validateInputs(code: String, observation: String): Boolean {
        var error = false

        // Check code
        if (!Correios.isValidCode(code.toString())) {
            binding.trackEditText.error = getString(R.string.add_code_error_message)
            error = true
        }
        else
            binding.trackEditText.error = null

        // Check product name
        if (observation.isEmpty()) {
            binding.descriptionEditText.error = getString(R.string.add_description_error_code)
            error = true
        }
        else
            binding.descriptionEditText.error = null

        return !error
    }

    override fun onDestroyView() {
        uiViewModel.setBottomNavVisibility(View.VISIBLE)
        super.onDestroyView()
    }
}
