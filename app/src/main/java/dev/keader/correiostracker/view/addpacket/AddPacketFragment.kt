package dev.keader.correiostracker.view.addpacket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dev.keader.correiosapi.Correios
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.database.TrackingDatabase
import dev.keader.correiostracker.databinding.FragmentAddPacketBinding
import dev.keader.correiostracker.extensions.hideKeyboard
import dev.keader.correiostracker.extensions.showKeyboard
import java.lang.ref.WeakReference

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
        val viewModelFactory = AddPacketViewModelFactory(db)
        addPacketViewModel = ViewModelProvider(this, viewModelFactory).get(AddPacketViewModel::class.java)
        binding.addPacketViewModel = addPacketViewModel

        binding.trackEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!Correios.isValidCode(binding.trackEditText.text.toString()))
                    binding.trackEditText.error = getString(R.string.add_code_error_message)
                else
                    binding.trackEditText.error = null
            }
        }

        binding.descriptionEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (binding.descriptionEditText.text.isEmpty())
                    binding.descriptionEditText.error = getString(R.string.add_description_error_code)
                else
                    binding.descriptionEditText.error = null
            }
        }

        // Keyboard and Focus
        lifecycle.addObserver(EditTextKeyboardLifecycleObserver(WeakReference(binding.trackEditText)))

        addPacketViewModel.eventCancelButtonNavigation.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().popBackStack()
                addPacketViewModel.handleCancelButtonEventFinish()
            }
        })

        // Sanity check, triggered by OK button
        addPacketViewModel.eventCheckInputs.observe(viewLifecycleOwner, Observer { checkInputs ->
            if (checkInputs) {
                val code = binding.trackEditText.text
                val observation = binding.descriptionEditText.text

                if (observation.isNotEmpty() &&  Correios.isValidCode(code.toString())) {
                    addPacketViewModel.handleCheckOK(code.toString().toUpperCase(), observation.toString())
                    binding.progressBar.visibility = View.VISIBLE
                    Snackbar.make(binding.cardViewOk, "Rastreando produto...", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                            .show()
                }
                else {
                    Snackbar.make(binding.cardViewOk, getString(R.string.add_product_error_message), Snackbar.LENGTH_LONG)
                            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.errorColor))
                            .show()
                    addPacketViewModel.handleCheckFail()
                }
            }
        })

        // Return of API
        addPacketViewModel.eventAddTrack.observe(viewLifecycleOwner, Observer { success ->
            if (success != null) {
                binding.progressBar.visibility = View.GONE

                if (success) {
                    Snackbar.make(binding.cardViewOk, getString(R.string.track_add_success), Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.sucessColor))
                            .show()
                    findNavController().popBackStack()
                }
                else {
                    Snackbar.make(binding.cardViewOk, getString(R.string.track_add_fail), Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.errorColor))
                            .show()
                }
                addPacketViewModel.handleAddEventFinish()

            }
        })
        
        return binding.root
    }

    override fun onDestroyView() {
        uiViewModel.setBottomNavVisibility(View.VISIBLE)
        super.onDestroyView()
    }
}

class EditTextKeyboardLifecycleObserver(private val editText: WeakReference<EditText>) :
        LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun openKeyboard() {
        editText.get()?.postDelayed({ editText.get()?.showKeyboard() }, 100)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun closeKeyboard() {
        editText.get()?.hideKeyboard()
    }
}