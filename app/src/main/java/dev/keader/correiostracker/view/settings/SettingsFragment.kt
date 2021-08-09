package dev.keader.correiostracker.view.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.keader.correiostracker.MainActivity
import dev.keader.correiostracker.R
import dev.keader.correiostracker.UIViewModel
import dev.keader.correiostracker.databinding.FragmentSettingsBinding
import dev.keader.correiostracker.model.PreferencesManager
import javax.inject.Inject


@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val uiViewModel: UIViewModel by activityViewModels()
    @Inject
    lateinit var preferences: PreferencesManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.settingsViewModel = settingsViewModel

        // Get current values
        val savedPosition = preferences.getFrequencyPosition()
        binding.switchAutomove.isChecked = preferences.getAutoMove()
        binding.switchTheme.isChecked = preferences.getDarkTheme()

        // Configure Spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.frequency_array,
                android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFrequency.adapter = spinnerAdapter
        binding.spinnerFrequency.setSelection(savedPosition)

        binding.switchAutomove.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.saveAutoMove(isChecked)
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.saveDarkTheme(isChecked)
        }

        binding.spinnerFrequency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Ignore first call, it's caused by default value
                if (position != savedPosition)
                    settingsViewModel.saveFrequency(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                parent?.setSelection(savedPosition)
            }
        }

        binding.lifecycleOwner = this
        return binding.root
    }

    private fun getSnack(string: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar? {
        val activity = activity
        if (activity is MainActivity)
            return activity.getSnackInstance(string, duration)
        return null
    }
}
